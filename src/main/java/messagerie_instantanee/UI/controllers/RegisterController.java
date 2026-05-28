package messagerie_instantanee.UI.controllers;

import java.nio.channels.IllegalSelectorException;
import java.rmi.Naming;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import messagerie_instantanee.interfaces.InterfaceServeurForum;

/**
 * Controller de RegisterForm.fxml.
 *
 * Ne gère plus :
 *   - l'affichage des erreurs (→ parent.showError / parent.showSuccess)
 *   - le thème (→ TopBarController)
 *   - la sidebar (→ TopBarController)
 *
 * Requiert un appel à setParent() après le chargement FXML (fait par AuthLayoutController).
 */
public class RegisterController {

    @FXML private TextField     pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private AuthLayoutController parent;

    // ------------------------------------------------------------------ init

    /** Injecté par AuthLayoutController après le chargement du FXML. */
    public void setParent(AuthLayoutController parent) {
        this.parent = parent;
    }

    // ------------------------------------------------------------------ actions FXML

    /** Lien "Déjà un compte ?" → affiche LoginForm dans le squelette. */
    @FXML
    private void showLogin() {
        parent.showLogin();
    }

    /** Valide les champs et tente la création de compte via RMI. */
    @FXML
    private void handleCreateAccount(ActionEvent event) {
        String pseudo    = pseudoField.getText().trim();
        String serveur   = parent.getServerIp();
        String password  = passwordField.getText().trim();
        String confirm   = confirmPasswordField.getText().trim();

        // -------- validation basique --------
        if (pseudo.isEmpty() || serveur.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            parent.showError("Veuillez remplir tous les champs");
            return;
        }

        if (!password.equals(confirm)) {
            parent.showError("Les mots de passe ne correspondent pas");
            return;
        }

        // -------- connexion RMI + création --------
        try {
            InterfaceServeurForum server = (InterfaceServeurForum)
                Naming.lookup("//" + serveur + ":8090/messagerie");

            try {
                if (server.creationUser(pseudo, password)) {
                    parent.showSuccess("Compte créé ! Vous pouvez vous connecter.");
                    parent.showLogin();
                } else {
                    parent.showError("Impossible de créer ce compte");
                }
            } catch (IllegalSelectorException e) {
                parent.showError("Ce nom d'utilisateur est déjà utilisé");
            }

        } catch (Exception e) {
            parent.showError("Connexion impossible : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
