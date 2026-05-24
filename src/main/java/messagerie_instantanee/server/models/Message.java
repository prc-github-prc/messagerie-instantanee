package messagerie_instantanee.server.models;

import static messagerie_instantanee.server.database.DAO.UserDAO.findUserById;

/**
 * Représente un message.
 */
public class Message {
    private int id_message;
    private String contenu;
    private int id_author;
    private int id_discussion;
    
    /**
     * 
     * @param id_message
     * @param contenu
     * @param id_author
     * @param id_discussion
     * 
     * Crée un objet message.
     */
    public Message(int id_message, String contenu, 
        int id_author, int id_discussion){

        this.id_message = id_message;
        this.contenu = contenu;
        this.id_author = id_author;
        this.id_discussion = id_discussion;
    }

    /**
     * 
     * @return id_message
     */
    public int getId_message() {
        return id_message;
    }

    /**
     * 
     * @return contenu
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * 
     * @return id_author
     */
    public int getId_author() {
        return id_author;
    }

    public String getAuthorName(){
        return findUserById(id_author).getUsername();
    }

    /**
     * 
     * @return id_discussion
     */
    public int getId_discussion() {
        return id_discussion;
    }

    /*=============Setter========================================= */
    /**
     * 
     * @param id
     */
    public void set_ID(int id){
        this.id_message=id;
    }
}
