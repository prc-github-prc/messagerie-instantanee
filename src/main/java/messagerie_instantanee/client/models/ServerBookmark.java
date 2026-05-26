package messagerie_instantanee.client.models;

/**
 * ServerBookmark.
 */
public class ServerBookmark {
    public String nom;
    public String ip;
    public int    port;

    /**
     * 
     * @param nom
     * @param ip
     * @param port
     * 
     * Crée un serverbookmark.
     */
    public ServerBookmark(String nom, String ip, int port) {
        this.nom     = nom;
        this.ip = ip;
        this.port    = port;
    }

    public String getName() { return nom; }
    public String getIp()   { return ip;   }
    public int getPort()   { return port;}
    public void setName(String name) { this.nom = name;}
    public void setIp(String ip)     { this.ip  = ip;}
    public void setPort(int port) { this.port= port;}
}