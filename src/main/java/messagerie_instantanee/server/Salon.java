package messagerie_instantanee.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import messagerie_instantanee.interfaces.InterfaceAffichageClient;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import static messagerie_instantanee.server.database.DAO.MessageDAO.addMessageToDiscussion;
import messagerie_instantanee.server.database.DAO.RoleDAO;
import messagerie_instantanee.server.database.DAO.UserDAO;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.Message;
import messagerie_instantanee.server.models.User;
import messagerie_instantanee.server.utils.Enum.Role;

public class Salon extends UnicastRemoteObject implements InterfaceSujetDiscussion {
    private int id_discussion;
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
        this.id_discussion = discussion.getId_discussion();
        this.nom = discussion.getNom_discussion();
        this.lst_participants = new ArrayList<>();   // clients connectés, pas de données BDD ici
        this.lst_messages = DiscussionDAO.findMessagesByIdDiscussion(id_discussion);
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
        this.nom = nom;
        this.lst_participants = new ArrayList<>();
        List<User> autresParticipants = new ArrayList<>();  // pad d'inscrit a la creation
        this.lst_messages = new ArrayList<>();
        try {
            this.id_discussion = DiscussionDAO.insertDiscussionReturnId(nom, estPrivee, owner, autresParticipants);
        } catch (SQLException e) {
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
    }

    /**
     * Désinscrit un client RMI connecté du salon.
     */
    @Override
    public synchronized void desinscription(InterfaceAffichageClient user) throws RemoteException {
        lst_participants.remove(user);
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
        Message new_message = new Message(-1, message, user.getId_user(), id_discussion);

        // stock le message
        try {
            addMessageToDiscussion(new_message);
        } catch (SQLException e) {
            System.out.println("[Salon] Impossible d'ajouter le message à la discussion : " + e.getMessage());
        }
        lst_messages.add(new_message);

        // distribue le message
        List<InterfaceAffichageClient> aSupprimer = new ArrayList<>();
        for (InterfaceAffichageClient client : lst_participants) {
            try {
                client.affiche(new_message);
            } catch (RemoteException e) {
                aSupprimer.add(client);  // client déconnecté → on le retire
            }
        }
        lst_participants.removeAll(aSupprimer);
    }

    public Stack<Message> getArchive() throws RemoteException{
        Stack<Message> archive = new Stack<>();
        // lst_message null
        int lst_message_size = lst_messages.size();
        for(int i = lst_message_size - 1; i >= lst_message_size - 20 && i >= 0; i--){
            if(lst_messages.get(i) != null){
                archive.add(lst_messages.get(i));
            }
        }
        return archive;
    }

    public List<User> getAdmin() {
        try {
            return RoleDAO.findUserByRole(id_discussion, Role.Admin);
        } catch (Exception e) {
            System.out.println("[Salon] Impossible de trouver l'admin du salon : " + e.getMessage());
            return null;
        }
    }

    public List<User> getUser() {
        try {
            return RoleDAO.findUserByRole(id_discussion, Role.User);
        } catch (Exception e) {
            System.out.println("[Salon] Impossible de trouver les utilisateurs du salon : " + e.getMessage());
            return null;
        }
    }

    // ============================ getters ============================
    public int getSalonId() { return id_discussion; }
    public String getSalonNom() { return nom; }
    public List<InterfaceAffichageClient> getParticipants() { return lst_participants; }
    public Boolean getEstPrivee() { return estPrivee; }

}
