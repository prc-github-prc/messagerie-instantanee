package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import messagerie_instantanee.client.Client;
import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.Server;
import messagerie_instantanee.server.models.Discussion;
import static messagerie_instantanee.server.services.ServiceServer.discussionToSalon;

/**
 * Représente un chatcontroler.
 */
public class ChatControler {

    @FXML private ListView<Discussion> salonList;
    /**
     * liste des labels de salon.
     */
    @FXML private Label salonLabel;
    /**
     * boite de messages.
     */
    @FXML private VBox messagesBox;
    /**
     * scrollPane.
     */
    @FXML private ScrollPane scrollPane;
    /**
     * entrée utilisateur.
     */
    @FXML private TextField inputField;
    /**
     * headerChatbox.
     */
    @FXML private VBox headerChatBox; 
    /**
     * label de tag.
     */
    @FXML private Label tagsLabel;    
    /**
     * label de titre.
     */
    @FXML private Label titreLabel;

    // ✅ InterfaceServeurForum au lieu de Server
    private InterfaceServeurForum serveur;
    private InterfaceSujetDiscussion salonCourant;
    /**
     * rmi du client.
     */
    private Client clientRMI;
    /**
     * pseudo de l'utilisateur.
     */
    private String pseudo;

    @FXML
    public void initialize(InterfaceServeurForum server, String pseudo) {
        this.pseudo = pseudo;
        this.serveur = server;

        try {
            clientRMI = new Client(msg ->
                Platform.runLater(() -> afficherBulle(msg, false))
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            List<Discussion> lst_discussion = server.listerSalons();
            for (Discussion discu : lst_discussion) {
                salonList.getItems().add(discu);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        salonList.getSelectionModel().selectedItemProperty().addListener((observable, ancienSalon, nouveauSalon) -> {
            if (nouveauSalon != null) {
                Platform.runLater(() -> {
                    tagsLabel.setText("#discussion");
                    titreLabel.setText(nouveauSalon.getNom_discussion());
                    System.out.println("Affichage salon");
                    System.out.println("Création tag");
                });
            }
        });
    }

    private void afficherBulle(String msg, boolean estMoi) {
        Label message = new Label(msg);
        message.getStyleClass().add("bulle-message");
        message.setWrapText(true);
        message.setMaxWidth(300);
        VBox conteneur = new VBox(message);
        if (estMoi) {
            conteneur.setAlignment(Pos.CENTER_LEFT);
        } else {
            conteneur.setAlignment(Pos.CENTER_RIGHT);
        }
        messagesBox.getChildren().add(conteneur);
    }

    @FXML
    public void currentSalon(MouseEvent event){
        Discussion clicked = salonList.getSelectionModel().getSelectedItem();
        salonCourant = discussionToSalon(clicked);
    }

    @FXML
    public void actionEnvoi() {        
        String texte = inputField.getText();
        if (texte != null && !texte.trim().isEmpty()) {
            Label nouveauMessage = new Label(texte);
            try {
                salonCourant.diffuse(texte, pseudo);
            } catch (RemoteException e) {
                throw new RuntimeException("Un problème est arrivé lors de la diffusion du message" + e.getMessage());
            }
            nouveauMessage.getStyleClass().add("bulle-message");
            nouveauMessage.setWrapText(true);
            nouveauMessage.setMaxWidth(300);
            messagesBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            messagesBox.setAlignment(Pos.CENTER_RIGHT);
            messagesBox.getChildren().add(nouveauMessage);
            inputField.clear();
        }
    }

    @FXML
    private void handleAjouterTag(ActionEvent event) {
        if (tagsLabel == null || titreLabel.getText().equals("# Sélectionnez un salon")) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un salon avant d'ajouter un tag ");
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
                if (!tagNettoye.startsWith("#")) {
                    tagNettoye = "#" + tagNettoye;
                }
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
    private void handleCreerDiscussion(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle discussion");
        dialog.setContentText("Nom de la discussion :");
        Optional<String> result = dialog.showAndWait();
    }

    @FXML
    private void handleCreerSalon(ActionEvent event) {
        if (serveur == null) {
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
        String nomNettoye = nomSalon.trim();
        
        if (!nomNettoye.isEmpty()) {
            if (salonList.getItems().contains(nomNettoye)) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Ce salon existe déjà ");
                alert.showAndWait();
            } else {
                Discussion nouveauSalon = ((Server) serveur).creationSalon(nomNettoye, pseudo, false);
                salonList.getItems().add(nouveauSalon);
                salonList.getSelectionModel().select(nouveauSalon);
                System.out.println("Création du salon");
            }
        }}); 
    }

    @FXML
    private void handleCreerServeur(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Serveur");
        dialog.setContentText("Nom du serveur :");
        Optional<String> result = dialog.showAndWait();
    }
}
