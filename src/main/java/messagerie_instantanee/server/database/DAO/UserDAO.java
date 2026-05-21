package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToUser;

public class UserDAO {
    public static User findUserById(int id_user){
        try{
            ResultSet User_data = executeSQLQuerry("SELECT * FROM User WHERE id_User = " + id_user);
            return rsToUser(User_data).getFirst();
        } catch (SQLException e) {
            System.out.println("[UserDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[UserDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }
}
