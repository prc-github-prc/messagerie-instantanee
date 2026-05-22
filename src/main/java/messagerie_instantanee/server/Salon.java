package messagerie_instantanee.server;

import java.util.ArrayList;
import java.util.List;

import messagerie_instantanee.interfaces.*;
import messagerie_instantanee.server.models.*;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import messagerie_instantanee.server.database.DAO.MessageDAO;

import java.rmi.RemoteException;

import messagerie_instantanee.server.database.DAO.UserDAO;

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

     /**
     * @param nom
     * @param user
     * @throws RemoteException
     * 
     * Crée un Salon ainsi qu'une nouvelle discussion.
     */
    public Salon(String nom, User user) throws RemoteException {
        this.nom = nom;
        this.participants = new ArrayList<>();
        this.participants.add(user);
        this.messages = new ArrayList<>();
        this.id = DiscussionDAO.insertDiscussionReturnId(nom, user, participants);
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
        DiscussionDAO.addUserToDiscussionById(user.getId_user(), id);
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
        DiscussionDAO.RemoveUserToDiscussionById(user.getId_user(), id);
        c.affiche("Utilisateur désinscrit(e).");
    }

    /**
     * @param message
     * @return
     * @throws RemoteException
     * 
     * La fonction diffuse distribue le message à tous les clients/utilisateurs membres de la discussion.
     */
    @Override
    public void diffuse(String message, String username) throws RemoteException {
        User user =UserDAO.findUserByUsername(username);
        if(user !=null){
            Message msg = new Message(-1, message, user.getId_user() ,id);
            int id_msg = MessageDAO.addMessageToDiscussion(msg);
            msg.set_ID(id_msg);
            messages.add(msg);
        }else{
            throw new RuntimeException("Impossible de diffuser le message: utilisateur introuvable");
        }
        // Diffuser le message à tous les clients avec affiche pour chaque client.
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
