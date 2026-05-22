package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.nio.channels.IllegalSelectorException;
import java.rmi.Naming;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.server.Server;

/**
 * Controller d'inscription.
 */
public class RegisterController {
    /**
     * champs de pseudo.
     */
    @FXML private TextField pseudoField;
    /**
     * champs de mdp.
     */
    @FXML private PasswordField passwordField;
    /**
     * champs du serveur.
     */
    @FXML private TextField serverField;
    /**
     * barre des tâches.
     */
    @FXML private VBox sidebarMenu;
    /**
     * zone d'erreur éventuelle.
     */
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

    /**
     * 
     * @param event
     */
    @FXML
    private void handleToggleSidebar(ActionEvent event) {
        if (sidebarMenu != null) {
            boolean estVisible = sidebarMenu.isVisible();
            sidebarMenu.setVisible(!estVisible);
            sidebarMenu.setManaged(!estVisible);
        }
    }

    /**
     * 
     * @param event
     */
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
            InterfaceServeurForum server = (InterfaceServeurForum)
                Naming.lookup("//" + serveur + ":8090/messagerie");

            // ============== verifie pseudo + pwd =============
            try{
                if (server.creationUser(pseudo, password)){
                    System.out.println("compte crée");
                } else {
                    errorLabel.setText("Vous ne pouvez pas crée de compte");
                    return;
                }
            } catch(IllegalSelectorException e){
                errorLabel.setText("Ce nom d'utilisateur est deja utiliser");
                return;
            }
        } catch (Exception e) {
            errorLabel.setText("Connexion impossible : " + e.getMessage());
            return;
        }

        errorLabel.setText("Compte créé ! Retour à la connexion."); //TODO asser ça pop up si on a le temps
        showLogin(); // Retourne à la connexion après création
    }
}