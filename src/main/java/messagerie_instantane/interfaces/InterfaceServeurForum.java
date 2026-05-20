package messagerie_instantane.commun;
import java.rmi.*;

public interface InterfaceServeurForum extends Remote {
    public InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException;
}

