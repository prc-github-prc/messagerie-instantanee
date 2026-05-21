package messagerie_instantanee.UI;

import java.io.IOException;

import javafx.event.ActionEvent;
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

public class LoginChat {
    @FXML private TextField pseudoField;
    @FXML private TextField serverField;
    @FXML private Button SeConnecter;
    @FXML private Label errorLabel; 
    @FXML private VBox sidebarMenu;

    @FXML
    private void handleToggleSidebar(ActionEvent event) {
        if (sidebarMenu != null) {
            boolean estVisible = sidebarMenu.isVisible();
            sidebarMenu.setVisible(!estVisible);
            sidebarMenu.setManaged(!estVisible);
        }
    }
    
    
    @FXML
    private void onConnect(){
        //String pseudo = pseudoField.getText().trim();
        String serveur = serverField.getText().trim();
        if (serveur.isEmpty()) {
            errorLabel.setText("Veuillez remplir le champ serveur");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatView.fxml"));
            Parent chatRoot = loader.load();
            Stage stage = (Stage) SeConnecter.getScene().getWindow();
            
            Scene scene = new Scene(chatRoot);
                        
            stage.setScene(scene);
            //stage.setTitle("Chat - " + pseudo);
            stage.centerOnScreen(); 
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de l'application");
        }
    }

    
    
}
