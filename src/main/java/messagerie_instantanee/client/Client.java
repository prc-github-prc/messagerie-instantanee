package messagerie_instantanee.client;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;

import static messagerie_instantanee.server.database.DAO.UserDAO.findUserByUsername;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implémentation du callback RMI côté client.
 * Le serveur appelle affiche() pour pousser les messages reçus.
 * Le Consumer<String> reçu à la construction transmet le message au contrôleur JavaFX.
 */
public class Client extends UnicastRemoteObject implements InterfaceAffichageClient {

    private final java.util.function.Consumer<String> onMessage;
    String pseudo;

    public Client(java.util.function.Consumer<String> onMessage, String pseudo) throws RemoteException {
        this.onMessage = onMessage;
        this.pseudo = pseudo; // CORRIGÉ : pseudo n'était jamais assigné → getId_user() plantait en NPE
    }

    /**
     * Appelé par le serveur (thread RMI) quand un message arrive.
     * On délègue au Consumer qui appellera Platform.runLater() dans le contrôleur.
     */
    @Override
    public void affiche(String message) throws RemoteException {
        onMessage.accept(message);
    }

    @Override
    public int getId_user() throws RemoteException {
        return findUserByUsername(pseudo).getId_user();
    }
}
