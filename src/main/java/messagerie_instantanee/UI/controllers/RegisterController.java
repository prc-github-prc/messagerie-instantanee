package messagerie_instantanee.UI.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import messagerie_instantanee.UI.NavigationManager;

public class RegisterController {
    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private VBox sidebarMenu;
    @FXML private Label errorLabel; 

    // Affiche le formulaire de connexion
    @FXML
    private void showLogin() {
        try{
            NavigationManager.getInstance()
                .naviguerVers("/fxml/LoginView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de l'application");
        }
    }

    @FXML
    private void handleToggleSidebar(ActionEvent event) {
        if (sidebarMenu != null) {
            boolean estVisible = sidebarMenu.isVisible();
            sidebarMenu.setVisible(!estVisible);
            sidebarMenu.setManaged(!estVisible);
        }
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        System.out.println("Compte créé ! Retour à la connexion.");
        showLogin(); // Retourne à la connexion après création
    }
}