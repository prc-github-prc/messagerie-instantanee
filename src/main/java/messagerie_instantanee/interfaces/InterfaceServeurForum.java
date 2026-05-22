package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceServeurForum extends Remote {
    /**
     * 
     * @param titre
     * @return
     * @throws RemoteException
     * 
     * Obtient un salon à partir d'un titre de discussion.
     */
    public InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException;
}

