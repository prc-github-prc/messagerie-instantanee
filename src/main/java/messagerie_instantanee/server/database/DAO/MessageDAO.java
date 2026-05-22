package messagerie_instantanee.server.database.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import messagerie_instantanee.server.models.Message;

import static messagerie_instantanee.server.utils.ExecSqlQuerry.excuteInsertSQL;
import static messagerie_instantanee.server.utils.ExecSqlQuerry.executeSQLQuerry;
import static messagerie_instantanee.server.utils.ResultSetConverter.rsToMessage;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageDAO {
    /*===================================Méthodes de recherche=======================================*/
    /**
     * 
     * @param id_message
     * @return
     */
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
    /*===================================Méthodes d'insertion=======================================*/
    /**
     * 
     * @param message
     * @return cle du message
     */
    public static int addMessageToDiscussion(Message message){
        try{
            ResultSet rs = excuteInsertSQL("INSERT INTO Message(contenu, datage, horo, id_user, id_discussion ) VALUES ("+ message.getContenu() + Date.valueOf(LocalDate.now()) + Time.valueOf(LocalTime.now()) + ","+ message.getId_author()+","+ message.getId_discussion() +")");
            int clé=0;
            while(rs.next()){
                clé=rs.getInt("id_discussion");
            }
            return clé;
        } catch (SQLException e) {
            System.out.println("[MessageDAO] connexion impossible a la base de donnée" + e.getMessage());
            return -1;
        } 
    }

    /**
     * 
     * @param message
     */
    public static void deleteMessageFromDiscussion(Message message){
        try {
            executeSQLQuerry("DELETE FROM Message WHERE id_message = "+ message.getId_message());
        } catch (SQLException e) {
            System.out.println("[MessageDAO] Impossible de supprimer le lien dans message : " + e.getMessage());
        }
    }
}
