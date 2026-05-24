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

/**
 * DAO pour les messages.
 */
public class MessageDAO {

    /*===================================Méthodes de recherche=======================================*/

    public static Message findMessageById(int id_message) {
        try {
            // CORRIGÉ : table "Messages" -> "Message"
            ResultSet user_data = executeSQLQuerry(
                "SELECT * FROM Message WHERE id_message = " + id_message
            );
            return rsToMessage(user_data).getFirst();
        } catch (SQLException e) {
            System.out.println("[MessageDAO] connexion impossible a la base de donnée : " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("[MessageDAO] ce résultats ne contient aucune valeur : " + e.getMessage());
            return null;
        }
    }

    /*===================================Méthodes d'insertion=======================================*/

    /**
     * @param message
     * @return id du message inséré
     */
    public static int addMessageToDiscussion(Message message) throws SQLException {
        try {
            // CORRIGÉ : quotes autour des valeurs texte/date/heure
            // CORRIGÉ : virgules manquantes entre les valeurs
            // CORRIGÉ : ON CONFLICT ROLLBACK déplacé hors des VALUES -> INSERT OR ROLLBACK INTO
            ResultSet rs = excuteInsertSQL(
                "INSERT OR ROLLBACK INTO Message(contenu, datage, horo, id_user, id_discussion) VALUES ('"
                + message.getContenu() + "', '"
                + Date.valueOf(LocalDate.now()) + "', '"
                + Time.valueOf(LocalTime.now()) + "', "
                + message.getId_author() + ", "
                + message.getId_discussion() + ")"
            );

            int clé = 0;
            // CORRIGÉ : getInt(1) au lieu de getInt("id_discussion") sur getGeneratedKeys()
            if (rs.next()) {
                clé = rs.getInt(1);
            }
            return clé;

        } catch (SQLException e) {
            System.out.println("[MessageDAO] Impossible d'insérer le message : " + e.getMessage());
            throw e;
        }
    }

    public static void deleteMessageFromDiscussion(Message message) throws SQLException {
        try {
            // CORRIGÉ : executeSQLQuerry -> excuteInsertSQL (executeQuery ne fonctionne pas pour DELETE)
            excuteInsertSQL(
                "DELETE FROM Message WHERE id_message = " + message.getId_message()
            );
        } catch (SQLException e) {
            System.out.println("[MessageDAO] Impossible de supprimer le message : " + e.getMessage());
            throw e;
        }
    }
}
