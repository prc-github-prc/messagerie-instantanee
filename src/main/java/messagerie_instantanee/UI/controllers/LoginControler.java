package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.rmi.Naming;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.interfaces.InterfaceServeurForum;

/**
 * controller de login.
 */
public class LoginControler {
    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private TextField serverField;
    @FXML private Button SeConnecter; // bouton de validation du formulaire en vue de la connexion.
    @FXML private Label errorLabel;
    @FXML private VBox sidebarMenu;
    @FXML private VBox loginPane;
    @FXML private VBox signupPane;

    /**
     * gestion du thême.
     */
    private boolean isDarkTheme = true;

    @FXML
    private void showSignup() {
        try {
            NavigationManager.getInstance().naviguerVers("/fxml/RegisterView.fxml");
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

    @FXML
    private void handleLogin() {
        String pseudo = pseudoField.getText().trim();
        String serveur = serverField.getText().trim();
        String password = passwordField.getText().trim();

        if (pseudo.isEmpty() || serveur.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            InterfaceServeurForum server = (InterfaceServeurForum)
                Naming.lookup("//" + serveur + ":8090/messagerie");

            // Mot de passe brut — le serveur fait encoder.matches()
            if (server.checkId(pseudo, password)) {
                try {
                    var loader = NavigationManager.getInstance().naviguerVers("/fxml/ChatView.fxml");
                    ChatControler ctrl = loader.getController();
                    ctrl.configurerSession(server, pseudo);
                } catch (IOException e) {
                    e.printStackTrace();
                    errorLabel.setText("Erreur lors du chargement de l'application");
                }
            } else {
                errorLabel.setText("Pseudo ou mot de passe incorrect");
            }
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            errorLabel.setText("Connexion impossible : " + msg);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleToggleTheme(ActionEvent event) {
        var root = loginPane.getScene().getRoot();
        if (isDarkTheme) {
            root.getStyleClass().remove("dark-theme");
            root.getStyleClass().add("light-theme");
        } else {
            root.getStyleClass().remove("light-theme");
            root.getStyleClass().add("dark-theme");
        }
        isDarkTheme = !isDarkTheme;
    }
}
