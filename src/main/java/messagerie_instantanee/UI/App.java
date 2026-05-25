package messagerie_instantanee.UI;

import java.rmi.server.UnicastRemoteObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messagerie_instantanee.client.Client;

/**
 * Point d'entrée de l'application JavaFX.
 * La fenêtre est UNDECORATED (pas de barre Windows native) :
 * TitleBar.fxml fournit une barre custom qui suit le thème CSS.
 */
public class App extends Application {

    private static Client clientRMI;

    public static void setClientRMI(Client client) {
        clientRMI = client;
    }

    @Override
    public void start(Stage stage) throws Exception {
        try{
            // ── Supprime la barre Windows native ───────────────────────────
            stage.initStyle(StageStyle.UNDECORATED);

            // ── Structure racine ────────────────────────────────────────────
            //   BorderPane
            //   ├── top    → TitleBar.fxml  (barre custom persistante)
            //   └── center → contenu courant (AuthLayout, ChatView…)
            BorderPane root = new BorderPane();
            root.getStyleClass().add("dark-theme");

            // Barre de titre custom (posée UNE SEULE FOIS, ne change jamais)
            Pane titleBar = FXMLLoader.load(
                getClass().getResource("/fxml/TitleBar.fxml"));
            root.setTop(titleBar);

            // Initialise le NavigationManager (utilise root.center pour les vues)
            NavigationManager.init(root);

            // Charge la première vue (AuthLayout)
            FXMLLoader authLoader = new FXMLLoader(
                getClass().getResource("/fxml/AuthLayout.fxml"));
            Pane authPanel = authLoader.load();
            root.setCenter(authPanel);

            // ── Scène ───────────────────────────────────────────────────────
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm());

            stage.setTitle("Messagerie Instantanée");
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        } catch(Exception e){
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
