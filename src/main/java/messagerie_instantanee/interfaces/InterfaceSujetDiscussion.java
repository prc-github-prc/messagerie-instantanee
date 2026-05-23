package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface de sujet de discussion.
 */
public interface InterfaceSujetDiscussion extends Remote {
    /**
     * 
     * @param c
     * @param user
     * @throws RemoteException
     * 
     * Inscrit un User à un salon (et la discussion associée).
     */
    public void inscription(InterfaceAffichageClient c) throws RemoteException;

    /**
     * 
     * @param c
     * @param user
     * @throws RemoteException
     * 
     * Désinscrit un User à un salon (et la discussion associée).
     */
    public void desInscription(InterfaceAffichageClient c) throws RemoteException;

    /**
     * 
     * @param Message
     * @param username
     * @throws RemoteException
     * 
     * Diffuse un message à tous les participants.
     */
    public void diffuse(String Message, String username) throws RemoteException;
}
