package messagerie_instantanee.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import messagerie_instantanee.UI.DimensionManager;

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

    private static final double SNAP_THRESHOLD = 10; // px du bord haut pour déclencher

    private DimensionManager dm;

    // ------------------------------------------------------------------ drag

    @FXML
    private void handleMousePressed(MouseEvent e) {
        dragOffsetX = e.getScreenX() - getStage().getX();
        dragOffsetY = e.getScreenY() - getStage().getY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent e) {
        Stage stage = getStage();
        if (dm.isFakeMaximized()) {          // ← remplace stage.isMaximized()
            dm.restore(stage);
            dragOffsetX = stage.getWidth() / 2;
            dragOffsetY = e.getScreenY() - stage.getY();
            maximizeIcon.setIconLiteral("fas-expand");
        }
        stage.setX(e.getScreenX() - dragOffsetX);
        stage.setY(e.getScreenY() - dragOffsetY);
    }

    @FXML
    private void handleMouseReleased(MouseEvent e) {
        titleBar.getScene().getRoot().setOpacity(1.0);
        if (e.getScreenY() <=  SNAP_THRESHOLD) {
            dm.maximize(getStage());
            maximizeIcon.setIconLiteral("fas-compress");
        }
    }

    // Méthode appelée par App.java après le attach()
    public void setDimensionManager(DimensionManager dm) {
        this.dm = dm;
    }

    // ------------------------------------------------------------------ boutons

    @FXML
    private void handleMinimize() {
        getStage().setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        dm.toggleMaximize(getStage());
        maximizeIcon.setIconLiteral(dm.isFakeMaximized() ? "fas-compress" : "fas-expand");
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
