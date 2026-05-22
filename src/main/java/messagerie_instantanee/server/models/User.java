package messagerie_instantanee.server.models;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_user;
    private String username;
    private String password_hash;

    public User(int id_user, String username, String password_hash) {
        this.id_user = id_user;
        this.username = username;
        this.password_hash = password_hash;
    }

    public int getId_user() { return id_user; }
    public String getUsername() { return username; }
    public String getPassword_hash() { return password_hash; }
}
