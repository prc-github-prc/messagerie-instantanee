package messagerie_instantanee.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import messagerie_instantanee.interfaces.*;
import messagerie_instantanee.server.Salon;


public class Client implements InterfaceAffichageClient {

    @FXML
    private TextArea zoneAffichage; // TextArea JavaFX
    @FXML
    private TextField zoneSaisie;   // champ de saisie de message

    private InterfaceSujetDiscussion sujetActuel;

    public static final String URL_PAR_DEFAUT = "//localhost:1099/leServeur";

    private String cookie;

    public Client() throws RemoteException {
        // Comme on ne peut pas hériter de UnicastRemoteObject, 
        // on exporte l'objet manuellement pour le rendre accessible par RMI
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void affiche(String message) throws RemoteException {
        // TRÈS IMPORTANT : On bascule l'exécution sur le Thread JavaFX
        Platform.runLater(() -> {
            zoneAffichage.appendText(message + "\n");
        });
    }
}
