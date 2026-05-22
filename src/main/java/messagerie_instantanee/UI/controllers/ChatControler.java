package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;
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
import javafx.scene.layout.VBox;
import messagerie_instantanee.client.Client;
import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;

/**
 * Représente un chatcontroler.
 */
public class ChatControler {

    @FXML private ListView<InterfaceSujetDiscussion> salonList;
    @FXML private Label salonLabel;  //la liste des salon
    @FXML private VBox messagesBox;  
    @FXML private ScrollPane scrollPane;  //le layout des message
    @FXML private TextField inputField; //entrée de l'utilisateur
    @FXML private VBox headerChatBox; // partie haute du layout de message
    @FXML private Label tagsLabel;  //laebl de tag
    @FXML private Label titreLabel; //label de titre.

    // InterfaceServeurForum au lieu de Server
    private InterfaceServeurForum serveur;
    private InterfaceSujetDiscussion currentSalon;
    private Client clientRMI;
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
            List<InterfaceSujetDiscussion> lst_salon = server.listerSalons();
            for (InterfaceSujetDiscussion discu : lst_salon) {
                salonList.getItems().add(discu); //RECHECK chepa comment ça marche
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try{
            currentSalon = salonList.getItems().getFirst();
        } catch(NoSuchElementException e){
            currentSalon = null;
            System.out.println("il n'y a pas de salon a selectionner");
        }

        // salonList.getSelectionModel().selectedItemProperty().addListener((observable, ancienSalon, nouveauSalon) -> {
        //     if (nouveauSalon != null) {
        //         Platform.runLater(() -> {
        //             tagsLabel.setText("#discussion");
        //             titreLabel.setText(nouveauSalon.getNom_discussion());
        //             System.out.println("Affichage salon");
        //             System.out.println("Création tag");
        //         });
        //     }
        // });
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
        InterfaceSujetDiscussion clicked = salonList.getSelectionModel().getSelectedItem();
        currentSalon = clicked;
    }

    @FXML
    public void actionEnvoi() {        
        String texte = inputField.getText().trim();
        if (texte == null) {
            return;
        }
        try {
            currentSalon.diffuse(texte, pseudo);
        } catch (RemoteException e) {
            throw new RuntimeException("Un problème est arrivé lors de la diffusion du message" + e.getMessage());
        }
        // nouveauMessage.getStyleClass().add("bulle-message");
        // nouveauMessage.setWrapText(true);
        // nouveauMessage.setMaxWidth(300);
        // messagesBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        // messagesBox.setAlignment(Pos.CENTER_RIGHT);
        // messagesBox.getChildren().add(nouveauMessage);
        // inputField.clear();
    }

    @FXML
    private void createNewTag(ActionEvent event) {
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
    private void doNothings(){
        return;
    }

    @FXML
    private void createNewSalon(ActionEvent event) {
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
        String nom_Salon = nomSalon.trim();
        
        if (!nom_Salon.isEmpty()) {
            if (salonList.getItems().contains(nom_Salon)) { //FIXME c'est pas si simple
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Ce salon existe déjà ");
                alert.showAndWait();
            } else {
                try{
                    InterfaceSujetDiscussion nouveauSalon = serveur.creationSalon(nom_Salon, pseudo, false);
                    salonList.getItems().add(nouveauSalon);
                    salonList.getSelectionModel().select(nouveauSalon);
                    currentSalon = nouveauSalon;
                    System.out.println("Creation du salon"); //TODO remove me ligne de debug
                } catch (Exception e){
                    //TODO mettre l'erreur display quand y'en aura un
                    System.out.println(e.getMessage());
                }
            }
        }}); 
    }
}
