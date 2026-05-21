package messagerie_instantanee.server;

import java.util.List;

import messagerie_instantanee.client.*;
import messagerie_instantanee.interfaces.*;
import messagerie_instantanee.server.models.*;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;

import static messagerie_instantanee.server.utils.ResultSetConverter.*;

import java.rmi.RemoteException;

public class Salon implements InterfaceSujetDiscussion{
    private int id;
    private String nom;
    List<User> participants;

    /**
     * 
     * @param id
     * @throws RemoteException
     * 
     * Crée un Salon à partir d'une discussion déjà existante.
     */
    public Salon(Discussion discussion) throws RemoteException{
        this.id = discussion.getId_discussion();
        this.nom = discussion.getNom_discussion();
        this.participants = discussion.getParticipants();
    }

    public Salon(String nom) throws RemoteException {
        throw new UnsupportedOperationException("Unimplemented method 'nouveau Salon'");
    }

    /**
     * 
     * @param c
     * @return
     * @throws RemoteException
     * 
     * La fonction inscription récupère des données fournies par l'utilisateur et effectue une requête auprès du DAO.
     */
    @Override
    public void inscription(InterfaceAffichageClient c) throws RemoteException {
        // I. Récupérer un objet client depuis le DAO.
        // II. L'ajouter à la liste des participants.
        throw new UnsupportedOperationException("Unimplemented method 'inscription'");
    }

    /**
     * @param c
     * @return
     * @throws RemoteException
     * 
     * La fonction desInscription supprime le compte de l'utilisateur effectuant la requête.
     */
    @Override
    public void desInscription(InterfaceAffichageClient c) throws RemoteException {
        // I. Effectuer une requête au DAO avec l'id de l'utilisateur, pour supprimer les données lui correspondant dans la BBD.
        // II. Selon le retour du DAO, affichage d'un message via c.affiche() en fonction de la réponse du DA0 : Réussite de la désinscription ou erreur quelconque.
        throw new UnsupportedOperationException("Unimplemented method 'desInscription'");
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
        
        for (Client p : participants) {
            p.affiche(Message);
        }
    }
}
