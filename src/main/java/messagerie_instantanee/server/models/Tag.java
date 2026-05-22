package messagerie_instantanee.server.models;

public class Tag {
    private String nom_tag;

    /**
     * 
     * @param tag_name
     * 
     * Crée un objet tag.
     */
    public Tag(String tag_name){
        this.nom_tag = tag_name;
    }

    /**
     * 
     * @return nom_tag.
     */
    public String getNom_tag(){
        return nom_tag;
    }
}
