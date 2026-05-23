package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
    public void affiche(String Message) throws RemoteException;

    public int getId_user();
}
