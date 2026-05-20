package messagerie_instantane.server;

import java.util.List;
import java.rmi.RemoteException;
import messagerie_instantane.interfaces.*;
import messagerie_instantane.client.*;

public class Discussion implements InterfaceSujetDiscussion{
    List<Client> participants;
    String nom;

    public Discussion(String nom) throws RemoteException{
        this.nom = nom;
        // Récupérer la liste des participants.
    }

    /**
     * 
     * @param c
     * @return
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
