package messagerie_instantanee.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller de TopBar.fxml.
 * Gère le bouton de thème et le bouton d'ouverture de la sidebar.
 * Délègue toggleSidebar() à AuthLayoutController via la référence parent.
 */
public class TopBarController {

    @FXML private Button themeBtn;    // fx:id="themeBtn"
    @FXML private Button sidebarBtn;  // fx:id="sidebarBtn"

    private boolean isDarkTheme = true;
    private AuthLayoutController parent;

    // ======================  init  ======================

    /** Appelé par AuthLayoutController.initialize() après l'injection FXML. */
    public void setParent(AuthLayoutController parent) {
        this.parent = parent;
    }

    // ====================== actions FXML ======================

    /** Bascule entre le thème sombre et le thème clair sur toute la scène. */
    @FXML
    private void handleToggleTheme() {
        var root = themeBtn.getScene().getRoot();
        if (isDarkTheme) {
            root.getStyleClass().remove("dark-theme");
            root.getStyleClass().add("light-theme");
        } else {
            root.getStyleClass().remove("light-theme");
            root.getStyleClass().add("dark-theme");
        }
        isDarkTheme = !isDarkTheme;
    }

    /** Délègue l'ouverture / fermeture de la sidebar au controller parent. */
    @FXML
    private void handleToggleSidebar() {
        if (parent != null) parent.toggleSidebar();
    }
}
