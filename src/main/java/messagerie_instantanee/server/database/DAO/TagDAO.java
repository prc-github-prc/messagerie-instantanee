package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Tag;

import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToDiscussion;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToTag;


/**
 * DAO pour les Tags.
 */
public class TagDAO {

    /*===================================Méthodes de recherche=======================================*/
    /**
     * 
     * @param nom_tag
     * @return
     */
    public static List<Discussion> findDiscussionListByNomTag(String nom_tag){
        try{
            ResultSet discussion_data = executeSQLQuerry("SELECT * FROM Discussion d NATURAL JOIN Tags t WHERE t.nom_tag = " + nom_tag);
            return rsToDiscussion(discussion_data);
        } catch (SQLException e) {
            System.out.println("[TagDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[TagDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }
    /**
     * 
     * @param id_discussion
     * @return
     */
    public static List<Tag> findTagByIdDiscussion(int id_discussion){
        try{
            ResultSet tag_data = executeSQLQuerry("SELECT * FROM Tag t NATURAL JOIN Tags ts NATURAL JOIN Discussion d WHERE d.id_discussion = " + id_discussion);
            return rsToTag(tag_data);
        } catch (SQLException e) {
            System.out.println("[TagDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[TagDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    /*===================================Méthodes d'insertion=======================================*/

    /**
     * 
     * @param id_discussion
     * @param nom_tag
     */
    public static void insertTag(String nom_tag, int id_discussion) throws SQLException{
        try{
            executeSQLQuerry("INSERT INTO Tag VALUES ("+ nom_tag +") ON CONFLICT IGNORE"); //TODO a test
            executeSQLQuerry("INSERT INTO Tags VALUES ("+ nom_tag +","+ id_discussion +") ON CONFLICT ROLLBACK");
        } catch (SQLException e) {
            System.out.println("[TagDAO] connexion impossible a la base de donnée" + e.getMessage());
            throw e;
        } 
    }

    /**
     * 
     * @param id_discussion
     * @param nom_tag
     */
    public static void deleteTagDiscussion(String nom_tag, int id_discussion) throws SQLException{
        try {
            executeSQLQuerry("DELETE FROM Tags WHERE nom_tag = "+ nom_tag + "AND id_discussion = " + id_discussion +" ON CONFLICT ROLLBACK");
        } catch (SQLException e) {
            System.out.println("[TagDAO] Impossible de supprimer le lien dans Tags : " + e.getMessage());
            throw e;
        }
    }    
}
