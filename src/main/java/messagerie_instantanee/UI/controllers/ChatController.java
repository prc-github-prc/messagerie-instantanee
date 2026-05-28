package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messagerie_instantanee.UI.App;
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
                return salon.getNom_discussion().toLowerCase().contains(recherche);
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
        rafraichirSalons();
        try {
            List<Discussion> lst = server.listerSalons();
            if (lst != null && !lst.isEmpty()) {
                tousLesSalons.setAll(lst);
                Platform.runLater(() -> {
                    if (usernameLink != null) usernameLink.setText(pseudo);
                    if (titreLabel   != null) titreLabel.setText("Bienvenue " + pseudo + " !");
                    if (tagsLabel    != null) tagsLabel.setText("Choisis un salon à gauche pour commencer à discuter");
                    setChatVisible(false);
                });
            } else {
                currentSalon = null;
                titreLabel.setText("Aucun salon disponible");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
            tousLesSalons.setAll(lst);
            List<Discussion> masque = serveur.getDiscussionsHidedByUser(pseudo);
            masque.addAll(serveur.getPrivateDiscussionsNotVisibleByUser(pseudo)); //REMOVE ME (le filtre se fait dans listerSalon)
            salonsFiltres.setPredicate(s -> !masque.contains(s)); 
        } catch (RemoteException e) {
            System.err.println("[ChatController] Erreur rafraîchissement : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ création

    // fonction appeler depuis la menu bar
    public void createNewSalonFromMenu() { ouvrirDialogNouveauSalon(); }
    public void createNewTagFromMenu()   { ouvrirDialogNouveauTag();   }

    @FXML private void createNewSalon(ActionEvent e) { ouvrirDialogNouveauSalon(); }
    @FXML private void createNewTag(ActionEvent e)   { ouvrirDialogNouveauTag();   }

    // permet de creer un salon
    private void ouvrirDialogNouveauSalon() {
        if (serveur == null) {
            showAlert(AlertType.ERROR, "Erreur serveur",
                "Impossible de créer un salon : le serveur n'est pas initialisé.");
            return;
        }
        //pop up pour les parametre du salon
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Salon");
        dialog.setContentText("Nom du salon :");
        dialog.showAndWait().ifPresent(raw -> {
            String nom = raw.trim();
            if (nom.isEmpty()) return;
            boolean existe = tousLesSalons.stream()
                .anyMatch(d -> nom.equals(d.getNom_discussion()));
            if (existe) {
                showAlert(AlertType.WARNING, "Erreur", "Ce salon existe déjà.");
                return;
            }
            try {
                Discussion nouveau = serveur.creationSalon(nom, pseudo, false);
                tousLesSalons.add(nouveau);
                searchField.clear();
                salonList.getSelectionModel().select(nouveau);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    // permet dde créer un tag
    private void ouvrirDialogNouveauTag() {
        if (currentSalon == null) {
            showAlert(AlertType.INFORMATION, "Information",
                "Veuillez sélectionner un salon avant d'ajouter un tag.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau tag");
        dialog.setContentText("Nom du tag :");
        dialog.showAndWait().ifPresent(tag -> {
            String t = tag.trim();
            if (t.isEmpty()) return;
            if (!t.startsWith("#")) t = "#" + t;
            String actuel = tagsLabel.getText();
            tagsLabel.setText((actuel == null || actuel.isEmpty()) ? t : actuel + " " + t);
        });
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
                rafraichirSalons();
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
}
