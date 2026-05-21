package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Discussion;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToDiscussion;;

public class DiscussionDAO {
    public static Discussion findDiscussionById(int id_discussion){
        try{
            ResultSet discussion_data = executeSQLQuerry("SELECT * FROM Discussion WHERE id_discussion = " + id_discussion);
            return rsToDiscussion(discussion_data).getFirst();
        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[DiscutionDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }
}
