package messagerie_instantanee.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import static messagerie_instantanee.server.database.DAO.UserDAO.findUserByUsername;
import messagerie_instantanee.server.database.DatabaseLaucher;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.services.ServiceServer.salonToDiscussion;

public class Server extends UnicastRemoteObject implements InterfaceServeurForum {

    private Map<String, Salon> map_salons = new HashMap<>();

    public Server() throws RemoteException {
        DatabaseLaucher.initialiser();
        List<Discussion> convs = DiscussionDAO.findAllDiscussions();
        if(convs!= null){
            for(Discussion conv : convs){ // implementer dans discussionDAO
                map_salons.put(conv.getNom_discussion(), new Salon(conv));
            }
        }
    }

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
}
