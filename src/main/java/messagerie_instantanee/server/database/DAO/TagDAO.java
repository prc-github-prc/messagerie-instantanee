package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Tag;

import static messagerie_instantanee.server.utils.ExecSqlQuerry.excuteInsertSQL;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToDiscussion;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToTag;

/**
 * DAO pour les Tags.
 */
public class TagDAO {

    /*===================================Méthodes de recherche=======================================*/

    public static List<Discussion> findDiscussionListByNomTag(String nom_tag) {
        try {
            // CORRIGÉ : quotes manquantes autour de nom_tag (valeur texte)
            ResultSet discussion_data = executeSQLQuerry(
                "SELECT * FROM Discussion d NATURAL JOIN Tags t WHERE t.nom_tag = '" + nom_tag + "'"
            );
            return rsToDiscussion(discussion_data);
        } catch (SQLException e) {
            System.out.println("[TagDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[TagDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    public static List<Tag> findTagByIdDiscussion(int id_discussion) {
        try {
            ResultSet tag_data = executeSQLQuerry(
                "SELECT * FROM Tag t NATURAL JOIN Tags ts NATURAL JOIN Discussion d WHERE d.id_discussion = " + id_discussion
            );
            return rsToTag(tag_data);
        } catch (SQLException e) {
            System.out.println("[TagDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[TagDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    /*===================================Méthodes d'insertion=======================================*/

    public static void insertTag(String nom_tag, int id_discussion) throws SQLException {
        try {
            excuteInsertSQL("INSERT OR IGNORE INTO Tag VALUES ('" + nom_tag + "')");
            excuteInsertSQL("INSERT OR ROLLBACK INTO Tags VALUES ('" + nom_tag + "'," + id_discussion + ")");
        } catch (SQLException e) {
            System.out.println("[TagDAO] Impossible d'insérer le tag : " + e.getMessage());
            throw e;
        }
    }

    public static void deleteTagDiscussion(String nom_tag, int id_discussion) throws SQLException {
        try {
            excuteInsertSQL(
                "DELETE FROM Tags WHERE nom_tag = '" + nom_tag + "' AND id_discussion = " + id_discussion
            );
        } catch (SQLException e) {
            System.out.println("[TagDAO] Impossible de supprimer le tag : " + e.getMessage());
            throw e;
        }
    }
}
