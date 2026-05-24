package messagerie_instantanee.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controller de TitleBar.fxml.
 * Gère le drag de la fenêtre et les boutons réduire / agrandir / fermer.
 */
public class TitleBarController {

    @FXML private HBox     titleBar;
    @FXML private Button   minimizeBtn;
    @FXML private Button   maximizeBtn;
    @FXML private FontIcon maximizeIcon;

    // Position de la souris au moment du clic (pour le drag)
    private double dragOffsetX;
    private double dragOffsetY;

    // ------------------------------------------------------------------ drag

    @FXML
    private void handleMousePressed(MouseEvent e) {
        dragOffsetX = e.getScreenX() - getStage().getX();
        dragOffsetY = e.getScreenY() - getStage().getY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent e) {
        Stage stage = getStage();
        // Ne pas déplacer si la fenêtre est maximisée
        if (!stage.isMaximized()) {
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        }
    }

    // ------------------------------------------------------------------ boutons

    @FXML
    private void handleMinimize() {
        getStage().setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = getStage();
        boolean maximized = !stage.isMaximized();
        stage.setMaximized(maximized);
        // Mise à jour de l'icône selon l'état
        maximizeIcon.setIconLiteral(maximized ? "fas-compress" : "fas-expand");
    }

    @FXML
    private void handleClose() {
        // Déclenche App.stop() → unexport RMI proprement
        getStage().close();
    }

    // ------------------------------------------------------------------ utilitaire

    private Stage getStage() {
        return (Stage) titleBar.getScene().getWindow();
    }
}
