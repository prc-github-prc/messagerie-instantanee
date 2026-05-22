package messagerie_instantanee.server.models;

import java.util.List;

public class Discussion {
    private int id_discussion;
    private String nom_discussion;
    private List<User> participants;
    private Boolean est_prive;

    /**
     * 
     * @param id_discussion
     * @param nom_discussion
     * @param participants
     * @param est_prive
     * 
     * Crée un objet discussion.
     */
    public Discussion(int id_discussion, String nom_discussion, List<User> participants,Boolean est_prive){
        this.id_discussion = id_discussion;
        this.nom_discussion = nom_discussion;
        this.participants = participants;
        this.est_prive = est_prive;
    }

    /**
     * 
     * @return id_discussion.
     */
    public int getId_discussion() {
        return id_discussion;
    }

    /**
     * 
     * @return nom_discussion.
     */
    public String getNom_discussion() {
        return nom_discussion;
    }

    /**
     * 
     * @return participants.
     */
    public List<User> getParticipants() {
        return participants;
    }

    /**
     * 
     * @return est_prive.
     */
    public Boolean getPrive(){
        return est_prive;
    }
}
