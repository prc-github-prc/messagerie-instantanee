package messagerie_instantane.server;


import java.rmi.RemoteException;
import messagerie_instantane.interfaces.*;

public class Discussion implements InterfaceSujetDiscussion{
    /**
     * 
     * @param c
     * @return
     * 
     * La fonction inscription récupère des données fournies par l'utilisateur et effectue une requête auprès du DAO.
     */
    @Override
    public void inscription(InterfaceAffichageClient c) throws RemoteException {
        // I. Récupérer données utilisateurs
        // II. Effectuer diverses vérifications sur ces données, avec renvoi d'erreur si problème
        // III. Si pas d'erreur, envoie d'une requête au DAO.
        // IV. Affichage d'un message via c.affiche() en fonction de la réponse du DA0 : Réussite de l'inscription ou erreur quelconque.
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
        // I. Effectuer une requête au DAO, probablement avec l'id de l'utilisateur, pour supprimer les données lui correspondant dans la BBD.
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
        // I. Stocker le message en BDD.
        // II. Diffuser le message à tous les clients.
        throw new UnsupportedOperationException("Unimplemented method 'diffuse'");
    }
    
}
