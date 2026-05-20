package messagerie_instantane.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;

import messagerie_instantane.server.models.Message;
import static messagerie_instantane.server.utils.utils.executeSQLQuerry;

public class MessageDAO {
    public static Message findMessageById(int message_id){
        try{
            ResultSet user_data = executeSQLQuerry("SELECT * WHERE message_id = " + message_id);
            return rsToMessage(user_data);
        } catch (SQLException e) {
            System.out.println("[MessageDAO] connexion impossible a la base de donnée" + e); 
            return null; 
        }
    }
    
    private static Message rsToMessage(ResultSet message_data) throws SQLException{
        while (message_data.next()) {
            
        }
        return new Message();
    }
}
