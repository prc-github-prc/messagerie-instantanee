package messagerie_instantanee.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Permet de lancer le serveur de messagerie.
 */
public class LancerServeur {
    
    public static final int PORT = 8090;
    public static final String NOM = "messagerie";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            Server serveur = new Server();
            Naming.bind("//localhost:" + PORT + "/" + NOM, serveur);

            System.out.println("✅ Serveur démarré sur le port " + PORT);
            System.out.println("   Adresse : //localhost:" + PORT + "/" + NOM);
            System.out.println("──────────────────────────────────────");
            System.out.println("   Appuyez sur ENTRÉE pour arrêter.");
            System.out.println("──────────────────────────────────────");

            // ShutdownHook : s'exécute même si on ferme le terminal ou qu'on
            // fait Ctrl+C, sans passer par le Scanner ci-dessous.
            // Sans ça, LocateRegistry et le Server gardent des threads non-daemon
            // ouverts → le processus ne se ferme jamais proprement.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("[Shutdown] Fermeture du serveur...");
                    Naming.unbind("//localhost:" + PORT + "/" + NOM);
                    serveur.close();
                    UnicastRemoteObject.unexportObject(serveur, true);   // libère le port du Server
                    UnicastRemoteObject.unexportObject(registry, true);  // libère le port 8090 du registry
                    System.out.println("[Shutdown] Ports RMI libérés. Bye.");
                } catch (Exception e) {
                    System.err.println("[Shutdown] Erreur : " + e.getMessage());
                }
            }, "shutdown-hook"));

            // ← Bloque le thread principal : le serveur reste vivant
            Scanner scan = new Scanner(System.in);
            scan.nextLine();
            scan.close();

            // Appui sur ENTRÉE → déclenche System.exit() qui active le ShutdownHook ci-dessus
            System.exit(0);

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
