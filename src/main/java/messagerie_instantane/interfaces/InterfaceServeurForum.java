package messagerie_instantane.interfaces;
import java.rmi.*;

public interface InterfaceServeurForum extends Remote {
    public InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException;
}

