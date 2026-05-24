package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.excuteInsertSQL;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToUser;

/**
 * DAO pour les Users.
 */
public class UserDAO {

    public static User findUserById(int id_user) {
        try {
            ResultSet userData = executeSQLQuerry(
                "SELECT * FROM User WHERE id_user = " + id_user
            );
            return rsToUser(userData).getFirst();
        } catch (SQLException e) {
            System.out.println("[UserDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[UserDAO] aucun résultat : " + e.getMessage());
            return null;
        }
    }

    /**
     * Retourne un User (modèle BDD) à partir de son username.
     * Ne retourne PAS un stub RMI : utiliser le registre des clients connectés pour ça.
     */
    public static User findUserByUsername(String username) {
        try {
            ResultSet userData = executeSQLQuerry(
                "SELECT * FROM User WHERE username = '" + username + "'"
            );
            return rsToUser(userData).getFirst();
        } catch (SQLException e) {
            System.out.println("[UserDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[UserDAO] utilisateur introuvable : " + username);
            return null;
        }
    }

    public static void insertUser(String username, String password) throws SQLException {
        try {
            excuteInsertSQL(
                "INSERT OR ROLLBACK INTO User(username, password_hash) VALUES ('"
                + username + "', '" + password + "')"
            );
        } catch (SQLException e) {
            System.out.println("[UserDAO] erreur insertion : " + e.getMessage());
            // CORRIGÉ : throw new SQLException() -> throw e (pour conserver le message d'origine)
            throw e;
        }
    }
}
