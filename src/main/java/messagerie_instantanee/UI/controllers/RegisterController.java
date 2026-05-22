package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.nio.channels.IllegalSelectorException;
import java.rmi.Naming;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.server.Server;

public class RegisterController {
    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private TextField serverField;
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
        String pseudo = pseudoField.getText().trim();
        String serveur = serverField.getText().trim();
        String password = passwordField.getText().trim();

        // =========== validation du contenue des champ ===========
        if (pseudo.isEmpty() || serveur.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        // ========= verification que le serveur existe ===========
        try {
            Server server = (Server)
                Naming.lookup("//" + serveur + ":8090/messagerie");

            // ============== verifie pseudo + pwd =============
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            try{
                if (server.register(pseudo, encoder.encode(password))){
                } else {
                    errorLabel.setText("Vous ne pouvez pas crée de compte");
                }
            } catch(IllegalSelectorException e){
                errorLabel.setText("Ce nom d'utilisateur est deja utiliser");
            }
        } catch (Exception e) {
            errorLabel.setText("Connexion impossible : " + e.getMessage());
        }

        errorLabel.setText("Compte créé ! Retour à la connexion."); //TODO asser ça pop upsi on a le temps
        showLogin(); // Retourne à la connexion après création
    }
}