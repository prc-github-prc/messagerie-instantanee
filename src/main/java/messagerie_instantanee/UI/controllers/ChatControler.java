package messagerie_instantanee.UI.controllers;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import messagerie_instantanee.client.Client;
import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.models.Discussion;

/**
 * Représente un chatcontroler.
 */
public class ChatControler {

    @FXML private ListView<Discussion> salonList;
    @FXML private Label salonLabel;  //la liste des salon
    @FXML private VBox messagesBox;  
    @FXML private ScrollPane scrollPane;  //le layout des message
    @FXML private TextField inputField; //entrée de l'utilisateur
    @FXML private VBox headerChatBox; // partie haute du layout de message
    @FXML private Label tagsLabel;  //laebl de tag
    @FXML private Label titreLabel; //label de titre.
    @FXML private Button usernameLink;

    // InterfaceServeurForum au lieu de Server
    private InterfaceServeurForum serveur;
    private InterfaceSujetDiscussion currentSalon;
    private Client clientRMI;
    private String pseudo;

    @FXML 
    public void initialize(){
        salonList.getItems().clear();
        salonList.getSelectionModel().selectedItemProperty().addListener((observable, ancienSalon, nouveauSalon) -> {
            if (nouveauSalon != null && serveur != null) {
                try {
                    currentSalon = serveur.obtientSujet(nouveauSalon.getNom_discussion());
                    
                    Platform.runLater(() -> {
                        titreLabel.setText(nouveauSalon.getNom_discussion());
                        setChatVisible(true);
                        if (tagsLabel != null) {
                            tagsLabel.setText("#discussion");
                        }
                        System.out.println("Salon : " + nouveauSalon.getNom_discussion());
                    });
                } catch (RemoteException e) {
                    System.err.println("Erreur lors du clic : " + e.getMessage());
                }
            }
        });
    }

