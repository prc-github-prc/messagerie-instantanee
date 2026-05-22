package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.rmi.Naming;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.server.Server;

public class LoginControler {
    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private TextField serverField;
    @FXML private Button SeConnecter;
    @FXML private Label errorLabel; 
    @FXML private VBox sidebarMenu;
    @FXML private VBox loginPane;
    @FXML private VBox signupPane;

    // Affiche le formulaire d'inscription
    @FXML
    private void showSignup() {
        try{
            NavigationManager.getInstance()
                .naviguerVers("/fxml/RegisterView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la page sign up");
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

    // =================== Gere la connection ===================
    @FXML
    private void handleLogin(){
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
            if (server.checkId(pseudo, encoder.encode(password))){
                // ================== charge la page de l'application si c'est bon ===========
                try {
                    // ====== charge la vue ======
                    ChatControler ctrl = NavigationManager.getInstance().naviguerVers("/fxml/ChatView.fxml").getController();

                    // ====== initialise le controller =====
                    ctrl.initialize(server, pseudo);
                } catch (IOException e) {
                    e.printStackTrace();
                    errorLabel.setText("Erreur lors du chargement de l'application");
                }
            } else {
                errorLabel.setText("Vos information de connexion sont erronée");
            }
        } catch (Exception e) {
            errorLabel.setText("Connexion impossible : " + e.getMessage());
        }
    }
}
