package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import messagerie_instantanee.server.models.Discussion;

/**
 * Interface de serveur de forum.
 */
public interface InterfaceServeurForum extends Remote {
    InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException;
    Boolean checkId(String pseudo, String pwd_hash) throws RemoteException;
    List<Discussion> listerSalons() throws RemoteException;
    Boolean creationUser(String username, String hash) throws RemoteException;
    Discussion creationSalon(String nom_salon, String pseudo_owner, Boolean salon_pricvee) throws RemoteException;
    List<Discussion> getDiscussionsHidedByUser(String username) throws RemoteException;
    void hideDiscussion(int id_discussion, String username) throws RemoteException;
}
