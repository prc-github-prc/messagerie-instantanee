package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Discussion;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToDiscussion;;


public class HideDAO {
    public static List<Discussion> findHidedDiscussionListByIdUser(int id_user){
        try{
            ResultSet discussion_data = executeSQLQuerry("SELECT * FROM Discussion d NATURAL JOIN Hide h WHERE h.id_user = " + id_user);
            return rsToDiscussion(discussion_data);
        } catch (SQLException e) {
            System.out.println("[HideDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[HideDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }
}
