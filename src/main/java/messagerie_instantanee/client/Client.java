package messagerie_instantanee.client;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implémentation du callback RMI côté client.
 * Le serveur appelle affiche() pour pousser les messages reçus.
 * Le Consumer<String> reçu à la construction transmet le message au contrôleur JavaFX.
 */
public class Client extends UnicastRemoteObject implements InterfaceAffichageClient {

    // la gestion du cookie est faitee par le controller 
    //

    private final java.util.function.Consumer<String> onMessage;

    public Client(java.util.function.Consumer<String> onMessage) throws RemoteException {
        this.onMessage = onMessage;
    }

    /**
     * Appelé par le serveur (thread RMI) quand un message arrive.
     * On délègue au Consumer qui appellera Platform.runLater() dans le contrôleur.
     */
    @Override
    public void affiche(String message) throws RemoteException {
        onMessage.accept(message);
    }
}
