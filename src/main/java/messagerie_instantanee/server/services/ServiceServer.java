package messagerie_instantanee.server.services;

import java.util.ArrayList;
import java.util.List;

import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.Salon;

public class ServiceServer {
    public List<Discussion> salonToDiscussion(List<Salon> salons){
        List<Discussion> lst_discussions = new ArrayList<>();
        for(Salon salon : salons){
            lst_discussions.add(new Discussion(salon., null, null))
        }
    }
}
