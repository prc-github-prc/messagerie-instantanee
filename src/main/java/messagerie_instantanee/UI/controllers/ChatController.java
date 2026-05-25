package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
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
    @FXML private TextField            searchField;   // barre de recherche
    @FXML private Label                tagsLabel;
    @FXML private Label                titreLabel;
    @FXML private Button               usernameLink;
    @FXML private Button inscrireBtn;


    @FXML private MenuBarController menuBarController;

    private InterfaceServeurForum    serveur;
    private InterfaceSujetDiscussion currentSalon;
    private Client                   clientRMI;
    private String                   pseudo;
    private boolean estInscrit = false;
    private Set<String> salonsInscrits = new HashSet<>();
    private Discussion discussionActuelle; 


    // Liste source (tous les salons) + vue filtrée branchée sur la ListView
    private final ObservableList<Discussion> tousLesSalons = FXCollections.observableArrayList();
    private FilteredList<Discussion>         salonsFiltres;

    // ------------------------------------------------------------------ lifecycle

    @FXML
    public void initialize() {
        menuBarController.setParent(this);

        // 1. Crée la FilteredList à partir de la liste source
        salonsFiltres = new FilteredList<>(tousLesSalons, s -> true); // prédicat initial : tout afficher

        // 2. Branche la liste filtrée sur la ListView (à la place de getItems())
        salonList.setItems(salonsFiltres);

        // 3. Écoute les changements dans le champ de recherche
        searchField.textProperty().addListener((obs, ancien, nouveau) -> {
            String recherche = nouveau == null ? "" : nouveau.trim().toLowerCase();
            salonsFiltres.setPredicate(salon -> {
                if (recherche.isEmpty()) return true; // champ vide → tout afficher
                return salon.getNom_discussion().toLowerCase().contains(recherche);
            });
        });

        // 4. Sélection d'un salon dans la liste
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

         salonList.setOnContextMenuRequested(event -> {
            ContextMenu contextMenu = new ContextMenu();
            Discussion selected = salonList.getSelectionModel().getSelectedItem();
            estInscrit = salonsInscrits.contains(selected.getNom_discussion());
            if (selected == null) return;
            if(estInscrit){
                MenuItem mnuQuitter = new MenuItem("Se désinscrire");
                mnuQuitter.setOnAction(e -> actionToggleInscription());
                contextMenu.getItems().add(mnuQuitter);
                
            }
            else{
                MenuItem mnuInscrire = new MenuItem("S'inscire");
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
                    () -> afficherBulle(msg.getContenu(), msg.getAuthorName().equals(pseudo))),
                pseudo);
            App.setClientRMI(clientRMI);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            List<Discussion> lst = server.listerSalons();
            if (lst != null && !lst.isEmpty()) {
                tousLesSalons.setAll(lst);   // alimente la liste SOURCE (pas getItems())
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
        this.discussionActuelle = d;
        if (currentSalon != null && clientRMI != null) {
            currentSalon.desinscription(clientRMI);
        }
        currentSalon = serveur.obtientSujet(d.getNom_discussion());
        estInscrit = salonsInscrits.contains(d.getNom_discussion());
        if(estInscrit){
            inscrireBtn.setText("Quitter le salon");
            inputField.setDisable(false);
        }
        else{
            inscrireBtn.setText("S'inscrire");
            inputField.setDisable(true);
        }
        currentSalon.inscription(clientRMI);
        titreLabel.setText("# " + d.getNom_discussion());
        messagesBox.getChildren().clear();
        utils_loadMessages(currentSalon.getArchive());
    }

    private void utils_loadMessages(Stack<Message> messages) {
        while (!messages.isEmpty()) {
            Message m = messages.pop();
            afficherBulle(m.getContenu(), m.getAuthorName().equals(pseudo));
        }
    }

    // ------------------------------------------------------------------ rafraîchir

    public void rafraichirSalons() {
        if (serveur == null) return;
        try {
            List<Discussion> lst = serveur.listerSalons();
            tousLesSalons.setAll(lst);   // la FilteredList se met à jour automatiquement
        } catch (RemoteException e) {
            System.err.println("[ChatController] Erreur rafraîchissement : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ création

    public void createNewSalonFromMenu() { ouvrirDialogNouveauSalon(); }
    public void createNewTagFromMenu()   { ouvrirDialogNouveauTag();   }

    @FXML private void createNewSalon(ActionEvent e) { ouvrirDialogNouveauSalon(); }
    @FXML private void createNewTag(ActionEvent e)   { ouvrirDialogNouveauTag();   }

    private void ouvrirDialogNouveauSalon() {
        if (serveur == null) {
            showAlert(AlertType.ERROR, "Erreur serveur",
                "Impossible de créer un salon : le serveur n'est pas initialisé.");
            return;
        }
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
                tousLesSalons.add(nouveau);           // ajout dans la source → visible si filtre OK
                searchField.clear();                  // réinitialise la recherche pour voir le nouveau salon
                salonList.getSelectionModel().select(nouveau);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

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

    // ------------------------------------------------------------------ envoi message

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

    // ------------------------------------------------------------------ affichage

    private void afficherBulle(String msg, boolean estMoi) {
        Label label = new Label(msg);
        label.getStyleClass().add("bulle-message");
        label.setWrapText(true);
        label.setMaxWidth(300);
        VBox conteneur = new VBox(label);
        conteneur.setAlignment(estMoi ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messagesBox.getChildren().add(conteneur);
    }

    private void setChatVisible(boolean visible) {
        if (scrollPane != null) { scrollPane.setVisible(visible); scrollPane.setManaged(visible); }
        if (inputField != null && inputField.getParent() != null) {
            inputField.getParent().setVisible(visible);
            inputField.getParent().setManaged(visible);
        }
        if (tagsLabel != null) { tagsLabel.setVisible(true); tagsLabel.setManaged(true); }
    }

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
            if (estInscrit) {
                currentSalon.desinscription(clientRMI);
                salonsInscrits.remove(nom);
                //currentSalon = null;
                estInscrit = false;
                inscrireBtn.setText("S'inscrire"); 
                inputField.setDisable(true);
                inputField.setPromptText("Inscrivez-vous pour écrire...");
            } else {
                currentSalon.inscription(clientRMI);
                salonsInscrits.add(nom);
                estInscrit = true;
                inscrireBtn.setText("Quitter le salon"); 
                inputField.setDisable(false);
                inputField.setPromptText("Écrire un message...");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML private void doNothings() { /* placeholder */ }
}
