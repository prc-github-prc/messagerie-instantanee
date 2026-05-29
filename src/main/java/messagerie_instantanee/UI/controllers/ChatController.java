package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import messagerie_instantanee.UI.App;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.client.Client;
import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Message;

public class ChatController {

    @FXML private ListView<Discussion> salonList;
    @FXML private VBox                 messagesBox;
    @FXML private ScrollPane           scrollPane;
    @FXML private TextField            inputField;
    @FXML private TextField            searchField;
    @FXML private Label                tagsLabel;
    @FXML private Label                titreLabel;
    @FXML private Button               usernameLink;
    @FXML private Button               inscrireBtn;
    @FXML private Button               boutonInvitation;

    // Chargé programmatiquement dans initialize() — pas via @FXML
    private MenuBarController menuBarController;

    private InterfaceServeurForum    serveur;
    private InterfaceSujetDiscussion currentSalon;
    private Client                   clientRMI;
    private String                   pseudo;
    private boolean estInscritNotification = false;
    private Set<String> salonsInscritsNotification = new HashSet<>();
    private Discussion discussionActuelle;

    private final ObservableList<Discussion> tousLesSalons = FXCollections.observableArrayList();
    private FilteredList<Discussion>         salonsFiltres;

    // ------------------------------------------------------------------ lifecycle

    @FXML
    public void initialize() {

        // ================ permet l'insersion de la menu bar dans la chatview ==============
        try {
            FXMLLoader menuLoader = new FXMLLoader(
                getClass().getResource("/fxml/MenuBar.fxml"));
            MenuBar menuBarNode = menuLoader.load();
            menuBarController = menuLoader.getController();
            menuBarController.setParent(this);
            menuBarController.setDarkTheme(tagsLabel.getStyleClass().contains("dark-theme"));

            // Injecte la MenuBar dans le slot de la TitleBar
            if (App.titleBarController != null) {
                App.titleBarController.injectMenuBar(menuBarNode);
            }
        } catch (Exception e) {
            System.err.println("[ChatController] Impossible de charger MenuBar.fxml : " + e.getMessage());
            e.printStackTrace();
        }

        // ============= permet de filtrer les discussion afficher selon la bar de recherche =============

        final List<Discussion> masqueFinal = new ArrayList<>(); //Java exige qu'une variable utilisée dans un lambda soit finale

        salonsFiltres = new FilteredList<>(tousLesSalons, s -> !masqueFinal.contains(s));
        salonList.setItems(salonsFiltres);

        searchField.textProperty().addListener((obs, ancien, nouveau) -> {
            String recherche = nouveau == null ? "" : nouveau.trim().toLowerCase();
            
            salonsFiltres.setPredicate(salon -> {
                if (recherche.isEmpty()) return true;
                return normaliserTexte(salon.getNom_discussion().toLowerCase()).contains(recherche);
            });
        });

        // ============== event listener qui gere le click sur un salon ============
        salonList.getSelectionModel().selectedItemProperty().addListener(
            (obs, ancien, nouveau) -> {
                if (nouveau != null && serveur != null) {
                    try {
                        rejoindre(nouveau);
                        Platform.runLater(() -> {
                            setChatVisible(true);
                            if (tagsLabel != null) tagsLabel.setText("#discussion");
                        });
                    } catch (RemoteException e) {
                        System.err.println("Erreur lors du clic : " + e.getMessage());
                    }
                }
            }
        );

        // even listener du bouton d'inscription au notification
        salonList.setOnContextMenuRequested(event -> {
            ContextMenu contextMenu = new ContextMenu();
            Discussion selected = salonList.getSelectionModel().getSelectedItem();
            estInscritNotification = salonsInscritsNotification.contains(selected.getNom_discussion());
            MenuItem mnuMasquer = new MenuItem("Masquer");
            mnuMasquer.setOnAction(e -> handleMasquerSalon(e));
            if (estInscritNotification) {
                MenuItem mnuQuitter = new MenuItem("Se désinscrire");
                mnuQuitter.setOnAction(e -> actionToggleInscription());
                contextMenu.getItems().add(mnuQuitter);
            } else {
                MenuItem mnuInscrire = new MenuItem("S'inscrire");
                mnuInscrire.setOnAction(e -> actionToggleInscription());
                contextMenu.getItems().add(mnuInscrire);
            }
            contextMenu.show(salonList, event.getScreenX(), event.getScreenY());
        });
    }

