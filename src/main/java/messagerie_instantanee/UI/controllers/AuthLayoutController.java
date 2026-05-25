package messagerie_instantanee.UI.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller du squelette d'authentification (AuthLayout.fxml).
 */
public class AuthLayoutController {

    @FXML private StackPane contentArea;
    @FXML private VBox      sidebarMenu;

    // Injection automatique des controllers enfants (fx:id="topBar" → topBarController)
    @FXML private TopBarController       topBarController;
    @FXML private NotificationController notificationController;

    // Injection du noeud racine de Notification.fxml (HBox) pour pouvoir lui appliquer une marge
    @FXML private HBox notification;

    // ------------------------------------------------------------------ lifecycle

    @FXML
    public void initialize() {
        // Largeur sidebar = 1/4 de la largeur totale, recalculée dynamiquement
        sidebarMenu.prefWidthProperty().bind(
            contentArea.widthProperty().divide(4)
        );

        // Décale la notification sous la TopBar (impossible en FXML sur fx:include)
        StackPane.setMargin(notification, new Insets(75, 20, 0, 0));

        topBarController.setParent(this);
        showLogin();
    }

    // ------------------------------------------------------------------ navigation

    public void showLogin() {
        swapContent("/fxml/LoginForm.fxml");
    }

    public void showRegister() {
        swapContent("/fxml/RegisterForm.fxml");
    }

    private void swapContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node form = loader.load();

            // Injection de la référence parent dans le controller enfant
            Object ctrl = loader.getController();
            if (ctrl instanceof LoginController) {
                ((LoginController) ctrl).setParent(this);
            } else if (ctrl instanceof RegisterController) {
                ((RegisterController) ctrl).setParent(this);
            }

            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la vue : " + fxmlPath);
        }
    }

    // ------------------------------------------------------------------ sidebar

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
