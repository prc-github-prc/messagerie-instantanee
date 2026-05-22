package messagerie_instantanee.server.models;

public class User {
    private int id_user;
    private String username;
    private String password_hash;

    /**
     * 
     * @param id_user
     * @param username
     * @param password_hash
     * 
     * Crée un objet user
     */
    public User(int id_user, String username, String password_hash){
        this.id_user = id_user;
        this.username = username;
        this.password_hash = password_hash;
    }

    /**
     * 
     * @return id_user.
     */
    public int getId_user() {
        return id_user;
    }

    /**
     * 
     * @return username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @return password_hash.
     */
    public String getPassword_hash() {
        return password_hash;
    }
}