    // ------------------------------------------------------------------ session

    public void configurerSession(InterfaceServeurForum server, String pseudo) {
        this.pseudo  = pseudo;
        this.serveur = server;

        try {
            clientRMI = new Client(
                msg -> Platform.runLater(
                    () -> afficherBulle(msg)),
                pseudo);
            App.setClientRMI(clientRMI);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // === popule la list des salon ===
        //rafraichirSalons();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(rafraichirSalonsAuto, 0, 60, TimeUnit.SECONDS);

    }

    // ------------------------------------------------------------------ déconnexion

    public void deconnecter() {
        try {
            if (currentSalon != null && clientRMI != null) {
                currentSalon.desinscription(clientRMI);
            }
            if (clientRMI != null) {
                UnicastRemoteObject.unexportObject(clientRMI, true);
                clientRMI = null;
            }
        } catch (Exception e) {
            System.err.println("[ChatController] Erreur déconnexion : " + e.getMessage());
        }
        currentSalon = null;
        serveur      = null;
    }

    // ------------------------------------------------------------------ salon

    private void rejoindre(Discussion d) throws RemoteException {
        // si on était sur un salon, se désinscrit
        if (currentSalon != null && clientRMI != null) {
            currentSalon.desinscription(clientRMI);
        }
        
        // met a jour la discussion et le salon actuelle
        this.discussionActuelle = d;
        currentSalon = serveur.obtientSujet(d.getNom_discussion());
        estInscritNotification = salonsInscritsNotification.contains(d.getNom_discussion()); //TODO modifier quand les notif seront implementer
        if (estInscritNotification) {
            inscrireBtn.setText("Ne plus suivre le salon");
        } else {
            inscrireBtn.setText("Suivre le salon");
        }
        currentSalon.inscription(clientRMI);
        titreLabel.setText("# " + d.getNom_discussion());
        messagesBox.getChildren().clear();
        System.out.println(discussionActuelle.getNom_discussion()+"=================================");// TODO à enlever
        boutonInvitation.setVisible(discussionActuelle.getPrive());
        utils_loadMessages(currentSalon.getArchive());
    }

    private void utils_loadMessages(Stack<Message> messages) {
        while (!messages.isEmpty()) {
            Message m = messages.pop();
            afficherBulle(m);
        }
    }

    // ================ rafraîchir les salon ================

    public void rafraichirSalons() {
        if (serveur == null) return;
        try {
            List<Discussion> lst = serveur.listerSalons();
            //cretaion du masque
            List<Discussion> masque = serveur.getDiscussionsHidedByUser(pseudo);
            masque.addAll(serveur.getPrivateDiscussionsNotVisibleByUser(pseudo)); //REMOVE ME (le filtre se fait dans listerSalon)
            //application du masque
            lst.removeAll(masque);
            if (lst == null || lst.isEmpty()) {
                currentSalon = null;
                discussionActuelle = null;
                titreLabel.setText("Aucun salon disponible");
                return;
            }
            boutonInvitation.setVisible(false);
            tousLesSalons.setAll(lst);
            Platform.runLater(() -> {
                if (usernameLink != null) usernameLink.setText(pseudo);
                if (titreLabel   != null) titreLabel.setText("Bienvenue " + pseudo + " !");
                if (tagsLabel    != null) tagsLabel.setText("Choisis un salon à gauche pour commencer à discuter");
                setChatVisible(false);
            });
        } catch (RemoteException e) {
            System.err.println("[ChatController] Erreur rafraîchissement : " + e.getMessage());
        }
    }

    Runnable rafraichirSalonsAuto = new Runnable(){
        public void run(){
            rafraichirSalons();
        }
    };

    // ------------------------------------------------------------------ création

    // fonction appeler depuis la menu bar
    public void createNewSalonFromMenu() { ouvrirDialogNouveauSalon(); }
    public void createNewTagFromMenu()   { ouvrirDialogNouveauTag();   }

    @FXML private void createNewSalon(ActionEvent e) { ouvrirDialogNouveauSalon(); }
    @FXML private void createNewTag(ActionEvent e)   { ouvrirDialogNouveauTag();   }
    @FXML private void invitation(ActionEvent e){ ouvrirDialogInvitation();}

    // permet de creer un salon
    private void ouvrirDialogNouveauSalon() {
        if (serveur == null) {
            showAlert(AlertType.ERROR, "Erreur serveur",
                "Impossible de créer un salon : le serveur n'est pas initialisé.");
            return;
        }
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner((Stage) salonList.getScene().getWindow()); 
        dialog.setTitle("Nouveau Salon");
        dialog.setResizable(false);

        //------Contenu Formulaire
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);
        TextField tfsalon    = new TextField();
        tfsalon.getStyleClass().add("dialog-input");
        tfsalon.setPromptText("Nom du Salon");
        Label lbl = new Label("Nom du salon");
        lbl.getStyleClass().add("dialog-label");
        CheckBox cbPrive = new CheckBox("Salon privé ?");
        cbPrive.getStyleClass().add("dialog-checkbox");

        vbox.getChildren().addAll(
            lbl,
            tfsalon,
            cbPrive);
        

        // Boutons
        Button btnValider = new Button("Valider");
        Button btnAnnuler = new Button("Annuler");
        btnValider.setDefaultButton(true);
        btnAnnuler.setCancelButton(false);
        btnValider.getStyleClass().add("dialog-btn-valider");
        btnAnnuler.getStyleClass().add("dialog-btn-annuler");


        HBox boutons = new HBox(10, btnValider, btnAnnuler);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.setPadding(new Insets(0, 15, 15, 15));
        boutons.getStyleClass().add("dialog-footer");
        // Layout principal
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dialog-root");
        root.setCenter(vbox);
        root.setBottom(boutons);
        
        btnValider.setOnAction(e -> {
            String salon  = tfsalon.getText().trim();
            if (salon.isEmpty()) return;
            boolean existe = tousLesSalons.stream()
                .anyMatch(d -> salon.equals(d.getNom_discussion()));
            if (existe) {
                showAlert(AlertType.WARNING, "Erreur", "Ce salon existe déjà.");
                return;
            }
            Boolean estPrive= false;
            if (cbPrive.isSelected()) { 
                estPrive=true;
            }
            try {
                Discussion nouveau = serveur.creationSalon(salon, pseudo, estPrive);
                tousLesSalons.add(nouveau);
                searchField.clear();
                salonList.getSelectionModel().select(nouveau);
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
            }
            dialog.close();
        });

        btnAnnuler.setOnAction(e -> dialog.close());

        Scene scene = new Scene(root, 300, 200);
        scene.getStylesheets().addAll(salonList.getScene().getStylesheets());
        dialog.setScene(scene);
        dialog.showAndWait();

    }

