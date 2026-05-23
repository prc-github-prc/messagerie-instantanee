package messagerie_instantanee.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import messagerie_instantanee.server.database.DAO.MessageDAO;
import messagerie_instantanee.server.database.DAO.UserDAO;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Message;
import messagerie_instantanee.server.models.User;
import static messagerie_instantanee.server.services.ServiceSalon.InterfacToUserMinusOwner;

/**
 * Représente un salon, lié à une unique discussion.
 */
public class Salon extends UnicastRemoteObject implements InterfaceSujetDiscussion{
    private int id;
    private String nom;
    private List<InterfaceAffichageClient> lst_participants;
    private List<Message> lst_messages;
    private Boolean estPrivee; // boolean si vrai afficher seulement pour les user dans la table ROLE sinon ne pas afficher 

    /**
     * @param id
     * @throws RemoteException
     * 
     * Crée un Salon à partir d'une discussion déjà existante.
     */
    public Salon(Discussion discussion) throws RemoteException{
        this.id = discussion.getId_discussion();
        this.nom = discussion.getNom_discussion();
        this.lst_participants = discussion.getParticipants();
        this.lst_messages = DiscussionDAO.findMessagesByIdDiscussion(id);
        this.estPrivee = discussion.getPrive();
    }

    /**
     * @param nom
     * @param user
     * @throws RemoteException
     * 
     * Crée un Salon ainsi qu'une nouvelle discussion.
     */
    public Salon(String nom, InterfaceAffichageClient user, Boolean estPrivee) throws RemoteException {
        super();
        this.nom = nom;
        this.lst_participants = new ArrayList<>();
        this.lst_participants.add(user);
        this.id = DiscussionDAO.insertDiscussionReturnId(nom, estPrivee, (User) user, InterfacToUserMinusOwner(lst_participants, user)); 
        this.estPrivee = estPrivee;                                               //cette solution est bricoler de fou je regearderai plus tard
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
    public synchronized void inscription(InterfaceAffichageClient user) throws RemoteException {
        lst_participants.add(user);
        DiscussionDAO.addUserToDiscussionById(user.getId_user(), id);
        // RECHECK c.affiche("Utilisateur ajouté(e).");
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
    public synchronized void desInscription(InterfaceAffichageClient user) throws RemoteException {
        lst_participants.remove(user);
        DiscussionDAO.RemoveUserToDiscussionById(user.getId_user(), id);
        // c.affiche("Utilisateur désinscrit(e)."); RECHECK
    }

    /**
     * @param message
     * @return
     * @throws RemoteException
     * 
     * La fonction diffuse distribue le message à tous les clients/utilisateurs membres de la discussion.
     */
    @Override
    public synchronized void diffuse(String message, String username) throws RemoteException {
        User user = (User) UserDAO.findUserByUsername(username);
        // verifie que l'utilisateur existe
        if(user == null){
            throw new RuntimeException("Impossible de diffuser le message: utilisateur introuvable");
        }
        // ecriture en db
        new Message(-1, message, user.getId_user() ,id);
        
        // duffusuon du message avec desinscription auto quand erreur
        List<InterfaceAffichageClient> aSupprimer = new ArrayList<>();
        for (InterfaceAffichageClient c : lst_participants) {
            try {
                c.affiche(message); 
            } catch (RemoteException e) {
                // client déconnecté, on le retire
                aSupprimer.add(c);
            }
        }
        lst_participants.removeAll(aSupprimer);
    }

    // ============================ getteurs ============================
    public int getSalonId(){return id;}

    public String getSalonNom(){return nom;}

    public List<InterfaceAffichageClient> getParticipants() {return lst_participants;}
    
    public Boolean getEstPrivee() {return estPrivee;}
}
