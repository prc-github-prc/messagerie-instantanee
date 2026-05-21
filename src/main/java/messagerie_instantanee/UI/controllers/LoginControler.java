package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.rmi.Naming;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.interfaces.InterfaceServeurForum;

public class LoginControler {
    @FXML private TextField pseudoField;
    @FXML private TextField passwordField;
    @FXML private TextField serverField;
    @FXML private Button SeConnecter;
    @FXML private Label errorLabel; 

    @FXML
    private void onConnect(){
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
            if (server.checkId(pseudo, password.hash())){  //FIXME fonction de hash pas encore choisie

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
