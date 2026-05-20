package messagerie_instantane.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import messagerie_instantane.server.models.Message;
import static messagerie_instantane.server.utils.utils.executeSQLQuerry;

public class MessageDAO {
    public static Message findMessageById(int message_id){
        try{
            ResultSet user_data = executeSQLQuerry("SELECT * WHERE message_id = " + message_id);
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
