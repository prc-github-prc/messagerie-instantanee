package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import messagerie_instantanee.client.models.ServerBookmark;
import messagerie_instantanee.client.services.ServerBookmarkService;

/**
 * Controller de SideAuthSettings.fxml.
 * Gère le panneau latéral droit de l'écran d'authentification :
 *   - bouton thème (dark / light)
 *   - bouton affichage du panneau serveur/bookmarks
 *   - gestion de la liste de bookmarks (ajout / suppression)
 *   - champ serveur (IP utilisée pour la connexion)
 */
public class SideAuthSettingsController {

    @FXML private TextField serverField;
    @FXML private VBox      sideServerSettings;
    @FXML private VBox      bookMarkList;
    
    @FXML private Button    themeBtn;
    @FXML private Button    sidebarBtn;

    private AuthLayoutController parent;

    // ------------------------------------------------------------------ init

    public void setParent(AuthLayoutController parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {
        try {
            List<ServerBookmark> lst_bookMark = ServerBookmarkService.charger();
            for (ServerBookmark mark : lst_bookMark){
                addBookmark(mark);
            }
        } catch (IOException e) {
            Label error = new Label("le chargement des marque page a échouer");
            bookMarkList.getChildren().add(error);
        }
    }


    // ==================  thème ====================

    @FXML
    private void handleToggleTheme() {
        var root = themeBtn.getScene().getRoot();
        if (root.getStyleClass().contains("dark-theme")) {
            root.getStyleClass().remove("dark-theme");
            root.getStyleClass().add("light-theme");
        } else {
            root.getStyleClass().remove("light-theme");
            root.getStyleClass().add("dark-theme");
        }
    }

    // ================================  sidebar serveur ======================

    @FXML
    private void handleToggleSidebar() {
        boolean visible = !sideServerSettings.isVisible();
        sideServerSettings.setVisible(visible);
        sideServerSettings.setManaged(visible);
    }

    // ========== bookmarks ==========

    /**
     * Retourne l'IP saisie dans le champ serveur.
     * Appelé par LoginController pour pré-remplir le champ de connexion.
     */
    public String getServerIp() {
        return serverField.getText().trim();
    }

    /**
     * Ajoute un bookmark dans la liste et crée sa ligne visuelle.
     */
    public void addBookmark(ServerBookmark bookmark) {
        // Labels
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label(bookmark.getName());
        nameLabel.getStyleClass().add("input-label");
        nameLabel.setStyle("-fx-font-size: 14;");

        javafx.scene.control.Label ipLabel = new javafx.scene.control.Label(bookmark.getIp());
        ipLabel.getStyleClass().add("input-label");
        ipLabel.setStyle("-fx-font-size: 14;");

        VBox textBox = new VBox(nameLabel, ipLabel);

        // Bouton supprimer
        org.kordamp.ikonli.javafx.FontIcon trashIcon =
            new org.kordamp.ikonli.javafx.FontIcon("fas-trash");
        trashIcon.setIconSize(20);
        trashIcon.setIconColor(javafx.scene.paint.Color.WHITE);

        Button deleteBtn = new Button();
        deleteBtn.getStyleClass().add("envoi-btn-container");
        deleteBtn.setGraphic(new javafx.scene.layout.StackPane(trashIcon));

        // Ligne
        HBox row = new HBox(textBox, deleteBtn);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);

        // Attache le bookmark à la ligne pour le retrouver lors de la suppression
        row.setUserData(bookmark);

        deleteBtn.setOnAction(e -> {
            ServerBookmark toDelete = (ServerBookmark) row.getUserData();
            bookMarkList.getChildren().remove(row);
            System.out.println("Bookmark supprimé : " + toDelete.getName());
        });

        VBox.setMargin(row, new Insets(8, 0, 8, 0));
        row.getStyleClass().setAll("bookmark");
        bookMarkList.getChildren().add(row);
    }
}
