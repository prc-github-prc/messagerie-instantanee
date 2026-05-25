package messagerie_instantanee.UI;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Permet le redimensionnement d'une fenêtre UNDECORATED par drag sur ses bordures.
 * Usage : ResizeHelper.attach(stage, scene);
 */
public class DimensionManager {

    /** Épaisseur (px) de la zone sensible sur chaque bord. */
    private static final double BORDER = 6;
    private static final double MIN_W  = 800;
    private static final double MIN_H  = 600;

    // État au moment du clic
    private double clickX, clickY;       // position souris (screen)
    private double stageX, stageY;       // position fenêtre
    private double stageW, stageH;       // taille fenêtre
    private Zone   zone = Zone.NONE;

    /** Les 8 zones de redimensionnement + NONE (zone centrale). */
    private enum Zone {
        NONE,
        N, S, E, W,
        NE, NW, SE, SW
    }

    // ------------------------------------------------------------------ API publique

    public static void attach(Stage stage, Scene scene) {
        DimensionManager rh = new DimensionManager();

        scene.addEventFilter(MouseEvent.MOUSE_MOVED,   e -> rh.updateCursor(e, stage, scene));
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> rh.onPressed(e, stage));
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> rh.onDragged(e, stage));
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> rh.zone = Zone.NONE);
    }

    // ------------------------------------------------------------------ détection de zone

    private Zone detectZone(MouseEvent e, Scene scene) {
        double x = e.getX(), y = e.getY();
        double w = scene.getWidth(), h = scene.getHeight();

        boolean top    = y < BORDER;
        boolean bottom = y > h - BORDER;
        boolean left   = x < BORDER;
        boolean right  = x > w - BORDER;

        if (top    && left)  return Zone.NW;
        if (top    && right) return Zone.NE;
        if (bottom && left)  return Zone.SW;
        if (bottom && right) return Zone.SE;
        if (top)    return Zone.N;
        if (bottom) return Zone.S;
        if (left)   return Zone.W;
        if (right)  return Zone.E;
        return Zone.NONE;
    }

    // ------------------------------------------------------------------ curseur

    private void updateCursor(MouseEvent e, Stage stage, Scene scene) {
        if (stage.isMaximized()) {
            scene.setCursor(Cursor.DEFAULT);
            return;
        }
        switch (detectZone(e, scene)) {
            case N,  S  -> scene.setCursor(Cursor.N_RESIZE);
            case E,  W  -> scene.setCursor(Cursor.E_RESIZE);
            case NE, SW -> scene.setCursor(Cursor.NE_RESIZE);
            case NW, SE -> scene.setCursor(Cursor.NW_RESIZE);
            default      -> scene.setCursor(Cursor.DEFAULT);
        }
    }

    // ------------------------------------------------------------------ clic

    private void onPressed(MouseEvent e, Stage stage) {
        zone   = detectZone(e, stage.getScene());
        clickX = e.getScreenX();
        clickY = e.getScreenY();
        stageX = stage.getX();
        stageY = stage.getY();
        stageW = stage.getWidth();
        stageH = stage.getHeight();
    }

    // ------------------------------------------------------------------ drag

    private void onDragged(MouseEvent e, Stage stage) {
        if (zone == Zone.NONE || stage.isMaximized()) return;

        double dx = e.getScreenX() - clickX;   // déplacement horizontal
        double dy = e.getScreenY() - clickY;   // déplacement vertical

        double newX = stageX, newY = stageY;
        double newW = stageW, newH = stageH;

        // Bord droit / gauche
        if (zone == Zone.E || zone == Zone.NE || zone == Zone.SE)
            newW = Math.max(MIN_W, stageW + dx);

        if (zone == Zone.W || zone == Zone.NW || zone == Zone.SW) {
            newW = Math.max(MIN_W, stageW - dx);
            if (newW > MIN_W) newX = stageX + dx;  // déplace le bord gauche
        }

        // Bord bas / haut
        if (zone == Zone.S || zone == Zone.SE || zone == Zone.SW)
            newH = Math.max(MIN_H, stageH + dy);

        if (zone == Zone.N || zone == Zone.NE || zone == Zone.NW) {
            newH = Math.max(MIN_H, stageH - dy);
            if (newH > MIN_H) newY = stageY + dy;  // déplace le bord haut
        }

        stage.setX(newX); stage.setY(newY);
        stage.setWidth(newW); stage.setHeight(newH);
    }
}