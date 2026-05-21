package messagerie_instantanee.server;

import java.util.List;

import messagerie_instantanee.client.*;
import messagerie_instantanee.interfaces.*;
import messagerie_instantanee.server.models.*;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import messagerie_instantanee.server.database.DAO.MessageDAO;

import static messagerie_instantanee.server.utils.ResultSetConverter.*;

import java.rmi.RemoteException;

public class Salon implements InterfaceSujetDiscussion{
    private int id;
    private String nom;
    List<User> participants;
    List<Message> messages;

    /**
     * @param id
     * @throws RemoteException
     * 
     * Crée un Salon à partir d'une discussion déjà existante.
     */
    public Salon(Discussion discussion) throws RemoteException{
        this.id = discussion.getId_discussion();
        this.nom = discussion.getNom_discussion();
        this.participants = discussion.getParticipants();
        this.messages = DiscussionDAO.findMessagesByIdDiscussion(id);
    }

    public Salon(String nom) throws RemoteException {
        throw new UnsupportedOperationException("Unimplemented method 'nouveau Salon'");
    }

    /**
     * 
     * @param c
     * @param user
     * @return
     * @throws RemoteException
     * 
     * La fonction inscription récupère des données fournies par l'utilisateur et effectue une requête auprès du DAO.
     */
    @Override
    public void inscription(InterfaceAffichageClient c, User user) throws RemoteException {
        participants.add(user);
        Discussion.addUserToDiscussionByIDs(user.getId_user(), id);
        c.affiche("Utilisateur ajouté(e).");
    }

    /**
     * @param c
     * @param user
     * @return
     * @throws RemoteException
     * 
     * La fonction desInscription supprime le compte de l'utilisateur effectuant la requête.
     */
    @Override
    public void desInscription(InterfaceAffichageClient c, User user) throws RemoteException {
        for (User u : participants) {
            if (user.getId_user() == u.getId_user()) {
                participants.remove(u);
                break;
            }
        }
        Discussion.removeUserToDiscussionByIDs(user.getId_user(), id);
        c.affiche("Utilisateur désinscrit(e).");
    }

    /**
     * @param Message
     * @return
     * @throws RemoteException
     * 
     * La fonction diffuse distribue le message à tous les clients/utilisateurs membres de la discussion.
     */
    @Override
    public void diffuse(String Message) throws RemoteException {
        // I. Stocker le message en BDD en envoyant un message DAO.
        // II. Diffuser le message à tous les clients avec affiche pour chaque client.
    }

    // ============================ getteurs ============================
    public List<Message> RecupereArchive() {
        return messages;
    }

    public int getSalonId(){
        return id;
    }

    public String getSalonNom(){
        return nom;
    }

    public List<User> getLstInscrit(){
        return participants;
    }
}
