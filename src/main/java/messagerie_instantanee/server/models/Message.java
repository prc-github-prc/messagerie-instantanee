package messagerie_instantanee.server.models;

public class Message {
    private int id_message;
    private String contenu;
    private int id_author;
    private int id_discussion;
    
    public Message(int id_message, String contenu, 
        int id_author, int id_discussion){

        this.id_message = id_message;
        this.contenu = contenu;
        this.id_author = id_author;
        this.id_discussion = id_discussion;
    }

    public int getId_message() {
        return id_message;
    }

    public String getContenu() {
        return contenu;
    }

    public int getId_author() {
        return id_author;
    }

    public int getId_discussion() {
        return id_discussion;
    }
}
