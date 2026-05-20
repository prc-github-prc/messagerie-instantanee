package messagerie_instantane.server.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseLaucher {

    private static boolean databaseExists(){
        try(Connection conn = DatabaseConnection.get_conn()){
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
    public static void initialiser() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!databaseExists()) {
                executerScript(conn, "schema.sql");
                System.out.println("Base prete.");
            } else {
                System.out.println("Base existante detectee, demarrage immediat.");
            }
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
        InputStream is = DatabaseLaucher.class
                .getClassLoader()
                .getResourceAsStream(nomFichier);

        if (is == null) {
            throw new RuntimeException("Fichier introuvable dans le classpath : " + nomFichier);
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
