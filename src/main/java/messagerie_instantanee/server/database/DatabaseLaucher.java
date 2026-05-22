package messagerie_instantanee.server.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseLaucher {

    /**
     * Méthode statique pour vérifier si la BDD existe.
     * @return
     */
    private static boolean databaseExists(){
        Connection conn = DatabaseConnection.get_conn();
        try {
            DatabaseMetaData dMetaData = conn.getMetaData();
            try (ResultSet rs = dMetaData.getCatalogs()){
                while(rs.next()){
                    String currentDbName = rs.getString(1);
                    if("messagerie".equalsIgnoreCase(currentDbName)){
                        return true;
                    }
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Erreur lors de la vérification : " + e.getMessage());
        }
        return false;
    }

    /**
     * Méthode statique pour initialiser la base de données.
     * 
     * @throws Exception Si une erreur survient lors de l'initialisation de la base de données
     */
    public static void initialiser() {
        Connection conn = DatabaseConnection.get_conn();
        try {
            if (!databaseExists()) {
                executerScript(conn, "sql/table_init.sql");
                System.out.println("Base prete.");
            } else {
                System.out.println("Base existante detectee, demarrage immediat.");
            }
        }catch( SQLException s){
            throw  new RuntimeException("Un problème est arrivé lors de la connection à la base de donnée" + s.getMessage());
        }catch(Exception e){
            throw  new RuntimeException("Un problème est arrivé lors de l'execution du script" + e.getMessage());
        }
    }

    /**
     * Méthode privée statique pour exécuter un script SQL à partir d'un fichier.
     * Cette méthode lit le contenu du fichier SQL spécifié, supprime les commentaires et les lignes vides, puis exécute chaque instruction SQL séparément sur la connexion fournie.
     * 
     * @param conn La connexion à la base de données sur laquelle les instructions SQL seront exécutées.
     * @param nomFichier Le nom du fichier SQL à exécuter, qui doit être situé dans le classpath de l'application.
     * @throws Exception Si une erreur survient lors de la lecture du fichier ou de l'exécution des instructions SQL, une exception est levée avec un message d'erreur approprié.
     */
    private static void executerScript(Connection conn, String nomFichier) throws Exception {
        String resourcePath = nomFichier.startsWith("/") ? nomFichier : "/" + nomFichier;
        InputStream is = DatabaseLaucher.class
                .getResourceAsStream(resourcePath);

        if (is == null) {
            throw new RuntimeException("Fichier introuvable dans le classpath : " + resourcePath);
        }

        String contenu = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        contenu = contenu.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        contenu = contenu.replaceAll("--[^\\n]*", "");

        String[] instructions = contenu.split(";");

        try (Statement st = conn.createStatement()) {
            for (String instruction : instructions) {
                String ligne = instruction.strip();
                if (!ligne.isEmpty()) {
                    st.execute(ligne);
                }
            }
        }
        System.out.println("Script execute : " + nomFichier);
    }




}
