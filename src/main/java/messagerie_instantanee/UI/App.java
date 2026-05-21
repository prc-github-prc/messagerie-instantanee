package messagerie_instantanee.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Charger le fichier FXML depuis les ressources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/afficClient.fxml"));
        Parent root = loader.load();
        ChatControle controleur = loader.getController();

        // 2. Créer la scène avec le contenu du FXML
        Scene scene = new Scene(root);

        // 3. Afficher la fenêtre
        stage.setTitle("Mon Application FXML");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
