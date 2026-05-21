package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.List;

import messagerie_instantanee.server.models.Discussion;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.*;
import messagerie_instantanee.server.models.User;


public class DiscussionDAO {
    public static Discussion findDiscussionById(int id_discussion){
        try{
            ResultSet discussion_data = executeSQLQuerry("SELECT * FROM Discussion WHERE id_discussion = " + id_discussion + ";");
            return rsToDiscussion(discussion_data).getFirst();
        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[DiscutionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    public static List<User> findUserByIDDiscussion(int id_discussion) {
        try{
            ResultSet discussion_users = executeSQLQuerry("SELECT * FROM Roles NATURAL JOIN USER WHERE id_discussion = " + id_discussion + ";");
            return rsToUser(discussion_users);
        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[DiscutionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }
}
