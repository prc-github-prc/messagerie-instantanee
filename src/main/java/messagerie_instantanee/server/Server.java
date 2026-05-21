package messagerie_instantanee.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import messagerie_instantanee.interfaces.*;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;

public class Server implements InterfaceServeurForum {

    private Map<String, Salon> map_salons = new HashMap<>();

    public Server() throws RemoteException {
        for(Discussion conv : DiscussionDAO.findAllDiscussions()){ // implementer dans discussionDAO
            map_salons.put(conv.getNom_discussion(), new Salon(conv));
        } 
    }

    @Override
    public synchronized InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException {
        // crée le salon à la volée s'il n'existe pas
        map_salons.putIfAbsent(titre, new Salon(titre));
        return map_salons.get(titre);
    }

    /** Retourne la liste des titres de salons disponibles. */
    public synchronized List<String> listerSalons() {
        return salonToDiscussion(map_salons.keySet());
    }
}
