package messagerie_instantanee.server.models;

public class Tag {
    private String nom_tag;

    public Tag(String tag_name){
        this.nom_tag = tag_name;
    }

    public String getNom_tag(){
        return nom_tag;
    }
}
