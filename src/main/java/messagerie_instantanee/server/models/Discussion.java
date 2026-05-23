package messagerie_instantanee.server.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;

public class Discussion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_discussion;
    /**
     * nom de la discussion.
     */
    private String nom_discussion;
    /**
     * liste des participants à la discussion.
     */
    private List<InterfaceAffichageClient> participants;
    /**
     * indique si la discussion est privée ou publique.
     */
    private Boolean est_prive;

    public Discussion(int id_discussion, String nom_discussion, List<InterfaceAffichageClient> participants, Boolean est_prive) {
        this.id_discussion = id_discussion;
        this.nom_discussion = nom_discussion;
        this.participants = participants;
        this.est_prive = est_prive;
        
    }

    public Discussion(int id_discussion, String nom_discussion, Boolean est_prive) {
        this.id_discussion = id_discussion;
        this.nom_discussion = nom_discussion;
        this.est_prive = est_prive;
        this.participants = new ArrayList<>(); 
    }

    public int getId_discussion() { return id_discussion; }
    public String getNom_discussion() { return nom_discussion; }
    public List<InterfaceAffichageClient> getParticipants() { return participants; }
    public Boolean getPrive() { return est_prive; }

    @Override
    public String toString(){
        return nom_discussion;
    }
}
