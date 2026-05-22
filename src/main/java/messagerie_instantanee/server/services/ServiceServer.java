package messagerie_instantanee.server.services;

import java.rmi.RemoteException;

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
    public static Discussion salonToDiscussion(Salon salon){
        return new Discussion(salon.getSalonId(), salon.getSalonNom(), null, false);
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