    public void configurerSession(InterfaceServeurForum server, String pseudo) {
        this.pseudo = pseudo;
        this.serveur = server;
    salonList.getItems().clear();
        try {
            clientRMI = new Client(msg ->
                Platform.runLater(() -> afficherBulle(msg, false)),
            pseudo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        salonList.getItems().clear();
        try {
            List<Discussion> lst_salon = server.listerSalons();
            if(lst_salon != null && !lst_salon.isEmpty()){
                salonList.getItems().addAll(lst_salon);
                Platform.runLater(() -> {
                    if (usernameLink != null) {
                        usernameLink.setText(pseudo); 
                    }
                    if (titreLabel != null) {
                        titreLabel.setText("Bienvenue " +pseudo + " !");
                    }
                    if (tagsLabel != null) {
                        tagsLabel.setText("Choisis un salon à gauche pour commencer à discuter");
                    }
                    setChatVisible(false);
                });
            }
            else{
                currentSalon = null;
                titreLabel.setText("Aucun salon disponible");
            }
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        

        salonList.getSelectionModel().selectedItemProperty().addListener((observable, ancienSalon, nouveauSalon) -> {
            if (nouveauSalon != null) {
                try{
                    currentSalon = serveur.obtientSujet(nouveauSalon.getNom_discussion());
                    Platform.runLater(() -> {
                        tagsLabel.setText("#discussion");
                        titreLabel.setText(nouveauSalon.getNom_discussion());
                    });
                }
                catch (RemoteException e) {
                    System.out.println("Erreur lors de la sélection du salon : " + e.getMessage());
                }
                
            }
        });
    }

    // ==================================== mets en forme un message =====================================
    private void afficherBulle(String msg, boolean estMoi) {
        Label message = new Label(msg);
        message.getStyleClass().add("bulle-message");
        message.setWrapText(true);
        message.setMaxWidth(300);
        VBox conteneur = new VBox(message);
        // Bug 6 corrigé : alignement inversé (mes messages à droite, les autres à gauche)
        if (estMoi) {
            conteneur.setAlignment(Pos.CENTER_RIGHT);
        } else {
            conteneur.setAlignment(Pos.CENTER_LEFT);
        }
        messagesBox.getChildren().add(conteneur);
    }

    @FXML
    public void getCurrentSalon(MouseEvent event){
        Discussion clicked = salonList.getSelectionModel().getSelectedItem();
        if (clicked == null) {
            return;
        }
        try {
            currentSalon = serveur.obtientSujet(clicked.getNom_discussion());
        } catch (RemoteException e) {
            throw new RuntimeException("Impossible de récupérer le salon distant : " + e.getMessage(), e);
        }
    }

    private void rejoindre(String titre) {
        try {//TODO finish me
            if (currentSalon != null && clientRMI != null) {
                currentSalon.desinscription(clientRMI);
            }
            currentSalon = serveur.obtientSujet(titre);
            currentSalon.inscription(clientRMI);
            salonLabel.setText("# " + titre);
            messagesBox.getChildren().clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // ====================================== envoy un message ==================================
    @FXML
    public void actionEnvoi() {
        String texte = inputField.getText().trim();

        if (texte.isEmpty()) {
            return;
        }

        if (currentSalon == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aucun salon sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un salon avant d'envoyer un message.");
            alert.showAndWait();
            return;
        }

        try {
            currentSalon.diffuse(texte, pseudo);
            inputField.clear();
        } catch (RemoteException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur d'envoi");
            alert.setHeaderText(null);
            alert.setContentText("Un problème est arrivé lors de la diffusion : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void createNewTag(ActionEvent event) {
        if (currentSalon == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un salon avant d'ajouter un tag ");
            alert.showAndWait();
            return; 
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau tag");
        dialog.setContentText("Nom du tag :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tag -> {   
            String tagNettoye = tag.trim();
            if (!tagNettoye.isEmpty()) {
                if (!tagNettoye.startsWith("#")) {
                    tagNettoye = "#" + tagNettoye;
                }
                String tagsActuels = tagsLabel.getText();
                if (tagsActuels == null || tagsActuels.isEmpty()) {
                    tagsLabel.setText(tagNettoye);
                } else {
                    tagsLabel.setText(tagsActuels + " " + tagNettoye);
                }
            }
        }); 
    }

    // ======================== evite les erreur de compilation des bouton pas relier ==========================
    @FXML
    private void doNothings(){
        return;
    }

    // ====================== crée un nouveaux salon (et le selctione auto) ====================
    @FXML
    private void createNewSalon(ActionEvent event) {
        if (serveur == null) {
            System.out.println(serveur);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur serveur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de créer un salon : le serveur n'est pas initialisé.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Salon");
        dialog.setContentText("Nom du salon :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nomSalon -> {   
        String nom_Salon = nomSalon.trim();
        
        if (!nom_Salon.isEmpty()) {
            boolean existe = salonList.getItems().stream()
                .anyMatch(d -> nom_Salon.equals(d.getNom_discussion()));//TODO Est qu'on envlève la verification de nom car dans ce qu'on a def c'est possible d'avoir 2 salon avec le même nom
            if (existe) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Ce salon existe déjà ");
                alert.showAndWait();
            } else {
                try{
                    Discussion nouveauSalon = serveur.creationSalon(nom_Salon, pseudo, false);
                    salonList.getItems().add(nouveauSalon);
                    salonList.getSelectionModel().select(nouveauSalon);
                    currentSalon = serveur.obtientSujet(nom_Salon);
                } catch (Exception e){
                    //TODO mettre l'erreur display quand y'en aura un
                    System.out.println(e.getMessage());
                }
            }
        }}); 
    }

    private void setChatVisible(boolean visible){
        if (scrollPane != null) {
            scrollPane.setVisible(visible);
            scrollPane.setManaged(visible);
        }
        if (inputField != null && inputField.getParent() != null) {
            inputField.getParent().setVisible(visible);
            inputField.getParent().setManaged(visible);
        }
        if (tagsLabel != null) {
            tagsLabel.setVisible(true);
            tagsLabel.setManaged(true);
        }
    }
}
