package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

import messagerie_instantanee.server.models.Message;

/**
 * Interface de sujet de discussion.
 */
public interface InterfaceSujetDiscussion extends Remote {

    public void inscription(InterfaceAffichageClient c) throws RemoteException; //inscrit un client au salon
    public void desinscription(InterfaceAffichageClient c) throws RemoteException; // retire un client du salon
    public void diffuse(String Message, String username) throws RemoteException; // partege un message a tous les inscrit
    public Stack<Message> getArchive() throws RemoteException;
}
