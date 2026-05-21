package messagerie_instantanee.server.database.DAO;

import static messagerie_instantanee.server.utils.utils.executeSQLQuerry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Message;

public class MessageDAO {
    public static Message findMessageById(int id_message){
        try{
            ResultSet user_data = executeSQLQuerry("SELECT * FROM Messages WHERE id_message = " + id_message);
            return rsToMessage(user_data).getFirst();
        } catch (SQLException e) {
            System.out.println("[MessageDAO] connexion impossible a la base de donnée" + e.getMessage()); 
            return null; 
        } catch (NoSuchElementException e){
            System.out.println("[MessageDAO] ce resultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }
    
    private static List<Message> rsToMessage(ResultSet message_data) throws SQLException{
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
}
