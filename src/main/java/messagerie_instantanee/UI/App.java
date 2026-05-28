package messagerie_instantanee.UI;

import java.rmi.server.UnicastRemoteObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messagerie_instantanee.UI.controllers.TitleBarController;
import messagerie_instantanee.client.Client;

/**
 * Point d'entrée de l'application JavaFX.
 * La fenêtre est UNDECORATED (pas de barre Windows native) :
 * TitleBar.fxml fournit une barre custom qui suit le thème CSS.
 */
public class App extends Application {

    private static Client clientRMI;

    /** Référence statique au TitleBarController, accessible depuis n'importe quel controller. */
    public static TitleBarController titleBarController;

    public static void setClientRMI(Client client) {
        clientRMI = client;
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            // ── Supprime la barre Windows native ──────────────────────────
            stage.initStyle(StageStyle.UNDECORATED);

            // ── Structure racine ───────────────────────────────────────────
            BorderPane root = new BorderPane();
            root.getStyleClass().add("dark-theme");

            // Barre de titre custom (posée UNE SEULE FOIS, ne change jamais)
            FXMLLoader titleLoader = new FXMLLoader(getClass().getResource("/fxml/TitleBar.fxml"));
            Pane titleBar = titleLoader.load();
            titleBarController = titleLoader.getController();

            // Initialise le NavigationManager (utilise root.center pour les vues)
            NavigationManager.init(root);

            // Charge la première vue (AuthLayout)
            FXMLLoader authLoader = new FXMLLoader(
                getClass().getResource("/fxml/AuthLayout.fxml"));
            Pane authPanel = authLoader.load();
            root.setCenter(authPanel);

            // ── Scène ──────────────────────────────────────────────────────
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm());

            DimensionManager dm = DimensionManager.attach(stage, scene);
            stage.setTitle("Messagerie Instantanée");
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/logo-ogmos.png")));
            stage.show();
            dm.maximize(stage);
            titleBarController.setDimensionManager(dm);
            root.setTop(titleBar);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (clientRMI != null) {
            try {
                UnicastRemoteObject.unexportObject(clientRMI, true);
                System.out.println("[App] Client RMI unexport → port libéré.");
            } catch (Exception e) {
                System.err.println("[App] Erreur à la fermeture : " + e.getMessage());
            }
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
