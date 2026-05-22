package messagerie_instantanee.UI.controllers;

import static messagerie_instantanee.server.services.ServiceServer.discussionToSalon;

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
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.Salon;
import messagerie_instantanee.server.Server;
import messagerie_instantanee.server.models.Discussion;

public class ChatControler {

    // ===== FXML ==================================
    @FXML private ListView<Discussion> salonList;
    @FXML private Label salonLabel;
    @FXML private VBox messagesBox;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField inputField;

    // ===== Etat ==================================
    private Server serveur;
    private InterfaceSujetDiscussion salonCourant;
    private Client clientRMI;
    private String pseudo;
    private Label titreLabel;
    private Label tags;

    @FXML
    public void initialize(Server server, String pseudo) {
        this.pseudo = pseudo;
        this.serveur = server;

        try {
            clientRMI = new Client(msg ->
                Platform.runLater(() -> afficherBulle(msg, false)) // fonction qui affiche un message
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // ======== charge tous les nom de discussion dans la barre lateral ==========
        List<Discussion> lst_discussion = server.listerSalons();
        for(Discussion discu :lst_discussion){
            salonList.getItems().add(discu);
        }

        // détecter quand l’utilisateur change de salon sélectionné dans la list view
            salonList.getSelectionModel().selectedItemProperty().addListener((observable, ancienSalon, nouveauSalon) -> {
            if (nouveauSalon != null) {
                Platform.runLater(() -> {
                    Label titre = new Label(nouveauSalon.getNom_discussion());
                    titre.setStyle("-fx-text-fill: #f2f3f5; -fx-font-weight: bold; -fx-font-size: 16px;");
                    
                    VBox enteteContenu = new VBox();
                    String simuleTags = "#discussion";
                    tags = new Label(simuleTags);
                    tags.setStyle("-fx-text-fill: #949ba4; -fx-font-size: 12px;");
                    
                    enteteContenu.getChildren().addAll(tags, titre);
                    
                    salonLabel.setText("");
                    salonLabel.setGraphic(enteteContenu);
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
        if(estMoi){
            conteneur.setAlignment(Pos.CENTER_LEFT);
        }
        else{
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
                throw  new RuntimeException("Un problème est arrivé lors de la diffusion du message"+e.getMessage());
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau tag");
        dialog.setContentText("Nom du tag :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tag -> {   
        String tagNettoye = tag.trim();
        
        if (!tagNettoye.isEmpty()) {
            if(!tagNettoye.startsWith("#")){
                tagNettoye = "#" + tagNettoye;
            }
            String tagsActuels = tags.getText();
            if(tagsActuels == null || tagsActuels.isEmpty()){
                tags.setText(tagNettoye);
            } else{
                tags.setText(tagsActuels + " " + tagNettoye);
            }

        }}); 
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Salon");
        dialog.setContentText("Nom du salon :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nomSalon -> {   
        String nomNettoye = nomSalon.trim();
        
        if (!nomNettoye.isEmpty()) {
            serveur.
            salonList.getItems().add(nomNettoye);
            salonList.getSelectionModel().select(nomNettoye);
        }}); 
    }

    @FXML
    private void handleCreerServeur(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Serveur");
        dialog.setContentText("Nom du serveur :");
        Optional<String> result = dialog.showAndWait();
    }

    /*private void messageSystem(String texte) {
        Label labelSys = new Label(texte);
        Platform.runLater(() -> messagesBox.getChildren().add(labelSys));
    }*/
}
