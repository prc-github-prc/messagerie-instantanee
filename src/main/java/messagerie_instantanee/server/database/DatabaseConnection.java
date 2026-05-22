package messagerie_instantanee.server.database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Connexion à la BDD.
 */
public class DatabaseConnection {
    /**
     * connexion.
     */
    public static Connection con;
    /**
     * chemin de fichier vers la BDD.
     */
    private static final Path DB_PATH = Paths.get("data", "messagerie.db").toAbsolutePath();
    /**
     * url de la BDD.
     */
    private static final String URL = "jdbc:sqlite:" + DB_PATH;
    

    /**
     * Constructeur de la classe DatabaseConnection.
     * Ce constructeur tente d'établir une connexion à la base de données en appelant la méthode getConnection() de la classe DatabaseManager. 
     * Si la connexion échoue, un message d'erreur est affiché dans la console.
     */
    public DatabaseConnection(){ 
        try{
            con = DatabaseConnection.get_conn();
        } catch (RuntimeException e){
            System.err.println("La connexion à la base de données a échoué dans le constructeur : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Méthode statique pour obtenir la connexion à la base de données.
     * Cette méthode vérifie si la connexion (conn) est déjà établie. Si ce n'est pas le cas, elle tente d'établir la connexion en appelant la méthode getConnection() de la classe DatabaseManager.
     * Si la connexion échoue, un message d'erreur est affiché dans la console. 
     * 
     * @return La connexion à la base de données (conn)
     */
    public static Connection get_conn(){
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            System.err.println("La connexion à la base de données a échoué sur URL='" + URL + "'");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return con;
    }

}
