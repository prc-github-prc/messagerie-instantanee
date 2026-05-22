package messagerie_instantanee.server;

import java.nio.channels.IllegalSelectorException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import static messagerie_instantanee.server.database.DAO.DiscussionDAO.insertDiscussionReturnId;
import static messagerie_instantanee.server.database.DAO.UserDAO.findUserByUsername;
import static messagerie_instantanee.server.database.DAO.UserDAO.insertUser;
import messagerie_instantanee.server.database.DatabaseLaucher;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.services.ServiceServer.salonToDiscussion;


public class Server extends UnicastRemoteObject implements InterfaceServeurForum {

    private Map<String, Salon> map_salons = new HashMap<>();

    public Server() throws RemoteException {
        DatabaseLaucher.initialiser();
        List<Discussion> convs = DiscussionDAO.findAllDiscussions();
        if (convs != null) {
            for (Discussion conv : convs) {
                map_salons.put(conv.getNom_discussion(), new Salon(conv));
            }
        }
    }

    @Override
    public synchronized InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException {
        if (map_salons.get(titre) == null) {
            throw new NoSuchElementException();
        }
        return map_salons.get(titre);
    }

    @Override
    public List<Discussion> listerSalons() throws RemoteException {
        return salonToDiscussion(map_salons.values());
    }

    @Override
    public Boolean checkId(String pseudo, String rawPassword) throws RemoteException {
        User user = findUserByUsername(pseudo);
        // Utilisateur introuvable → false (pas de NPE)
        if (user == null) return false;
        return rawPassword.equals(user.getPassword());
    }

    @Override
    public Boolean creationUser(String username, String hash) throws RemoteException {
        try {
            insertUser(username, hash);
            return true;
        } catch (SQLException e) {
            throw new IllegalSelectorException();
        }
    }

    /* cree une discussion et cree un salon */
    public Discussion creationSalon(String nom_Salon,String username,Boolean est_privee) throws RemoteException{
        ArrayList<User> users = new ArrayList<>(); 
        User host = findUserByUsername(username);
        int id = insertDiscussionReturnId(nom_Salon,est_privee, host,users);
        users.add(host);
        Discussion new_discussion = new Discussion(id, nom_Salon, users, est_privee);
        map_salons.put(nom_Salon, new Salon(new_discussion)) ;
        return new_discussion; //RECHECK
    }
}
