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
 *
 */
public class NavigationManager {

    // ── Instance unique ───────────────────────────────────────────────
    /**
     * instance de gestionnaire de navigation.
     */
    private static NavigationManager instance;

    // ── Référence au BorderPane racine de la fenêtre ──────────────────
    /**
     * root.
     */
    private BorderPane root;

    // ── Constructeur privé : empêche le new depuis l'extérieur ────────
    /**
     * 
     */
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
     * Retourne le stage de l'instance unique
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
     * Retourne le FXMLLoader pour que l'appelant puisse récupérer
     * le controller via loader.getController() si besoin.
     *
     * @param cheminFxml  ex: "/fxml/ChatView.fxml"
     * @return le FXMLLoader après chargement
     */
    public FXMLLoader naviguerVers(String cheminFxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource(cheminFxml));
        Pane panel = loader.load();
        root.setCenter(panel);
        return loader; // ← permet de récupérer le controller si besoin
    }

    /**
     * Affiche directement un Pane déjà chargé (sans recharger le FXML).
     * Utile si on veut mettre en cache un panel.
     *
     * @param panel le Pane à afficher
     */
    public void naviguerVers(Pane panel) {
        root.setCenter(panel);
    }

    /**
     * permet de passez en pleine ecran
     * @param value boolean t si oui 
     */
    public void setMaximized(Boolean value){
        getStage().setMaximized(value);
    }
}
