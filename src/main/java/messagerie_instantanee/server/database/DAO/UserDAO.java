package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;
import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToUser;
import static messagerie_instantanee.server.services.ServiceSalon.InterfacToUserMinusOwner;

/**
 * DAO pour les Users.
 */
public class UserDAO {

    public static User findUserById(int id_user) {
        try {
            ResultSet userData = executeSQLQuerry("SELECT * FROM User WHERE id_User = " + id_user);
            return InterfacToUserMinusOwner(rsToUser(userData), null).getFirst();
        } catch (SQLException e) {
            System.out.println("[UserDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[UserDAO] aucun résultat : " + e.getMessage());
            return null;
        }
    }

    public static InterfaceAffichageClient findUserByUsername(String username) {
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
            // Guillemets autour des valeurs string en SQL
            executeSQLQuerry(
                "INSERT INTO User(username, password_hash) VALUES ('" + username + "', '" + password + "')"
            );
        } catch (SQLException e) {
            System.out.println("[UserDAO] erreur insertion : " + e.getMessage());
            throw new SQLException();
        }
    }
}
