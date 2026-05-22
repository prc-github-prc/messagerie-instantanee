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
import static messagerie_instantanee.server.database.DAO.UserDAO.findUserByUsername;
import static messagerie_instantanee.server.database.DAO.UserDAO.insertUser;
import static messagerie_instantanee.server.database.DAO.DiscussionDAO.insertDiscussionReturnId;

import messagerie_instantanee.server.database.DatabaseLaucher;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.services.ServiceServer.salonToDiscussion;

public class Server extends UnicastRemoteObject implements InterfaceServeurForum {

    private Map<String, Salon> map_salons = new HashMap<>();

    /**
     * Démarre le Serveur.
     * @throws RemoteException
     */
    public Server() throws RemoteException {
        DatabaseLaucher.initialiser();
        List<Discussion> convs = DiscussionDAO.findAllDiscussions();
        if(convs!= null){
            for(Discussion conv : convs){ // implementer dans discussionDAO
                map_salons.put(conv.getNom_discussion(), new Salon(conv));
            }
        }
    }

    /**
     * @param titre
     * @return
     * 
     * Crée un salon à partir du titre de la discussion.
     */
    @Override
    public synchronized InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException {
        // crée le salon à la volée s'il n'existe pas
        if(map_salons.get(titre) == null){
            throw new NoSuchElementException();
        }
        return map_salons.get(titre);
    }

    /** Retourne la liste des titres de salons disponibles. */
    public List<Discussion> listerSalons() {
        return salonToDiscussion(map_salons.values());
    }

    /**
     * verifie les information d'un utilisateur
     * 
     * @param pseudo de l'utilisateur
     * @return si l'utilisateur peut se connecter  
     */
    public Boolean checkId(String pseudo, String pwd_hash){
        User user = findUserByUsername(pseudo);
        if(user.getPassword_hash().compareTo(pwd_hash) == 0){
            return true;
        }
        return false;
    }

    /* Cree un user renvoie une exception si le pseudo est deja pris */
    public Boolean creationUser(String username, String hash) throws IllegalSelectorException{
        boolean creation = false;
        try{
            insertUser(username, hash);
            return true;
        }catch(SQLException e){
            throw new IllegalSelectorException();
        }
    }

    /* cree une discussion et cree un salon */
    public Discussion creationSalon(String nom_Salon,String username,Boolean est_privee){
        ArrayList<User> users = new ArrayList<>(); 
        User host = findUserByUsername(username);
        int id = insertDiscussionReturnId(nom_Salon,est_privee, host,users);
        users.add(host);
        return new Discussion(id, nom_Salon, users, est_privee);
    }
}
