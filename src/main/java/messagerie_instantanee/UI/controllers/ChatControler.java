package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import messagerie_instantanee.client.Client;
import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Message;

/**
 * Représente un chatcontroler.
 */
public class ChatControler {

    @FXML private ListView<Discussion> salonList; //la liste des salon
    @FXML private VBox messagesBox;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField inputField;
    @FXML private VBox headerChatBox;
    @FXML private Label tagsLabel;
    @FXML private Label titreLabel;
    @FXML private Button usernameLink;

    private InterfaceServeurForum serveur;
    private InterfaceSujetDiscussion currentSalon;
    private Client clientRMI;
    private String pseudo;

    // ====================== lance un event listener sur la liste des salon ======================
    @FXML
    public void initialize() {
        salonList.getItems().clear();
        salonList.getSelectionModel().selectedItemProperty().addListener((observable, ancienSalon, nouveauSalon) -> {
            if (nouveauSalon != null && serveur != null) {
                try {
                    rejoindre(nouveauSalon.getNom_discussion());

                    Platform.runLater(() -> {
                        setChatVisible(true);
                        if (tagsLabel != null) {
                            tagsLabel.setText("#discussion");
                        }
                        System.out.println("Salon : " + nouveauSalon.getNom_discussion());
                    });
                } catch (RemoteException e) {
                    System.err.println("Erreur lors du clic : " + e.getMessage());
                }
            }
        });
    }

    // ================================= Setup les parametres du controller =================================
    public void configurerSession(InterfaceServeurForum server, String pseudo) {
        this.pseudo = pseudo;
        this.serveur = server;

        // crée une instance du client et lui permet d'afficher des messages
        try {
            clientRMI = new Client(msg ->
                Platform.runLater(() -> afficherBulle(msg.getContenu(), msg.getAuthorName().equals(pseudo))),
            pseudo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // feed back utilisateur
        try {
            List<Discussion> lst_salon = server.listerSalons();
            if (lst_salon != null && !lst_salon.isEmpty()) {
                salonList.getItems().addAll(lst_salon);
                Platform.runLater(() -> {
                    if (usernameLink != null) usernameLink.setText(pseudo);
                    if (titreLabel != null) titreLabel.setText("Bienvenue " + pseudo + " !");
                    if (tagsLabel != null) tagsLabel.setText("Choisis un salon à gauche pour commencer à discuter");
                    // permet d'ecrire un message une fois un salon selectionnee
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

    // ====================== gere l'inscription et la desinscription à un salon =======================
    private void rejoindre(String titre) throws RemoteException{
        // désinscrit du salon précédent si on en avait un
        if (currentSalon != null && clientRMI != null) {
            currentSalon.desinscription(clientRMI);
        }
        currentSalon = serveur.obtientSujet(titre);
        // inscrit au nouveau salon une seule fois ici
        currentSalon.inscription(clientRMI);
        titreLabel.setText("# " + titre);
        messagesBox.getChildren().clear();
        utils_laodMessages(currentSalon.getArchive());
    }

    // affiche un lot de message
    private void utils_laodMessages(Queue<Message> queue_message){
        while (!queue_message.isEmpty()) {
            Message message = queue_message.remove();
            afficherBulle(message.getContenu(), message.getAuthorName() == pseudo);
        }
    }

    // ==================================== met en forme un message =====================================
    private void afficherBulle(String msg, boolean estMoi) {
        Label message = new Label(msg);
        message.getStyleClass().add("bulle-message");
        message.setWrapText(true);
        message.setMaxWidth(300);
        VBox conteneur = new VBox(message);
        if (estMoi) {
            conteneur.setAlignment(Pos.CENTER_RIGHT);
        } else {
            conteneur.setAlignment(Pos.CENTER_LEFT);
        }
        messagesBox.getChildren().add(conteneur);
    }

    @FXML
    public void getCurrentSalon(MouseEvent event) {
        Discussion clicked = salonList.getSelectionModel().getSelectedItem();
        if (clicked == null) return;
        try {
            currentSalon = serveur.obtientSujet(clicked.getNom_discussion());
        } catch (RemoteException e) {
            throw new RuntimeException("Impossible de récupérer le salon distant : " + e.getMessage(), e);
        }
    }

    // ====================================== envoie un message ==================================
    @FXML
    public void actionEnvoi() {
        String texte = inputField.getText().trim();

        if (texte.isEmpty()) return;

        if (currentSalon == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aucun salon sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un salon avant d'envoyer un message.");
            alert.showAndWait();
            return;
        }

        if (clientRMI == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Client non initialisé");
            alert.setHeaderText(null);
            alert.setContentText("Pas de client RMI.");
            alert.showAndWait();
            return;
        }

        try {
            currentSalon.diffuse(texte, pseudo);
            inputField.clear();
        } catch (RemoteException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur d'envoi");
            alert.setHeaderText(null);
            alert.setContentText("Un problème est arrivé lors de la diffusion : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void createNewTag(ActionEvent event) {
        if (currentSalon == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un salon avant d'ajouter un tag.");
            alert.showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau tag");
        dialog.setContentText("Nom du tag :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tag -> {
            String tagNettoye = tag.trim();
            if (!tagNettoye.isEmpty()) {
                if (!tagNettoye.startsWith("#")) tagNettoye = "#" + tagNettoye;
                String tagsActuels = tagsLabel.getText();
                if (tagsActuels == null || tagsActuels.isEmpty()) {
                    tagsLabel.setText(tagNettoye);
                } else {
                    tagsLabel.setText(tagsActuels + " " + tagNettoye);
                }
            }
        });
    }

    @FXML
    private void doNothings() {
        // placeholder boutons non reliés
    }

    // ====================== crée un nouveaux salon (et le selctione auto) ====================
    @FXML
    private void createNewSalon(ActionEvent event) {
        if (serveur == null) {
            System.out.println(serveur); // REMOVE ME print de debug
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur serveur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de créer un salon : le serveur n'est pas initialisé.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Salon");
        dialog.setContentText("Nom du salon :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nomSalon -> {
            String nom_Salon = nomSalon.trim();
            if (!nom_Salon.isEmpty()) {
                boolean existe = salonList.getItems().stream()
                    .anyMatch(d -> nom_Salon.equals(d.getNom_discussion()));
                if (existe) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Ce salon existe déjà.");
                    alert.showAndWait();
                } else {
                    try {
                        Discussion nouveauSalon = serveur.creationSalon(nom_Salon, pseudo, false);
                        salonList.getItems().add(nouveauSalon);
                        salonList.getSelectionModel().select(nouveauSalon);
                        // rejoindre() est appelé automatiquement par le listener de salonList
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    private void setChatVisible(boolean visible) {
        if (scrollPane != null) {
            scrollPane.setVisible(visible);
            scrollPane.setManaged(visible);
        }
        if (inputField != null && inputField.getParent() != null) {
            inputField.getParent().setVisible(visible);
            inputField.getParent().setManaged(visible);
        }
        if (tagsLabel != null) {
            tagsLabel.setVisible(true);
            tagsLabel.setManaged(true);
        }
    }
}
