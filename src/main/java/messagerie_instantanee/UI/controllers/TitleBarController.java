package messagerie_instantanee.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import messagerie_instantanee.UI.DimensionManager;

import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controller de TitleBar.fxml.
 * Gère le drag de la fenêtre, les boutons réduire / agrandir / fermer,
 * et l'injection optionnelle de la MenuBar (uniquement dans ChatView).
 */
public class TitleBarController {

    @FXML private HBox     titleBar;
    @FXML private HBox     menuBarSlot;   // slot pour la MenuBar de ChatView
    @FXML private Button   minimizeBtn;
    @FXML private Button   maximizeBtn;
    @FXML private FontIcon maximizeIcon;

    private double dragOffsetX;
    private double dragOffsetY;

    private static final double SNAP_THRESHOLD = 10;

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
        if (dm.isFakeMaximized()) {
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
        if (e.getScreenY() <= SNAP_THRESHOLD) {
            dm.maximize(getStage());
            maximizeIcon.setIconLiteral("fas-compress");
        }
    }

    // ------------------------------------------------------------------ injection MenuBar

    /**
     * Insère la MenuBar native dans le slot prévu dans la TitleBar.
     * Appelé par ChatController.initialize() lors de la navigation vers ChatView.
     */
    public void injectMenuBar(MenuBar menuBar) {
        menuBarSlot.getChildren().setAll(menuBar);
        // Rend la MenuBar transparente pour qu'elle hérite du style de la TitleBar
        menuBar.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        menuBar.setPrefHeight(32);
    }

    /**
     * Vide le slot MenuBar.
     * Appelé par NavigationManager.naviguerVers() à chaque changement de vue,
     * ce qui retire automatiquement la MenuBar quand on revient sur AuthLayout.
     */
    public void removeMenuBar() {
        menuBarSlot.getChildren().clear();
    }

    // ------------------------------------------------------------------ DimensionManager

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
        getStage().close();
    }

    // ------------------------------------------------------------------ utilitaire

    private Stage getStage() {
        return (Stage) titleBar.getScene().getWindow();
    }
}
