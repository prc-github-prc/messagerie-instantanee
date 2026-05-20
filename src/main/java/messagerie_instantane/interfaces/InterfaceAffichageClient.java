package messagerie_instantane.commun;

import java.rmi.*;

public interface InterfaceAffichageClient extends Remote {
    public void affiche(String Message) throws RemoteException;
}
