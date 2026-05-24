package messagerie_instantanee.UI;

import java.rmi.server.UnicastRemoteObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import messagerie_instantanee.client.Client;

/**
 * Une application.
 */
public class App extends Application {

    // référence gardée pour pouvoir la unexport à la fermeture
    private static Client clientRMI;

    /**
     * Permet à ChatControler de transmettre le clientRMI à App
     * pour qu'il soit proprement unexport à la fermeture.
     */
    public static void setClientRMI(Client client) {
        clientRMI = client;
    }

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
        root.getStyleClass().add("dark-theme");

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

    /**
     * Appelé automatiquement par JavaFX quand la fenêtre est fermée (croix rouge).
     * C'est ici qu'on libère proprement les ressources RMI côté client,
     * sinon le thread RMI interne reste vivant et le terminal ne se ferme pas.
     */
    @Override
    public void stop() {
        // unexport le client RMI → ferme le port qu'il avait ouvert
        // et libère le thread RMI non-daemon qui bloquait la JVM
        if (clientRMI != null) {
            try {
                UnicastRemoteObject.unexportObject(clientRMI, true);
                System.out.println("[App] Client RMI unexport → port libéré.");
            } catch (Exception e) {
                System.err.println("[App] Erreur à la fermeture du client RMI : " + e.getMessage());
            }
        }
        // Platform.exit() est déjà appelé par JavaFX avant stop(),
        // mais System.exit(0) force la JVM à s'arrêter même si un thread non-daemon traîne
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
