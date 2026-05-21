package messagerie_instantanee.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceAffichageClient extends Remote {
    public void affiche(String Message) throws RemoteException;
}
