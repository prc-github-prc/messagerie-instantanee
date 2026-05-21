package messagerie_instantanee.server.models;

public class Discussion {
    private int id_discussion;
    private String nom_discussion;

    public Discussion(int id_discussion, String nom_discussion){
        this.id_discussion = id_discussion;
        this.nom_discussion = nom_discussion;
    }

    public int getId_discussion() {
        return id_discussion;
    }

    public String getNom_discussion() {
        return nom_discussion;
    }
}
