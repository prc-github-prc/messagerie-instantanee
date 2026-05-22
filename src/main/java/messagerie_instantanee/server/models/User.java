package messagerie_instantanee.server.models;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_user;
    /**
     * nom d'utilisateur.
     */
    private String username;
    /**
     * hash du user.
     */
    private String password;

    public User(int id_user, String username, String password) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
    }

    public int getId_user() { return id_user; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
