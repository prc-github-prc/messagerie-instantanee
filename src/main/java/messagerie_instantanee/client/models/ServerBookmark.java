package messagerie_instantanee.client.models;

public class ServerBookmark {
    public String nom;
    public String adresse;   // ← correspond au champ JSON "adresse"
    public int    port;

    public ServerBookmark(String nom, String adresse, int port) {
        this.nom     = nom;
        this.adresse = adresse;
        this.port    = port;
    }

    public String getName() { return nom;     }
    public String getIp()   { return adresse; }  // getIp() conservé pour compatibilité
    public int    getPort() { return port;    }

    public void setName(String nom)      { this.nom     = nom;     }
    public void setIp(String adresse)    { this.adresse = adresse; }
    public void setPort(int port)        { this.port    = port;    }
}