    // permet dde créer un tag
    private void ouvrirDialogNouveauTag() {
        if (currentSalon == null) {
            showAlert(AlertType.INFORMATION, "Information",
                "Veuillez sélectionner un salon avant d'ajouter un tag.");
            return;
        }
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner((Stage) salonList.getScene().getWindow()); 
        dialog.setTitle("Nouveau Tag");
        dialog.setResizable(false);
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);
        TextField tfTag   = new TextField();
        tfTag.getStyleClass().add("dialog-input");
        tfTag.setPromptText("Nom du Tag");
        Label lbl = new Label("Nom du Tag");
        lbl.getStyleClass().add("dialog-label");

        vbox.getChildren().addAll(
            lbl,
            tfTag);

        // Boutons
        Button btnValider = new Button("Valider");
        Button btnAnnuler = new Button("Annuler");
        btnValider.setDefaultButton(true);
        btnAnnuler.setCancelButton(false);
        btnValider.getStyleClass().add("dialog-btn-valider");
        btnAnnuler.getStyleClass().add("dialog-btn-annuler");

        HBox boutons = new HBox(10, btnValider, btnAnnuler);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.setPadding(new Insets(0, 15, 15, 15));
        boutons.getStyleClass().add("dialog-footer");
        
        // Layout principal
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dialog-root");
        root.setCenter(vbox);
        root.setBottom(boutons);

