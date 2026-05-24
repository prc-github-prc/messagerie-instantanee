package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.rmi.Naming;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.interfaces.InterfaceServeurForum;

/**
 * Controller de LoginForm.fxml.
 *
 * Ne gère plus :
 *   - l'affichage des erreurs (→ parent.showError)
 *   - le thème (→ TopBarController)
 *   - la sidebar (→ TopBarController)
 *
 * Requiert un appel à setParent() après le chargement FXML (fait par AuthLayoutController).
 */
public class LoginController {

    @FXML private TextField     pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     serverField;

    private AuthLayoutController parent;

    // ------------------------------------------------------------------ init

    /** Injecté par AuthLayoutController après le chargement du FXML. */
    public void setParent(AuthLayoutController parent) {
        this.parent = parent;
    }

    // ------------------------------------------------------------------ actions FXML

    /** Lien "Pas encore de compte ?" → affiche RegisterForm dans le squelette. */
    @FXML
    private void showRegister() {
        parent.showRegister();
    }

    /** Valide les champs et tente la connexion RMI. */
    @FXML
    private void handleLogin() {
        String pseudo   = pseudoField.getText().trim();
        String serveur  = serverField.getText().trim();
        String password = passwordField.getText().trim();

        // -------- validation basique --------
        if (pseudo.isEmpty() || serveur.isEmpty() || password.isEmpty()) {
            parent.showError("Veuillez remplir tous les champs");
            return;
        }

        // -------- connexion RMI --------
        try {
            InterfaceServeurForum server = (InterfaceServeurForum)
                Naming.lookup("//" + serveur + ":8090/messagerie");

            if (server.checkId(pseudo, password)) {
                try {
                    var loader = NavigationManager.getInstance().naviguerVers("/fxml/ChatView.fxml");
                    ChatController ctrl = loader.getController();
                    ctrl.configurerSession(server, pseudo);
                } catch (IOException e) {
                    e.printStackTrace();
                    parent.showError("Erreur lors du chargement de l'application");
                }
            } else {
                parent.showError("Pseudo ou mot de passe incorrect");
            }

        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            parent.showError("Connexion impossible : " + msg);
            e.printStackTrace();
        }
    }
}
