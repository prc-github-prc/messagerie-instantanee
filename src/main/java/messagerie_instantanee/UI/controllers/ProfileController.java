package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import messagerie_instantanee.UI.NavigationManager;
import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.server.database.DAO.UserDAO;
import messagerie_instantanee.server.models.Discussion;

public class ProfileController {

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private ListView<Discussion> hiddenDiscussionList;

    private InterfaceServeurForum serveur;
    private String pseudo;


    // Méthode d'initialisation appelée depuis le ChatController après la connexion
    public void configurerProfil(
        InterfaceServeurForum serveur,
        String pseudo
    ) {

        this.serveur = serveur;
        this.pseudo = pseudo;

        usernameLabel.setText(pseudo);

        chargerDiscussionsMasquees();
    }



        // permet d'afficher des alerts (sert surtout a factoriser le code)
        private void showAlert(AlertType type, String titre, String contenu) {
            Alert alert = new Alert(type);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(contenu);
            alert.showAndWait();
        }


    // Charge les discussions masquées par l'utilisateur et configure la ListView
    private void chargerDiscussionsMasquees() {

        try {

            List<Discussion> discussions =
                serveur.getDiscussionsHidedByUser(pseudo);

            hiddenDiscussionList.setItems(
                FXCollections.observableArrayList(discussions)
            );

            hiddenDiscussionList.setCellFactory(list -> new ListCell<>() {

                @Override
                protected void updateItem(
                    Discussion discussion,
                    boolean empty
                ) {

                    super.updateItem(discussion, empty);

                    if (empty || discussion == null) {
                        setGraphic(null);
                        return;
                    }

                    Label nomDiscussion =
                        new Label(discussion.getNom_discussion());

                    nomDiscussion.getStyleClass()
                        .add("sidebar-title");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    Button btnAfficher =
                        new Button("Afficher");

                    btnAfficher.getStyleClass()
                        .add("btn-primary");

                    btnAfficher.setOnAction(event -> {

                        try {

                            serveur.unhideDiscussion(
                                discussion.getId_discussion(),
                                pseudo
                            );

                            hiddenDiscussionList
                                .getItems()
                                .remove(discussion);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });

                    HBox container =
                        new HBox(15,
                            nomDiscussion, spacer,
                            btnAfficher
                        );

                    container.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(container);
                }
            });

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    // Bouton de retour vers la vue de chat
    @FXML
    private void handleRetour(){
        try {
            FXMLLoader loader = NavigationManager.getInstance()
                .naviguerVers("/fxml/ChatView.fxml");
            
            ChatController controller = loader.getController();
            controller.configurerSession(serveur, pseudo);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Bouton de mise à jour du username
    @FXML
    private void handleUpdateUsername() {
        String newUsername = usernameField.getText().trim();

        if (newUsername.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Succès", "Username mis à jour !");
            return;
        }

        try {
            UserDAO.updateUsername(pseudo, newUsername);

            // update local state
            pseudo= newUsername;
            usernameLabel.setText(newUsername);

            showAlert(AlertType.INFORMATION, "Succès", "Username mis à jour !");
            
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }
}