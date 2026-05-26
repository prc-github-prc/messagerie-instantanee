package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import messagerie_instantanee.client.models.ServerBookmark;
import messagerie_instantanee.client.services.ServerBookmarkService;

/**
 * Controller de TopBar.fxml.
 * Gère le bouton de thème et le bouton d'ouverture de la sidebar.
 * Délègue toggleSidebar() à AuthLayoutController via la référence parent.
*/
public class SideAuthSettingsController {
    
    @FXML private VBox BookMarkList;
    @FXML private VBox sidebarMenu;
    @FXML private Button themeBtn;    // fx:id="themeBtn"
    @FXML private Button sidebarBtn;  // fx:id="sidebarBtn"

    private boolean isDarkTheme = true;
    private AuthLayoutController parent;

    // ======================  init  ======================

    @FXML
    public void initialize() {
        initBookMark();
    }

    /** Appelé par AuthLayoutController.initialize() après l'injection FXML. */
    public void setParent(AuthLayoutController parent) {
        this.parent = parent;
    }

    // ====================== actions FXML ======================

    /** Bascule entre le thème sombre et le thème clair sur toute la scène. */
    @FXML
    private void handleToggleTheme() {
        var root = themeBtn.getScene().getRoot();
        if (isDarkTheme) {
            root.getStyleClass().remove("dark-theme");
            root.getStyleClass().add("light-theme");
        } else {
            root.getStyleClass().remove("light-theme");
            root.getStyleClass().add("dark-theme");
        }
        isDarkTheme = !isDarkTheme;
    }

    // ------------------------------------------------------------------ sidebar

    public void handleToggleSidebar() {
        boolean estVisible = sidebarMenu.isVisible();
        sidebarMenu.setVisible(!estVisible);
        sidebarMenu.setManaged(!estVisible);
    }

    private void initBookMark(){
        try {
            List<ServerBookmark> bookMark_saved= ServerBookmarkService.charger();
            for (ServerBookmark mark : bookMark_saved){
                addBookmarkUtils(mark);
            }
        } catch (IOException e) {
            Label error = new Label("chargement des marqupage impossible");
            BookMarkList.getChildren().add(error);
            e.printStackTrace();
        }
    }

    private List<ServerBookmark> getAllBookmarks() {
        return BookMarkList.getChildren().stream()
            .map(node -> (ServerBookmark) node.getUserData())
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Crée une ligne visuelle liée à un BookmarkServer et l'ajoute à la liste.
     */
    private void addBookmarkUtils(ServerBookmark bookmark) {

        // --- Labels
        Label nameLabel = new Label(bookmark.getName());
        nameLabel.getStyleClass().add("input-label");
        nameLabel.setStyle("-fx-font-size: 14;");

        Label ipLabel = new Label(bookmark.getIp());
        ipLabel.getStyleClass().add("input-label");
        ipLabel.setStyle("-fx-font-size: 14;");

        VBox textBox = new VBox(nameLabel, ipLabel);

        // --- Bouton supprimer
        FontIcon trashIcon = new FontIcon("fas-trash");
        trashIcon.setIconSize(20);
        trashIcon.setIconColor(javafx.scene.paint.Color.WHITE);

        Button deleteBtn = new Button();
        deleteBtn.getStyleClass().add("envoi-btn-container");
        deleteBtn.setGraphic(new StackPane(trashIcon));

        // --- La ligne HBox
        HBox row = new HBox(textBox, deleteBtn);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);

        // On attache le BookmarkServer à la ligne
        row.setUserData(bookmark);

        // Le bouton sait quelle ligne (et donc quel bookmark) supprimer
        deleteBtn.setOnAction(e -> {
            ServerBookmark toDelete = (ServerBookmark) row.getUserData();
            BookMarkList.getChildren().remove(row);
            System.out.println("Supprimé : " + toDelete.getName());
        });

        BookMarkList.getChildren().add(row);
    }
}
