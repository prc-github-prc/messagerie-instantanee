package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.User;
import messagerie_instantanee.server.utils.Enum.Role;

import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToUser;

/**
 * DAO pour les roles.
 */
public class RoleDAO {

    public static List<User> findUserByRole(int id_discussion, Role role) {
        // Admin=1 et User=0 (cohérent avec DiscussionDAO où le créateur=1)
        int id_role = switch (role) {
            case Admin -> 1;
            case User  -> 0;
        };
        try {
            // CORRIGÉ : "role" → "roles" (nom réel de la colonne dans table_init.sql)
            ResultSet user_data = executeSQLQuerry(
                "SELECT * FROM User WHERE id_user IN "
                + "(SELECT id_user FROM Roles WHERE id_discussion = " + id_discussion
                + " AND roles = " + id_role + ")"
            );
            return rsToUser(user_data);
        } catch (SQLException e) {
            System.out.println("[RoleDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[RoleDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    public static Map<Role, List<User>> findRoleByDiscussion(int id_discussion) {
        try {
            HashMap<Role, List<User>> roleByDiscussion = new HashMap<>();

            // CORRIGÉ : "role" → "roles" (nom réel de la colonne dans table_init.sql)
            ResultSet user_data = executeSQLQuerry(
                "SELECT * FROM User WHERE id_user IN "
                + "(SELECT id_user FROM Roles WHERE id_discussion = " + id_discussion
                + " AND roles = 0)"
            );
            ResultSet admin_data = executeSQLQuerry(
                "SELECT * FROM User WHERE id_user IN "
                + "(SELECT id_user FROM Roles WHERE id_discussion = " + id_discussion
                + " AND roles = 1)"
            );

            roleByDiscussion.put(Role.User, rsToUser(user_data));
            roleByDiscussion.put(Role.Admin, rsToUser(admin_data));
            return roleByDiscussion;

        } catch (SQLException e) {
            System.out.println("[RoleDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[RoleDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }
}
