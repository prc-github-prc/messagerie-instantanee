package messagerie_instantanee.UI;

import java.rmi.server.UnicastRemoteObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import messagerie_instantanee.client.Client;

/**
 * Point d'entrée de l'application JavaFX.
 */
public class App extends Application {

    // Référence gardée pour pouvoir l'unexport à la fermeture
    private static Client clientRMI;

    /**
     * Permet à ChatController de transmettre le clientRMI à App
     * pour qu'il soit proprement unexport à la fermeture.
     */
    public static void setClientRMI(Client client) {
        clientRMI = client;
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();

        // ── Initialise le NavigationManager AVANT le premier FXML ──────
        // (les controllers peuvent déjà l'utiliser dans initialize())
        NavigationManager.init(root);

        // ── Charge le squelette d'authentification ─────────────────────
        // On utilise un FXMLLoader INSTANCE (pas la méthode statique)
        // pour que JavaFX instancie bien le controller et appelle initialize().
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/AuthLayout.fxml"));
        BorderPane authPanel = loader.load();   // ← initialize() est appelé ici
        root.setCenter(authPanel);
        root.getStyleClass().add("dark-theme");

        // ── Scène ───────────────────────────────────────────────────────
        StackPane wrapper = new StackPane(root);
        wrapper.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(wrapper);
        scene.getStylesheets().add(
            getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("Messagerie Instantanée");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Appelé automatiquement par JavaFX à la fermeture de la fenêtre.
     * Libère proprement les ressources RMI côté client.
     */
    @Override
    public void stop() {
        if (clientRMI != null) {
            try {
                UnicastRemoteObject.unexportObject(clientRMI, true);
                System.out.println("[App] Client RMI unexport → port libéré.");
            } catch (Exception e) {
                System.err.println("[App] Erreur à la fermeture du client RMI : " + e.getMessage());
            }
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
