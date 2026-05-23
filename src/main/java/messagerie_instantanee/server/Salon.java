package messagerie_instantanee.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
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


import static messagerie_instantanee.server.database.DAO.MessageDAO.addMessageToDiscussion;

/**
 * Représente un salon, lié à une unique discussion.
 *
 * Séparation claire des types :
 *  - lst_participants : List<InterfaceAffichageClient> → clients RMI actuellement connectés
 *  - User                                             → modèle BDD (id, username, password)
 * Ces deux objets ne sont JAMAIS castés l'un vers l'autre.
 */
public class Salon extends UnicastRemoteObject implements InterfaceSujetDiscussion {
    private int id;
    private String nom;
    private List<InterfaceAffichageClient> lst_participants;
    private List<Message> lst_messages;
    private Boolean estPrivee;

    /**
     * Crée un Salon à partir d'une discussion déjà existante en BDD.
     * lst_participants démarre vide : les clients se réinscriront via inscription()
     * quand ils se reconnecteront.
     */
    public Salon(Discussion discussion) throws RemoteException {
        this.id = discussion.getId_discussion();
        this.nom = discussion.getNom_discussion();
        this.lst_participants = new ArrayList<>();   // clients connectés, pas de données BDD ici
        this.lst_messages = DiscussionDAO.findMessagesByIdDiscussion(id);
        this.estPrivee = discussion.getPrive();
    }

    /**
     * Crée un Salon et insère une nouvelle discussion en BDD.
     *
     * @param nom       nom du salon
     * @param owner     User BDD du créateur (retourné par UserDAO, jamais un stub RMI)
     * @param estPrivee visibilité du salon
     */
    public Salon(String nom, User owner, Boolean estPrivee) throws RemoteException {
        super();
        this.nom = nom;
        this.lst_participants = new ArrayList<>();
        List<User> autresParticipants = new ArrayList<>();  // personne d'autre à la création
        try {
            this.id = DiscussionDAO.insertDiscussionReturnId(nom, estPrivee, owner, autresParticipants);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.estPrivee = estPrivee;
    }

    /**
     * Inscrit un client RMI connecté au salon.
     */
    @Override
    public synchronized void inscription(InterfaceAffichageClient user) throws RemoteException {
        lst_participants.add(user);
        try {
            DiscussionDAO.addUserToDiscussionById(user.getId_user(), id);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Désinscrit un client RMI connecté du salon.
     */
    @Override
    public synchronized void desinscription(InterfaceAffichageClient user) throws RemoteException {
        lst_participants.remove(user);
        try {
            DiscussionDAO.RemoveUserFromDiscussionById(user.getId_user(), id);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Diffuse un message à tous les participants connectés.
     * La déconnexion silencieuse d'un client est gérée automatiquement.
     */
    @Override
    public synchronized void diffuse(String message, String username) throws RemoteException {
        User user = UserDAO.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Impossible de diffuser le message: utilisateur introuvable");
        }
        Message msg = new Message(-1, message, user.getId_user(), id);

        List<InterfaceAffichageClient> aSupprimer = new ArrayList<>();
        for (InterfaceAffichageClient client : lst_participants) {
            try {
                addMessageToDiscussion(msg);
                client.affiche(message);
                lst_messages.add(msg);
            } catch (RemoteException e) {
                aSupprimer.add(client);  // client déconnecté → on le retire
            }
            catch (SQLException e) {
                System.out.println("[Salon] Impossible d'ajouter le message à la discussion : " + e.getMessage());
            }
        }
        lst_participants.removeAll(aSupprimer);
    }

    public Message[] getArchive() {
        Message[] archive = new Message[20];
        for(int i = lst_messages.size() - 1; i >= lst_messages.size() - 20 && i >= 0; i--){
            if(lst_messages.get(i) != null){
                archive[lst_messages.size() - 1 - i] = lst_messages.get(i);
            }
        }
        return archive;
    }

    // ============================ getters ============================
    public int getSalonId() { return id; }
    public String getSalonNom() { return nom; }
    public List<InterfaceAffichageClient> getParticipants() { return lst_participants; }
    public Boolean getEstPrivee() { return estPrivee; }
}
