package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Discussion;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.excuteInsertSQL;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToDiscussion;;

/**
 * DAO pour les Hide.
 */
public class HideDAO {
    /*=======================Méthode de Lecture====================================================== */
    /**
     * 
     * @param id_user
     * @return
     */
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

    /*================================Méthode d'écriture==================================================== */
    /**
     * 
     * @param id_discussion
     * @param id_user
     */
    public static void addHideDiscussionByIdDiscussion(int id_discussion, int id_user) throws SQLException{
        try {
            excuteInsertSQL("INSERT OR ROLLBACK INTO Hide VALUES ("+id_user+","+id_discussion+")");
        } catch (SQLException e) {
            System.out.println("[HideDAO] Impossible créer le lien dans hide : " + e.getMessage());
            throw e;
        }
    }

    /**
     * 
     * @param id_discussion
     * @param id_user
     */
    public static void deleteHideDiscussionByIdDiscussion(int id_discussion, int id_user) throws SQLException{
        try {
            excuteInsertSQL("DELETE OR ROLLBACK FROM Hide  WHERE id_user="+id_user+"AND id_discussion="+id_discussion+" ON CONFLICT ");
        } catch (SQLException e) {
            System.out.println("[HideDAO] Impossible de supprimer le lien dans hide : " + e.getMessage());
            throw e;
        }
    }
}
