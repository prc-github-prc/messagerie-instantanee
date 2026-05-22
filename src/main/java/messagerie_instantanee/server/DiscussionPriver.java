package messagerie_instantanee.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Message;
import messagerie_instantanee.server.models.User;

public class DiscussionPriver extends Salon {
    
    private int id;
    private String nom;
    List<User> participants;
    List<Message> messages;


    /**
     * @param id
     * @throws RemoteException
     * 
     * Crée une discussion priver à partir d'une discussion déjà existante.
     */
    public DiscussionPriver(Discussion discussion) throws RemoteException{
        super(discussion);
    }

    /**
     * @param nom
     * @param user
     * @throws RemoteException
     * 
     * Crée une discussion priver ainsi qu'une nouvelle discussion.
     */
    public DiscussionPriver(String nom, User user) throws RemoteException {
        super(nom, user);
    }
}
