package rennes.istic.l2.projet_defi;

import java.rmi.*;

public interface InterfaceServeurForum extends Remote {
    public InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException;
}