        btnValider.setOnAction(e->{
            String tag  = tfTag.getText().trim();
            if (tag.isEmpty()) return;
            if (!tag.startsWith("#")) tag = "#" + tag;
            String actuel = tagsLabel.getText();
            tagsLabel.setText((actuel == null || actuel.isEmpty()) ? tag : actuel + " " + tag);
            dialog.close();
        });
        btnAnnuler.setOnAction(e -> dialog.close());

        Scene scene = new Scene(root, 300, 200);
        scene.getStylesheets().addAll(salonList.getScene().getStylesheets());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    //permet d'ajouter quelqu'un
    private void ouvrirDialogInvitation(){
        if (currentSalon == null) {
            showAlert(AlertType.INFORMATION, "Information",
                "Veuillez sélectionner un salon avant d'ajouter un tag.");
            return;
        }
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner((Stage) salonList.getScene().getWindow()); 
        dialog.setTitle("Invitation");
        dialog.setResizable(false);
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);
        TextField tfUsername   = new TextField();
        tfUsername.getStyleClass().add("dialog-input");
        tfUsername.setPromptText("Nom de l'utilisateur");
        Label lbl = new Label("username");
        lbl.getStyleClass().add("dialog-label");

        vbox.getChildren().addAll(
            lbl,
            tfUsername);

        // Boutons
        Button btnValider = new Button("Valider");
        Button btnAnnuler = new Button("Annuler");
        btnValider.setDefaultButton(true);
        btnAnnuler.setCancelButton(false);
        btnValider.getStyleClass().add("dialog-btn-valider");
        btnAnnuler.getStyleClass().add("dialog-btn-annuler");

        HBox boutons = new HBox(10, btnValider, btnAnnuler);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.setPadding(new Insets(0, 15, 15, 15));
        boutons.getStyleClass().add("dialog-footer");
        
        // Layout principal
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dialog-root");
        root.setCenter(vbox);
        root.setBottom(boutons);

        btnValider.setOnAction(e->{
            String username = tfUsername.getText().trim();
            try {
                serveur.addUserToDiscussion(discussionActuelle.getId_discussion(), username);
            } catch (RemoteException e1) {
                showAlert(AlertType.ERROR, "Erreur d'envoi",
                    "Problème lors de l'invitation : " + e1.getMessage());
        }
            dialog.close();
        });
        btnAnnuler.setOnAction(e -> dialog.close());

