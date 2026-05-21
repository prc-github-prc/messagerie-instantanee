package messagerie_instantanee.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class ChatControle {

    @FXML private ListView<String> salonList;
    @FXML private Label salonLabel;
    @FXML private VBox messagesBox;

    public void initialize() {
        salonList.getItems().addAll("Jeux vidéo", "Musique", "Cinéma");

        salonList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                changerDeSalon(newValue);
            }
        });
    }

    @FXML
    private void changerDeSalon(String nomSalon){

    }
}
