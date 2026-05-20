package messagerie_instantane.server.database;

import java.sql.Connection;

public class DatabaseConnection {
   public Connection con;
   
   /**
     * Constructeur de la classe DatabaseConnection.
     * Ce constructeur tente d'établir une connexion à la base de données en appelant la méthode getConnection() de la classe DatabaseManager. 
     * Si la connexion échoue, un message d'erreur est affiché dans la console.
     */
    public DatabaseConnection(){ 
        try{
            conn = DatabaseManager.getConnection();
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
    public static Connection get_conn(){
         if (conn == null) {
            try {
                conn = DatabaseManager.getConnection();
            } catch (Exception e) {
                System.out.println("La connexion à la base de données a échoué");
            }
        }
        return conn;
    }

   
}
