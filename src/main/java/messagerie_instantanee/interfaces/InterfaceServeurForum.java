package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import messagerie_instantanee.server.models.Discussion;

public interface InterfaceServeurForum extends Remote {
    InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException;
    Boolean checkId(String pseudo, String pwd_hash) throws RemoteException;
    List<Discussion> listerSalons() throws RemoteException;
    Boolean creationUser(String username, String hash) throws RemoteException;
}
