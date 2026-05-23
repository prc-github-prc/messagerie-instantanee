package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Message;
import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.excuteInsertSQL;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToDiscussion;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToMessage;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToUser;

/**
 * DAO pour les discussions.
 */
public class DiscussionDAO {

    /*=====================Methode de  lecture ============================= */
    /**
     * 
     * @param id_discussion
     * @return
     */
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

    /**
     * 
     * @param id_discussion
     * @return
     */
    public static List<InterfaceAffichageClient> findUserByIDDiscussion(int id_discussion) {
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

    /**
     * 
     * @return
     */
    public static List<Discussion> findAllDiscussions(){
        try {
            ResultSet data_discussions = executeSQLQuerry("SELECT * FROM Discussion");
            return rsToDiscussion(data_discussions);
        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[DiscussionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    /**
     * 
     * @param id_discussion
     * @return
     */
    public static List<Message> findMessagesByIdDiscussion( int id_discussion){
        try {
            ResultSet data_discussions = excuteInsertSQL("SELECT * FROM Message WHERE id_discussion ="+id_discussion);
            return rsToMessage(data_discussions);
        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[DiscutionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }


    /*==============Methode d'écriture ================ */

    /**
     * 
     * @param titre
     * @param user
     * @param users
     */
    public static void insertDiscussion(String titre,Boolean est_prive,User user, List<User> users){
        try {
            ResultSet rs=excuteInsertSQL("INSERT INTO Discussion(nom_discussion, est_prive) VALUES ("+ titre+","+ est_prive +")");
            int clé=0;
            while(rs.next()){
                clé=rs.getInt("id_discussion");
            }
            String user_discussion = "INSERT INTO Roles Values ("+ user.getId_user()+","+ clé+","+ "1)";
            
            if(!users.isEmpty()){
                user_discussion+=",";
                for(User u : users){
                     if(u.getId_user() != user.getId_user()){ //évite d'insérer le créateur deux fois
                        user_discussion += "("+ u.getId_user()+","+ clé+","+ "0)";
                    }
                }
            }
            excuteInsertSQL(user_discussion);

        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] La discussion n'a pas pu être crée : " + e.getMessage());
        }
    }

    /**
     * 
     * @param titre
     * @param user
     * @param users
     * @return la clé
     */
    public static int insertDiscussionReturnId(String titre,Boolean est_prive,User user, List<User> users){
        try {
            ResultSet rs=excuteInsertSQL("INSERT INTO Discussion(nom_discussion, est_prive) VALUES ('"+ titre+"',"+ est_prive +")");
            int clé=0;
            while(rs.next()){
                clé=rs.getInt("id_discussion");
            }
            String user_discussion = "INSERT INTO Roles Values ("+ user.getId_user()+","+ clé+","+ "1)";
            
            if(!users.isEmpty()){
                user_discussion+=",";
                for(User u : users){
                    if(u.getId_user() != user.getId_user()){ //évite d'insérer le créateur deux fois
                        user_discussion += "("+ u.getId_user()+","+ clé+","+ "0)";
                    }
                }
            }
            excuteInsertSQL(user_discussion);
            return clé;

        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] La discussion n'a pas pu être crée : " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 
     * @param id_user
     * @param id_discussion
     */
    public static void addUserToDiscussionById(int id_user ,int id_discussion){
        try {
            excuteInsertSQL("INSERT INTO Role VALUES ("+ id_user+","+ id_discussion +"0)");
        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] La discussion n'a pas pu être crée : " + e.getMessage());
        }

    }
    
    /**
     * 
     * @param id_user
     * @param id_discussion
     */
    public static void RemoveUserToDiscussionById(int id_user ,int id_discussion){
        try {
            executeSQLQuerry("DELETE FROM Role WHERE id_discussion ="+id_discussion + "AND id_user="+id_user);
        } catch (SQLException e) {
            System.out.println("[DiscutionDAO] La discussion n'a pas pu être crée : " + e.getMessage());
        }

    }
    
}
