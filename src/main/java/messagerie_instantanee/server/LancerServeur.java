package messagerie_instantanee.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

/**
 * Permet de lancer le serveur de messagerie.
 */
public class LancerServeur {
    /**
     * port.
     */
    public static final int PORT = 8090;
    /**
     * nom du serveur.
     */
    public static final String NOM = "messagerie";

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(PORT);
            Server serveur = new Server();
            Naming.bind("//localhost:" + PORT + "/" + NOM, serveur);

            System.out.println("✅ Serveur démarré sur le port " + PORT);
            System.out.println("   Adresse : //localhost:" + PORT + "/" + NOM);
            System.out.println("──────────────────────────────────────");
            System.out.println("   Appuyez sur ENTRÉE pour arrêter.");
            System.out.println("──────────────────────────────────────");

            // ← Bloque le thread principal : le serveur reste vivant
            Scanner scan = new Scanner(System.in);
            scan.nextLine();

            System.out.println("Arrêt du serveur...");
            
            Naming.unbind("//localhost:" + PORT + "/" + NOM);

            scan.close();

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}