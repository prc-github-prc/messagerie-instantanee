package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import messagerie_instantanee.server.models.*;

public interface InterfaceSujetDiscussion extends Remote {
    public void inscription(InterfaceAffichageClient c, User user) throws RemoteException;
    public void desInscription(InterfaceAffichageClient c, int id_user) throws RemoteException;
    public void diffuse(String Message) throws RemoteException;
}
