package messagerie_instantane.server;


import java.rmi.RemoteException;
import messagerie_instantane.interfaces.*;

public class Discussion implements InterfaceSujetDiscussion{

    @Override
    public void inscription(InterfaceAffichageClient c) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'inscription'");
    }

    @Override
    public void desInscription(InterfaceAffichageClient c) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'desInscription'");
    }

    @Override
    public void diffuse(String Message) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'diffuse'");
    }
    
}
