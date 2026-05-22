package messagerie_instantanee.server.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Message;
import messagerie_instantanee.server.models.User;
import messagerie_instantanee.server.models.Tag;
import messagerie_instantanee.server.database.DAO.*;

public class ResultSetConverter {
    public static List<User> rsToUser(ResultSet User_data) throws SQLException{
        List<User> lst_User = new ArrayList<>();
        while (User_data.next()) {
            int id_user = User_data.getInt("id_user");
            String username = User_data.getString("username");
            String password_hash = User_data.getString("password_hash");
            lst_User.add(new User(id_user, username, password_hash));
        }
        return lst_User;
    }

    public static List<Discussion> rsToDiscussion(ResultSet Discussion_data) throws SQLException{
        List<Discussion> lst_Discussion = new ArrayList<>();
        while (Discussion_data.next()) {
            int id_Discussion = Discussion_data.getInt("id_Discussion");
            String nom_discussion = Discussion_data.getString("nom_discussion");
            Boolean est_priver = Discussion_data.getBoolean("est_prive");
            lst_Discussion.add(new Discussion(id_Discussion, nom_discussion, DiscussionDAO.findUserByIDDiscussion(id_Discussion), est_priver));
        }
        return lst_Discussion;
    }
    
    public static List<Message> rsToMessage(ResultSet message_data) throws SQLException{
        List<Message> lst_message = new ArrayList<>();
        while (message_data.next()) {
            int id_message = message_data.getInt("id_message");
            String contenu = message_data.getString("contenu");
            int id_author = message_data.getInt("id_author");
            int id_discussion = message_data.getInt("id_discussion");
            lst_message.add(new Message(id_message, contenu, id_author, id_discussion));
        }
        return lst_message;
    }

    public static List<Tag> rsToTag(ResultSet tag_data) throws SQLException{
        List<Tag> lst_tag = new ArrayList<>();
        while (tag_data.next()) {
            String nom_tag = tag_data.getString("nom_tag");
            lst_tag.add(new Tag(nom_tag));
        }
        return lst_tag;
    }
}
