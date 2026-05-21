package messagerie_instantanee.UI;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class AuthController {

    @FXML private VBox loginPane;
    @FXML private VBox signupPane;

    // Affiche le formulaire d'inscription
    @FXML
    private void showSignup() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        
        signupPane.setVisible(true);
        signupPane.setManaged(true);
    }

    // Affiche le formulaire de connexion
    @FXML
    private void showLogin() {
        signupPane.setVisible(false);
        signupPane.setManaged(false);
        
        loginPane.setVisible(true);
        loginPane.setManaged(true);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("Tentative de connexion...");
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        System.out.println("Compte créé ! Retour à la connexion.");
        showLogin(); // Retourne à la connexion après création
    }
}

