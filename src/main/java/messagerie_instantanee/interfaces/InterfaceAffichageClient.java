package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import messagerie_instantanee.server.models.Message;

/**
 * Interface d'affichage client.
 */
public interface InterfaceAffichageClient extends Remote {
    /**
     * 
     * @param Message
     * @throws RemoteException
     * 
     * Affiche un message chez le client.
     */
    public void affiche(Message Message) throws RemoteException;

    public int getId_user() throws RemoteException;
}
