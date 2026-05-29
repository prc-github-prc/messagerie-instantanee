package messagerie_instantanee.UI.controllers;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import messagerie_instantanee.client.models.ServerBookmark;
import messagerie_instantanee.client.services.ServerBookmarkService;
import messagerie_instantanee.server.models.Discussion;

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

    // ============== init ==============

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

    List<ServerBookmark> getBookmarks() {
        return bookMarkList.getChildren().stream()
            .filter(node -> node.getUserData() instanceof ServerBookmark)
            .map(node -> (ServerBookmark) node.getUserData())
            .toList();
    }

    public void handleNewBookmark(){
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner((Stage) bookMarkList.getScene().getWindow()); 
        dialog.setTitle("Nouveau MarquePage");
        dialog.setResizable(false);

        //------Contenu Formulaire
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        // label nom
        Label labelNom = new Label("Nom du marque-page");
        labelNom.getStyleClass().add("dialog-label");
        // champ nom
        TextField textFieldNom    = new TextField();
        textFieldNom.getStyleClass().add("dialog-input");
        textFieldNom.setPromptText("mon serveur");

        // label adress
        Label labelAdress = new Label("Adress du serveur");
        labelAdress.getStyleClass().add("dialog-label");
        // champ adress
        TextField textFieldAdress    = new TextField();
        textFieldAdress.getStyleClass().add("dialog-input");
        textFieldAdress.setPromptText("127.0.0.1");
        
        // label adress
        Label labelPort = new Label("Port du seveur");
        labelPort.getStyleClass().add("dialog-label");
        // champ adress
        TextField textFieldPort    = new TextField();
        textFieldPort.getStyleClass().add("dialog-input");
        textFieldPort.setPromptText("8282");

        vbox.getChildren().addAll(
            labelNom,
            textFieldNom,
            labelAdress,
            textFieldAdress,
            labelPort,
            textFieldPort);

        // Boutons
        Button btnValider = new Button("Valider");
        Button btnAnnuler = new Button("Annuler");
        btnValider.setDefaultButton(true);
        btnAnnuler.setCancelButton(false);
        btnValider.getStyleClass().add("dialog-btn-valider");
        btnAnnuler.getStyleClass().add("dialog-btn-annuler");


        HBox boutons = new HBox(10, btnValider, btnAnnuler);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.setPadding(new Insets(0, 15, 15, 15));
        boutons.getStyleClass().add("dialog-footer");
        // Layout principal
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dialog-root");
        root.setCenter(vbox);
        root.setBottom(boutons);
        
        btnValider.setOnAction(e -> {
            String nom  = textFieldNom.getText().trim();
            String adress  = textFieldAdress.getText().trim();
            int port = Integer.parseInt(textFieldPort.getText().trim());
            if (nom.isEmpty() || adress.isEmpty()) return;
            try {
                ServerBookmark new_bookmark = new ServerBookmark(nom, adress, port);
                addBookmark(new_bookmark);
                //sauvegarde
                save_bookmark_list();
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
            }
            dialog.close();
        });

        btnAnnuler.setOnAction(e -> dialog.close());

        Scene scene = new Scene(root, 300, 200);
        //scene.getStylesheets().addAll(salonList.getScene().getStylesheets());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private boolean save_bookmark_list(){
        try {
            ServerBookmarkService.sauvegarder(getBookmarks());
            return true;
        } catch (IOException e) {
            System.out.println("sauvegarde json impossible");
            e.printStackTrace();
            return false;
        }
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
            save_bookmark_list();
        });

        // Empêche le clic sur "supprimer" de remonter jusqu'à la ligne
        deleteBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);

        row.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            serverField.setText(((ServerBookmark) row.getUserData()).getIp());
        });

        VBox.setMargin(row, new Insets(8, 0, 8, 0));
        row.getStyleClass().setAll("bookmark");
        bookMarkList.getChildren().add(row);
    }
}
