package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

    /*===================== Méthodes de lecture ============================= */

    public static Discussion findDiscussionById(int id_discussion) {
        try {
            ResultSet discussion_data = executeSQLQuerry(
                "SELECT * FROM Discussion WHERE id_discussion = " + id_discussion
            );
            return rsToDiscussion(discussion_data).getFirst();
        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[DiscussionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    /**
     * Retourne la liste des User (modèle BDD) participants à une discussion.
     * Ne retourne PAS des stubs RMI : les clients connectés sont gérés dans Salon.lst_participants.
     */
    public static List<User> findUserByIDDiscussion(int id_discussion) {
        try {
            ResultSet discussion_users = executeSQLQuerry(
                "SELECT * FROM Roles NATURAL JOIN User WHERE id_discussion = " + id_discussion
            );
            return rsToUser(discussion_users);
        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[DiscussionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    public static List<Discussion> findAllDiscussions() {
        try {
            ResultSet data_discussions = executeSQLQuerry("SELECT * FROM Discussion");
            return rsToDiscussion(data_discussions);
        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[DiscussionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    // CORRIGÉ : utilisait excuteInsertSQL au lieu de executeSQLQuerry
    public static List<Message> findMessagesByIdDiscussion(int id_discussion) {
        try {
            ResultSet data_messages = executeSQLQuerry(
                "SELECT * FROM Messages WHERE id_discussion = " + id_discussion
            );
            return rsToMessage(data_messages);
        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[DiscussionDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return new ArrayList<Message>();
        }
    }

    /*============== Méthodes d'écriture ================ */

    public static void insertDiscussion(String titre, Boolean est_prive, User user, List<User> users) throws SQLException {
        try {
            // CORRIGÉ : INSERT OR ROLLBACK INTO (syntaxe SQLite valide)
            ResultSet rs = excuteInsertSQL(
                "INSERT OR ROLLBACK INTO Discussion(nom_discussion, est_prive) VALUES ('"
                + titre + "', " + est_prive + ")"
            );

            int clé = 0;
            // CORRIGÉ : getInt(1) au lieu de getInt("id_discussion") sur getGeneratedKeys()
            if (rs.next()) {
                clé = rs.getInt(1);
            }
            if (clé == 0) {
                throw new SQLException("[DiscussionDAO] Aucune clé générée pour la nouvelle discussion.");
            }

            // CORRIGÉ : construction propre de la liste de valeurs, sans virgule orpheline
            List<String> valeurs = new ArrayList<>();
            valeurs.add("(" + user.getId_user() + "," + clé + ",1)");

            for (User u : users) {
                if (u.getId_user() != user.getId_user()) {
                    valeurs.add("(" + u.getId_user() + "," + clé + ",0)");
                }
            }

            String user_discussion = "INSERT INTO Roles VALUES " + String.join(",", valeurs);
            excuteInsertSQL(user_discussion);

        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] La discussion n'a pas pu être créée : " + e.getMessage());
            throw e;
        }
    }

    /**
     * @return l'id de la discussion créée
     */
    public static int insertDiscussionReturnId(String titre, Boolean est_prive, User user, List<User> users) throws SQLException {
        try {
            // CORRIGÉ : INSERT OR ROLLBACK INTO (syntaxe SQLite valide)
            ResultSet rs = excuteInsertSQL(
                "INSERT OR ROLLBACK INTO Discussion(nom_discussion, est_prive) VALUES ('"
                + titre + "', " + est_prive + ")"
            );

            int clé = 0;
            // CORRIGÉ : getInt(1) au lieu de getInt("id_discussion") sur getGeneratedKeys()
            if (rs.next()) {
                clé = rs.getInt(1);
            }
            if (clé == 0) {
                throw new SQLException("[DiscussionDAO] Aucune clé générée pour la nouvelle discussion.");
            }

            // CORRIGÉ : construction propre de la liste de valeurs, sans virgule orpheline
            List<String> valeurs = new ArrayList<>();
            valeurs.add("(" + user.getId_user() + "," + clé + ",1)");

            for (User u : users) {
                if (u.getId_user() != user.getId_user()) {
                    valeurs.add("(" + u.getId_user() + "," + clé + ",0)");
                }
            }

            String user_discussion = "INSERT INTO Roles VALUES " + String.join(",", valeurs);
            excuteInsertSQL(user_discussion);

            return clé;

        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] La discussion n'a pas pu être créée : " + e.getMessage());
            throw e;
        }
    }

    /**
     * @param id_user
     * @param id_discussion
     */
    public static void addUserToDiscussionById(int id_user, int id_discussion) throws SQLException {
        try {
            // CORRIGÉ : table Role -> Roles, virgule manquante, syntaxe INSERT OR ROLLBACK INTO
            excuteInsertSQL(
                "INSERT OR ROLLBACK INTO Roles VALUES (" + id_user + "," + id_discussion + ",0)"
            );
        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] Impossible d'ajouter l'utilisateur à la discussion : " + e.getMessage());
            throw e;
        }
    }

    /**
     * @param id_user
     * @param id_discussion
     */
    public static void RemoveUserFromDiscussionById(int id_user, int id_discussion) throws SQLException {
        try {
            // CORRIGÉ : table Role -> Roles, espace manquant avant AND, excuteInsertSQL pour DELETE
            excuteInsertSQL(
                "DELETE FROM Roles WHERE id_discussion = " + id_discussion + " AND id_user = " + id_user
            );
        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] Impossible de retirer l'utilisateur de la discussion : " + e.getMessage());
            throw e;
        }
    }

    public static void updateDiscussion(String titre, Boolean est_prive, List<User> admin, List<User> users) throws SQLException {
        try {
            // CORRIGÉ : INSERT OR REPLACE INTO + quotes autour de titre
            ResultSet rs = excuteInsertSQL(
                "INSERT OR REPLACE INTO Discussion(nom_discussion, est_prive) VALUES ('"
                + titre + "', " + est_prive + ")"
            );

            int clé = 0;
            if (rs.next()) {
                clé = rs.getInt(1);
            }
            if (clé == 0) {
                throw new SQLException("[DiscussionDAO] Aucune clé générée lors de la mise à jour.");
            }

            // CORRIGÉ : construction propre avec String.join pour éviter les virgules orphelines
            List<String> valeurs = new ArrayList<>();
            for (User u : admin) {
                valeurs.add("(" + u.getId_user() + "," + clé + ",1)");
            }
            for (User u : users) {
                valeurs.add("(" + u.getId_user() + "," + clé + ",0)");
            }

            if (!valeurs.isEmpty()) {
                String user_discussion = "INSERT OR REPLACE INTO Roles VALUES " + String.join(",", valeurs);
                excuteInsertSQL(user_discussion);
            }

        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] La discussion n'a pas pu être mise à jour : " + e.getMessage());
            throw e;
        }
    }
}
