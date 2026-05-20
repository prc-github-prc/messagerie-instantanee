package messagerie_instantane.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection con;
    private static String URL = "jdbc:sqlite:/data/messagerie.db";
    
    /**
     * Méthode statique pour obtenir une connexion à la base de données SQLite.
     * 
     * @throws SQLException Si une erreur survient lors de l'établissement de la connexion à la base de données.
     * @return Une connexion à la base de données SQLite, ou une exception SQLException en cas d'échec de la connexion.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Constructeur de la classe DatabaseConnection.
     * Ce constructeur tente d'établir une connexion à la base de données en appelant la méthode getConnection() de la classe DatabaseManager. 
     * Si la connexion échoue, un message d'erreur est affiché dans la console.
     */
    public DatabaseConnection(){ 
        try{
            con = DatabaseConnection.getConnection();
        } catch (Exception e){
            System.out.println("la connection a échouer a la base de donnée");
        }
    }

    /**
     * Méthode statique pour obtenir la connexion à la base de données.
     * Cette méthode vérifie si la connexion (conn) est déjà établie. Si ce n'est pas le cas, elle tente d'établir la connexion en appelant la méthode getConnection() de la classe DatabaseManager.
     * Si la connexion échoue, un message d'erreur est affiché dans la console. 
     * 
     * @return La connexion à la base de données (conn), ou null en cas d'échec de la connexion.
     */
    public static Connection get_conn(){ //TODO recheck pk y'a deux getConn
        if (con == null) {
            try {
                con = DatabaseConnection.getConnection();
            } catch (Exception e) {
                System.out.println("La connexion à la base de données a échoué");
            }
        }
        return con;
    }

}
