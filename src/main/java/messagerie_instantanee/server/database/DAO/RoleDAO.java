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

public class RoleDAO {

    /**
     * 
     * @param id_discussion
     * @param role
     * @return
     */
    public static List<User> findUserByRole(int id_discussion,Role role){
        int id_role = switch(role){
            case Role.Admin -> 0;
            case Role.User -> 1;
        };
        try{
            ResultSet User_data = executeSQLQuerry("SELECT * FROM USER WHERE id_user = (SELECT id_user FROM Roles WHERE id_discussion = " + id_discussion + "AND role = " + id_role+")");
            return rsToUser(User_data);
        } catch (SQLException e) {
            System.out.println("[RoleDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[RoleDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    /**
     * 
     * @param id_discussion
     * @return
     */
    public static Map<Role,List<User>> findRoleByDiscussion(int id_discussion){
        try{
            HashMap<Role, List<User>> roleByDiScussion = new HashMap<>();
            ResultSet User_data = executeSQLQuerry("SELECT * FROM USER WHERE id_user = (SELECT id_user FROM Roles WHERE id_discussion = " + id_discussion + "AND role = 0)");
            ResultSet Admin_data = executeSQLQuerry("SELECT * FROM USER WHERE id_user = (SELECT id_user FROM Roles WHERE id_discussion = " + id_discussion + "AND role = 1)");
            roleByDiScussion.put(Role.User, rsToUser(User_data));
            roleByDiScussion.put(Role.Admin,rsToUser(Admin_data));
            return roleByDiScussion;
        } catch (SQLException e) {
            System.out.println("[RoleDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[RoleDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

}
