package messagerie_instantanee.UI.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller du squelette d'authentification (AuthLayout.fxml).
 * Il est le point central entre TopBar, Notification, LoginForm et RegisterForm.
 *
 * Responsabilités :
 *   - charger LoginForm.fxml par défaut au démarrage
 *   - swapper entre LoginForm et RegisterForm dans contentArea
 *   - afficher/masquer la sidebar des serveurs
 *   - exposer showError / showSuccess / showInfo pour les formulaires enfants
 */
public class AuthLayoutController {

    @FXML private StackPane contentArea;
    @FXML private VBox      sidebarMenu;

    // Injections automatiques des controllers inclus via fx:include
    @FXML private TopBarController      topBarController;       // fx:id="topBar"
    @FXML private NotificationController notificationController; // fx:id="notification"

    // ------------------------------------------------------------------ lifecycle

    @FXML
    public void initialize() throws Exception{
        topBarController.setParent(this);   // donne au TopBar une ref vers ce controller
        showLogin();                         // formulaire affiché par défaut
        throw new Exception();
    }

    // ------------------------------------------------------------------ navigation

    /** Charge LoginForm.fxml dans la zone centrale. */
    public void showLogin() {
        swapContent("/fxml/LoginForm.fxml", ctrl -> {
            if (ctrl instanceof LoginController lc) lc.setParent(this);
        });
    }

    /** Charge RegisterForm.fxml dans la zone centrale. */
    public void showRegister() {
        swapContent("/fxml/RegisterForm.fxml", ctrl -> {
            if (ctrl instanceof RegisterController rc) rc.setParent(this);
        });
    }

    /**
     * Utilitaire générique : charge un FXML et l'insère dans contentArea.
     * @param fxmlPath chemin absolu depuis le classpath
     * @param setup    lambda appelé avec le controller enfant juste après le chargement
     */
    private void swapContent(String fxmlPath, java.util.function.Consumer<Object> setup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node form = loader.load();
            setup.accept(loader.getController());
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la vue : " + fxmlPath);
        }
    }

    // ------------------------------------------------------------------ sidebar

    /** Affiche ou masque le panneau latéral des serveurs. */
    public void toggleSidebar() {
        boolean estVisible = sidebarMenu.isVisible();
        sidebarMenu.setVisible(!estVisible);
        sidebarMenu.setManaged(!estVisible);
    }

    // ------------------------------------------------------------------ notifications

    public void showError(String message)   { notificationController.showError(message);   }
    public void showSuccess(String message) { notificationController.showSuccess(message); }
    public void showInfo(String message)    { notificationController.showInfo(message);    }
}
