package messagerie_instantanee.client;

import messagerie_instantanee.UI.App;

/**
 * Point d'entrée alternatif pour lancer uniquement le client.
 * Délègue au main JavaFX de App.
 */
public class LancerClient {
    /**
     * 
     * @param args
     * 
     * Lance le client.
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
