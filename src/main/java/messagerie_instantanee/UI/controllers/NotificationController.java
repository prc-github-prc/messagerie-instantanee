package messagerie_instantanee.UI.controllers;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Controller de Notification.fxml.
 * Affiche une notification flottante en haut à droite de la scène.
 * Masquée par défaut ; appelée via AuthLayoutController.showError/Success/Info().
 *
 * Types disponibles :
 *   showError(msg)   → fond rouge  #e74c3c
 *   showSuccess(msg) → fond vert   #27ae60
 *   showInfo(msg)    → fond bleu   #2980b9
 */
public class NotificationController {

    @FXML private HBox     notificationBox;  // fx:id="notificationBox"
    @FXML private Label    notifLabel;       // fx:id="notifLabel"
    @FXML private FontIcon notifIcon;        // fx:id="notifIcon"

    // ------------------------------------------------------------------ actions FXML

    /** Masque la notification (bouton ✕ dans le FXML). */
    @FXML
    public void hide() {
        notificationBox.setVisible(false);
        notificationBox.setManaged(false);
    }

    // ------------------------------------------------------------------ API publique

    public void showError(String message) {
        show(message, "#e74c3c", "fas-exclamation-circle");
    }

    public void showSuccess(String message) {
        show(message, "#27ae60", "fas-check-circle");
    }

    public void showInfo(String message) {
        show(message, "#2980b9", "fas-info-circle");
    }

    // ------------------------------------------------------------------ interne

    private void show(String message, String color, String iconLiteral) {
        notifLabel.setText(message);
        notifIcon.setIconLiteral(iconLiteral);
        notificationBox.setStyle(
            "-fx-background-color: " + color + ";"
            + "-fx-background-radius: 10px;"
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.35), 12, 0, 0, 4);"
        );
        notificationBox.setVisible(true);
        notificationBox.setManaged(true);
    }
}
