package messagerie_instantanee.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Singleton de navigation.
 * Détient le BorderPane racine de l'application et permet
 * à n'importe quel controller de changer le panel affiché
 * sans avoir besoin d'une référence directe à App.java.
 */
public class NavigationManager {

    private static NavigationManager instance;
    private BorderPane root;

    private NavigationManager() {}

    /**
     * Initialise le singleton avec le root de App.java.
     * À appeler UNE SEULE FOIS dans App.start().
     */
    public static void init(BorderPane root) {
        if (instance == null) {
            instance = new NavigationManager();
        }
        instance.root = root;
    }

    /**
     * Retourne l'instance unique (doit être initialisée avant).
     */
    public static NavigationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "NavigationManager non initialisé. Appelez init(root) dans App.start() d'abord.");
        }
        return instance;
    }

    /**
     * Retourne le Stage courant.
     */
    public Stage getStage() {
        if (instance == null) {
            throw new IllegalStateException(
                "NavigationManager non initialisé. Appelez init(root) dans App.start() d'abord.");
        }
        return (Stage) root.getScene().getWindow();
    }

    /**
     * Charge un fichier FXML et l'affiche dans le centre du root.
     * Retire automatiquement la MenuBar de la TitleBar avant chaque navigation,
     * ce qui garantit qu'elle n'apparaît que dans ChatView.
     *
     * @param cheminFxml  ex: "/fxml/ChatView.fxml"
     * @return le FXMLLoader après chargement (permet de récupérer le controller)
     */
    public FXMLLoader naviguerVers(String cheminFxml) throws Exception {
        // ✅ Retire la MenuBar de la TitleBar à chaque changement de vue.
        // ChatController.initialize() la réinjectera si on va vers ChatView.
        if (App.titleBarController != null) {
            App.titleBarController.removeMenuBar();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));
        Pane panel = loader.load();
        root.setCenter(panel);
        return loader;
    }

    /**
     * Affiche directement un Pane déjà chargé (sans recharger le FXML).
     * Utile si on veut mettre en cache un panel.
     */
    public void naviguerVers(Pane panel) {
        if (App.titleBarController != null) {
            App.titleBarController.removeMenuBar();
        }
        root.setCenter(panel);
    }
}
