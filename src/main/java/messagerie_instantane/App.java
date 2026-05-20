package messagerie_instantane;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Charger le fichier FXML depuis les ressources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatView.fxml"));
        Parent root = loader.load();

        // 2. Créer la scène avec le contenu du FXML
        Scene scene = new Scene(root, 600, 400);

        // 3. Afficher la fenêtre
        stage.setTitle("Mon Application FXML");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
