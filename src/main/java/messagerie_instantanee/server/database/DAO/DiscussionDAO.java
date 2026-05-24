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
            ResultSet rs = excuteInsertSQL(
                "INSERT OR ROLLBACK INTO Discussion(nom_discussion, est_prive) VALUES ('"
                + titre + "', " + est_prive + ")"
            );

            int clé = 0;
            if (rs.next()) {
                clé = rs.getInt(1);
            }
            if (clé == 0) {
                throw new SQLException("[DiscussionDAO] Aucune clé générée pour la nouvelle discussion.");
            }

            List<String> valeurs = new ArrayList<>();
            valeurs.add("(" + user.getId_user() + "," + clé + ",1)");

            for (User u : users) {
                if (u.getId_user() != user.getId_user()) {
                    valeurs.add("(" + u.getId_user() + "," + clé + ",0)");
                }
            }

            //INSERT INTO Roles(id_user, id_discussion, roles) pour être explicite sur les colonnes
            String user_discussion = "INSERT INTO Roles(id_user, id_discussion, roles) VALUES " + String.join(",", valeurs);
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
            ResultSet rs = excuteInsertSQL(
                "INSERT OR ROLLBACK INTO Discussion(nom_discussion, est_prive) VALUES ('"
                + titre + "', " + est_prive + ")"
            );

            int clé = 0;
            if (rs.next()) {
                clé = rs.getInt(1);
            }
            if (clé == 0) {
                throw new SQLException("[DiscussionDAO] Aucune clé générée pour la nouvelle discussion.");
            }

            List<String> valeurs = new ArrayList<>();
            valeurs.add("(" + user.getId_user() + "," + clé + ",1)");

            for (User u : users) {
                if (u.getId_user() != user.getId_user()) {
                    valeurs.add("(" + u.getId_user() + "," + clé + ",0)");
                }
            }

            //INSERT INTO Roles(id_user, id_discussion, roles) pour être explicite sur les colonnes
            String user_discussion = "INSERT INTO Roles(id_user, id_discussion, roles) VALUES " + String.join(",", valeurs);
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
            // INSERT INTO Roles(id_user, id_discussion, roles) pour être explicite sur les colonnes
            excuteInsertSQL(
                "INSERT OR ROLLBACK INTO Roles(id_user, id_discussion, roles) VALUES (" + id_user + "," + id_discussion + ",0)"
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
            //  INSERT OR REPLACE insérait une NOUVELLE ligne à chaque appel car id_discussion
            // (PK auto-increment) n'était pas fourni → pas de conflit détecté par SQLite → duplication.
            // On utilise UPDATE sur la ligne existante à la place.

            // 1. Met à jour est_prive sur la ligne existante
            excuteInsertSQL(
                "UPDATE Discussion SET est_prive = " + est_prive
                + " WHERE nom_discussion = '" + titre + "'"
            );

            // 2. Récupère l'id_discussion existant (nécessaire pour mettre à jour Roles)
            ResultSet rs = executeSQLQuerry(
                "SELECT id_discussion FROM Discussion WHERE nom_discussion = '" + titre + "'"
            );
            int clé = 0;
            if (rs.next()) {
                clé = rs.getInt("id_discussion");
            }
            if (clé == 0) {
                throw new SQLException("[DiscussionDAO] Discussion introuvable pour la mise à jour : " + titre);
            }

            // 3. Resynchronise les Roles : supprime les anciens, réinsère les actuels
            excuteInsertSQL("DELETE FROM Roles WHERE id_discussion = " + clé);

            List<String> valeurs = new ArrayList<>();

            // CORRIGÉ : défense contre null — getAdmin()/getUser() retournent null si RoleDAO échoue
            if (admin != null) {
                for (User u : admin) {
                    valeurs.add("(" + u.getId_user() + "," + clé + ",1)");
                }
            }
            if (users != null) {
                for (User u : users) {
                    valeurs.add("(" + u.getId_user() + "," + clé + ",0)");
                }
            }

            if (!valeurs.isEmpty()) {
                // CORRIGÉ : INSERT INTO Roles(id_user, id_discussion, roles) pour être explicite sur les colonnes
                excuteInsertSQL("INSERT INTO Roles(id_user, id_discussion, roles) VALUES " + String.join(",", valeurs));
            }

        } catch (SQLException e) {
            System.out.println("[DiscussionDAO] La discussion n'a pas pu être mise à jour : " + e.getMessage());
            throw e;
        }
    }
}
