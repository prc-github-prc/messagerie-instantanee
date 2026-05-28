package messagerie_instantanee.UI.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Controller du squelette d'authentification (AuthLayout.fxml).
 */
public class AuthLayoutController {

    @FXML private StackPane contentArea;

    // Injection automatique des controllers enfants (fx:id="topBar" → topBarController)
    @FXML private SideAuthSettingsController sideAuthSettingsController;
    @FXML private NotificationController notificationController;

    // Injection du noeud racine de Notification.fxml (HBox) pour pouvoir lui appliquer une marge
    @FXML private HBox notification;

    // ------------------------------------------------------------------ lifecycle

    @FXML
    public void initialize() {
        // Décale la notification sous la TopBar (impossible en FXML sur fx:include)
        StackPane.setMargin(notification, new Insets(75, 20, 0, 0));

        sideAuthSettingsController.setParent(this);
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
            if (ctrl instanceof LoginController lc) {
                lc.setParent(this);
            } else if (ctrl instanceof RegisterController rc) {
                rc.setParent(this);
            }

            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la vue : " + fxmlPath);
        }
    }

    // ------------------------------------------------------------------ notifications

    public void showError(String message)   { notificationController.showError(message);   }
    public void showSuccess(String message) { notificationController.showSuccess(message); }
    public void showInfo(String message)    { notificationController.showInfo(message);    }

    /** Retourne l'IP saisie dans la sidebar, utilisée par Login et Register. */
    public String getServerIp() {
        return sideAuthSettingsController.getServerIp();
    }
}
