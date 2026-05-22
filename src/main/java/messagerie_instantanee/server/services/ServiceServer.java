package messagerie_instantanee.server.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import messagerie_instantanee.server.Salon;
import messagerie_instantanee.server.models.Discussion;

/**
 * Serveur de services.
 */
public class ServiceServer {
    /**
     * 
     * @param salons
     * @return
     * 
     * Transforme une collection de salons en liste de discussions.
     */
    public static List<Discussion> salonToDiscussion(Collection<Salon> salons){
        List<Discussion> lst_discussions = new ArrayList<>();
        for(Salon salon : salons){
            lst_discussions.add(new Discussion(salon.getSalonId(), salon.getSalonNom(), null, false));
        }
        return lst_discussions;
    }

    public static Salon discussionToSalon(Discussion discussion){
        Salon salon;
            try {
                salon = new Salon(discussion);
            } catch (RemoteException e) {
                throw new RuntimeException(" Porblème lors de la transformation des discussions en salons"+e.getMessage());
            }
        return salon;
    }
}
