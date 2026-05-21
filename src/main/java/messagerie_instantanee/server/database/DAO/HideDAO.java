package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Discussion;
import static messagerie_instantanee.server.utils.utils.executeSQLQuerry;


public class HideDAO {
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

    private static List<Discussion> rsToDiscussion(ResultSet Discussion_data) throws SQLException{
        List<Discussion> lst_Discussion = new ArrayList<>();
        while (Discussion_data.next()) {
            int id_Discussion = Discussion_data.getInt("id_Discussion");
            String nom_discussion = Discussion_data.getString("nom_discussion");
            lst_Discussion.add(new Discussion(id_Discussion, nom_discussion));
        }
        return lst_Discussion;
    }
}
