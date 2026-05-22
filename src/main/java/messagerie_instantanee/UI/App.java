package messagerie_instantanee.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    /**
     * @param stage
     * 
     * Démarre un stage.
     */
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();

        // == Initialise le NavigationManager avec le root ==============
        // À faire AVANT de charger le premier FXML, pour que les
        // controllers puissent déjà utiliser NavigationManager si besoin
        NavigationManager.init(root);

    // == Charge et affiche le panel Login au démarrage =============
    Pane panelLogin = FXMLLoader.load(
        getClass().getResource("/fxml/LoginView.fxml")); //RECHECK post merge
    root.setCenter(panelLogin);

        // == Mise en page et scène =====================================
        StackPane wrapper = new StackPane(root);
        wrapper.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(wrapper);
        scene.getStylesheets().add(
            getClass().getResource("/css/style.css").toExternalForm());
        //stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Messagerie Instantanée");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
