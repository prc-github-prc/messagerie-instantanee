package messagerie_instantanee.server.services;

import java.util.ArrayList;
import java.util.List;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;
import messagerie_instantanee.server.models.User;

public class ServiceSalon {
    
    public static List<User> InterfacToUserMinusOwner(List<InterfaceAffichageClient> lst_participant, InterfaceAffichageClient owner){
        List<User> new_list= new ArrayList<>();
        for(InterfaceAffichageClient client : lst_participant){
            if(client != owner){new_list.add((User) client);}
        }
        return new_list;
    }
}