        Scene scene = new Scene(root, 300, 200);
        scene.getStylesheets().addAll(salonList.getScene().getStylesheets());
        dialog.setScene(scene);
        dialog.showAndWait();

    }

    // ========== envoi de message ========

    @FXML
    public void actionEnvoi() {
        String texte = inputField.getText().trim();
        if (texte.isEmpty()) return;
        if (currentSalon == null) {
            showAlert(AlertType.WARNING, "Aucun salon sélectionné",
                "Veuillez sélectionner un salon avant d'envoyer un message.");
            return;
        }
        if (clientRMI == null) {
            showAlert(AlertType.WARNING, "Client non initialisé", "Pas de client RMI.");
            return;
        }
        try {
            currentSalon.diffuse(texte, pseudo);
            inputField.clear();
        } catch (RemoteException e) {
            showAlert(AlertType.ERROR, "Erreur d'envoi",
                "Problème lors de la diffusion : " + e.getMessage());
        }
    }

    // =================== fonction utilitaire d'affichage ==================

    // affiche un message du bon cote selon auhtor ou pas
    private void afficherBulle(Message m) {
        boolean estMoi = m.getAuthorName().equals(pseudo);  
        VBox conteneur = new VBox(2);

        // pseudo au-dessus du message
        if (!estMoi) {
            Label pseudoLabel = new Label(m.getAuthorName());
            pseudoLabel.getStyleClass().add("bulle-pseudo");

            conteneur.getChildren().add(pseudoLabel);
        }

        // message
        Label messageLabel = new Label(m.getContenu());
        messageLabel.getStyleClass().add("bulle-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        conteneur.getChildren().add(messageLabel);

        // alignement
        conteneur.setAlignment(
            estMoi ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT
        );

        messagesBox.getChildren().add(conteneur);
    }

    // rend la bar d'ecriture de message visible
    private void setChatVisible(boolean visible) {
        if (scrollPane != null) { scrollPane.setVisible(visible); scrollPane.setManaged(visible); }
        if (inputField != null && inputField.getParent() != null) {
            inputField.getParent().setVisible(visible);
            inputField.getParent().setManaged(visible);
        }
        if (tagsLabel != null) { tagsLabel.setVisible(true); tagsLabel.setManaged(true); }
    }

    // permet d'afficher des alerts (sert surtout a factoriser le code)
    private void showAlert(AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    @FXML
    private void actionToggleInscription() {
        if (currentSalon == null) return;
        try {
            String nom = discussionActuelle.getNom_discussion();
            if (estInscritNotification) {
                // currentSalon.desinscription(clientRMI);
                salonsInscritsNotification.remove(nom);
                estInscritNotification = false;
                inscrireBtn.setText("S'inscrire");
                //inputField.setDisable(true);
                //inputField.setPromptText("Inscrivez-vous pour écrire...");
            } else {
                // currentSalon.inscription(clientRMI);
                salonsInscritsNotification.add(nom);
                estInscritNotification = true;
                inscrireBtn.setText("Quitter le salon");
                //inputField.setDisable(false);
                //inputField.setPromptText("Écrire un message...");
            }
        } catch(Exception e){ //TODO changer en RemoteException quand y'aura inscriptionNotif cote serv
            return;
        }
    }

    @FXML
    private void handleMasquerSalon(ActionEvent event){
        Discussion selected = salonList.getSelectionModel().getSelectedItem();
        if(selected != null){
            try{
                serveur.hideDiscussion(selected.getId_discussion(), this.pseudo);
                rafraichirSalons(); //TODO juste suprimer la discussion au lieux de tout recharger
                if(discussionActuelle != null){
                    messagesBox.getChildren().clear();
                    titreLabel.setText("# Sélectionnez un salon");
                    inputField.setDisable(true);
                }
            }
            catch(RemoteException e){
                System.err.println("[Client] Erreur masquage " + e.getMessage());
            }
            
        }
    }

    public void showHiddenChannels() {
        try {
            List<Discussion> masques = serveur.getDiscussionsHidedByUser(pseudo);
            if (masques.isEmpty()) {
                showAlert(AlertType.INFORMATION, "Salons", "Aucun salon masqué.");
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("Salons masqués");

            ListView<Discussion> listView = new ListView<>(FXCollections.observableArrayList(masques));
            Button btnRetablir = new Button("Rétablir le salon");

            btnRetablir.setOnAction(e -> {
                Discussion select = listView.getSelectionModel().getSelectedItem();
                if (select != null) {
                    try {
                        serveur.unhideDiscussion(select.getId_discussion(), pseudo);
                        rafraichirSalons(); 
                        listView.getItems().remove(select); 
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            VBox root = new VBox(10, new Label("Salons masqués :"), listView, btnRetablir);
            root.setAlignment(Pos.CENTER);
            stage.setScene(new Scene(root, 300, 400));
            stage.show();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // === fonction appeler par les bouton pas encore connecter
    @FXML private void doNothings() { /* placeholder */ }


    // === ouverture du profilf
    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = NavigationManager.getInstance()
                .naviguerVers("/fxml/ProfilView.fxml");

            ProfileController controller = loader.getController();
            controller.configurerProfil(serveur, pseudo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String normaliserTexte(String texte){
        if (texte == null) return "";
       String sansAccents = Normalizer.normalize(texte, Normalizer.Form.NFD)
                                   .replaceAll("\\p{M}", "");
        return sansAccents.toLowerCase().trim();
    }
}
