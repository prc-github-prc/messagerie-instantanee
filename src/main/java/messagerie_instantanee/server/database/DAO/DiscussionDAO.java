package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.excuteInsertSQL;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToDiscussion;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToUser;


public class DiscussionDAO {

    /*=====================Methode de  lecture ============================= */
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

    public static List<Discussion> findAllDiscussions(){
        try {
            ResultSet data_discussions = excuteInsertSQL("SELECT * FROM Discussion ");
            return rsToDiscussion(data_discussions);
        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[DiscutionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    /*==============Methode d'écriture ================ */

    public static void insertDiscussion(String titre,User user, List<User> users){
        try {
            ResultSet rs=excuteInsertSQL("INSERT INTO Discussion(titre) VALUES ("+ titre+")");
            int clé=0;
            while(rs.next()){
                clé=rs.getInt("id_discussion");
            }
            String user_discussion = "INSERT INTO Roles Values ("+ user.getId_user()+","+ clé+","+ "1)";
            
            if(!users.isEmpty()){
                user_discussion+=",";
                for(User u : users){
                    user_discussion += "("+ u.getId_user()+","+ clé+","+ "0)";
                }
            }
            executeSQLQuerry(user_discussion);

        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] La discussion n'a pas pu être crée : " + e.getMessage());
        }
    }
    
}
