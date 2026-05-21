package messagerie_instantanee.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.Salon;

public class ServiceServer {
    public static List<Discussion> salonToDiscussion(Collection<Salon> salons){
        List<Discussion> lst_discussions = new ArrayList<>();
        for(Salon salon : salons){
            lst_discussions.add(new Discussion(salon.getSalonId(), salon.getSalonNom(), null));
        }
        return lst_discussions;
    }
}
