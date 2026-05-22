package messagerie_instantanee.client.models;

/**
 * ServerBookmark.
 */
public class ServerBookmark {
    /**
     * nom.
     */
    public String nom;
    /**
     * adresse.
     */
    public String adresse;
    /**
     * port.
     */
    public int    port;

    /**
     * 
     * @param nom
     * @param adresse
     * @param port
     * 
     * Crée un serverbookmark.
     */
    public ServerBookmark(String nom, String adresse, int port) {
        this.nom     = nom;
        this.adresse = adresse;
        this.port    = port;
    }
}