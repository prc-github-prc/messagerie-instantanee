package messagerie_instantanee.UI.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import messagerie_instantanee.UI.NavigationManager;

/**
 * Controller de MenuBar.fxml.
 * Délègue les actions métier (salon, tag) à ChatController via la référence parent.
 */
public class MenuBarController {

    // Injection du nœud MenuBar lui-même pour accéder à la scène / la fenêtre
    @FXML private MenuBar      menuBar;
    @FXML private CheckMenuItem darkThemeItem;
    @FXML private CheckMenuItem fullscreenItem;

    private ChatController parent;

    // ------------------------------------------------------------------ init

    public void setParent(ChatController parent) {
        this.parent = parent;
    }

    /** Raccourci : récupère le Stage depuis le nœud MenuBar injecté. */
    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

    // ------------------------------------------------------------------ Fichier

    @FXML
    private void handleDeconnexion() {
        if (parent != null) parent.deconnecter();
        try {
            NavigationManager.getInstance().naviguerVers("/fxml/AuthLayout.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitter() {
        getStage().close();   // déclenche App.stop() → unexport RMI
    }

    // ------------------------------------------------------------------ Discussion

    @FXML
    private void handleNouveauSalon() {
        if (parent != null) parent.createNewSalonFromMenu();
    }

    @FXML
    private void handleNouveauTag() {
        if (parent != null) parent.createNewTagFromMenu();
    }

    @FXML
    private void handleRafraichir() {
        if (parent != null) parent.rafraichirSalons();
    }

    // ------------------------------------------------------------------ Affichage

    @FXML
    private void handleToggleTheme() {
        var root = menuBar.getScene().getRoot();
        if (darkThemeItem.isSelected()) {
            root.getStyleClass().remove("light-theme");
            root.getStyleClass().add("dark-theme");
        } else {
            root.getStyleClass().remove("dark-theme");
            root.getStyleClass().add("light-theme");
        }
    }

    @FXML
    private void handleToggleFullscreen() {
        getStage().setFullScreen(fullscreenItem.isSelected());
    }

    // ------------------------------------------------------------------ Aide

    @FXML
    private void handleAPropos() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Messagerie Instantanée");
        alert.setContentText(
            "Application de messagerie en temps réel.\n" +
            "Communication via RMI (Remote Method Invocation).\n\n" +
            "Projet — Licence ISTN"
        );
        alert.showAndWait();
    }
}
