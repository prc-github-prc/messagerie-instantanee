package messagerie_instantanee.server.utils;

import java.util.List;

public class Equal {
    
    public static  <T> boolean Contient(List<T> list, T objet){
        boolean oui =false;
        for(T obj: list){
            oui= oui || obj.equals(objet);
        }
        return oui;
    }
}
