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
     * Désinscrit un User à un salon (et la discussion associée).
     * 
     * @param c
     * @param user
     * @throws RemoteException
     */
    public void desInscription(InterfaceAffichageClient c) throws RemoteException;

    /**
     * Diffuse un message à tous les participants.
     * 
     * @param Message
     * @param username
     * @throws RemoteException
     */
    public void diffuse(String Message, String username) throws RemoteException;
}
