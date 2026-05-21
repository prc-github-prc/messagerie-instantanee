package messagerie_instantanee.client.models;

public class ServerBookmark {
    public String nom;
    public String adresse;
    public int    port;

    public ServerBookmark(String nom, String adresse, int port) {
        this.nom     = nom;
        this.adresse = adresse;
        this.port    = port;
    }
}