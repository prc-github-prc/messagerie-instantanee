package messagerie_instantanee.UI;

import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

public class ChatControle {
    @FXML private ListView<String> salonList;
    @FXML private Label salonLabel;
    @FXML private VBox messagesBox;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField inputField;
    
    @FXML
    public void initialize() {
        salonList.getItems().addAll("Jeux vidéo", "Musique", "Cinéma");

        salonList.getSelectionModel().selectedItemProperty().addListener((observable, ancienSalon, nouveauSalon) -> {
            if (nouveauSalon != null) {
                salonLabel.setText("# " + nouveauSalon);
            }
        });
    }

    @FXML
    private void actionEnvoi() {
        String texte = inputField.getText().trim();
        if (!texte.isEmpty()) {
            messagesBox.getChildren().add(new Label("Moi: " + texte));
            inputField.clear();
        }
    }

    @FXML
    private void handleAjouterTag(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau tag");
        dialog.setContentText("Nom du tag :");
        Optional<String> result = dialog.showAndWait();


    }

    @FXML
    private void handleCreerDiscussion(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle discussion");
        dialog.setContentText("Nom de la discussion :");
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
                salonList.getItems().add(nomNettoye);
                salonList.getSelectionModel().select(nomNettoye);
            }
        }}); 
    }
    

    @FXML
    private void handleCreerSalon(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Salon");
        dialog.setContentText("Nom du salon :");
        Optional<String> result = dialog.showAndWait();
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
