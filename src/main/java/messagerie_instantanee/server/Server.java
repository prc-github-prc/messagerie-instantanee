package messagerie_instantanee.server;

import java.nio.channels.IllegalSelectorException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import messagerie_instantanee.interfaces.InterfaceServeurForum;
import messagerie_instantanee.interfaces.InterfaceSujetDiscussion;
import messagerie_instantanee.server.database.DAO.DiscussionDAO;
import static messagerie_instantanee.server.database.DAO.UserDAO.findUserByUsername;
import static messagerie_instantanee.server.database.DAO.UserDAO.insertUser;
import static messagerie_instantanee.server.services.ServiceServer.salonToDiscussion;
import messagerie_instantanee.server.database.DAO.DiscussionDAO.updateDiscussion;

import messagerie_instantanee.server.database.DatabaseLaucher;
import messagerie_instantanee.server.models.Discussion;
import messagerie_instantanee.server.models.User;


public class Server extends UnicastRemoteObject implements InterfaceServeurForum {

    private Map<String, Salon> map_salons = new HashMap<>();

    public Server() throws RemoteException {
        DatabaseLaucher.initialiser();
        List<Discussion> convs = DiscussionDAO.findAllDiscussions();

        if (convs != null) {
            for (Discussion conv : convs) {
                map_salons.put(conv.getNom_discussion(), new Salon(conv));
                System.out.println(conv.getNom_discussion()); // TODO à enlever après test
            }
        }
    }

    @Override
    public synchronized InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException {
        if (map_salons.get(titre) == null) {
            throw new NoSuchElementException();
        }
        return map_salons.get(titre);
    }

    @Override
    public List<Discussion> listerSalons() throws RemoteException {
        return new ArrayList<>(map_salons.values().stream()
            .map((Salon salon) -> salonToDiscussion(salon))
            .collect(Collectors.toList()));
    }

    @Override
    public Boolean checkId(String pseudo, String rawPassword) throws RemoteException {
        // findUserByUsername retourne désormais directement un User, plus besoin de cast
        User user = findUserByUsername(pseudo);
        if (user == null) return false;
        return rawPassword.equals(user.getPassword());
    }

    @Override
    public Boolean creationUser(String username, String hash) throws RemoteException {
        try {
            insertUser(username, hash);
            return true;
        } catch (SQLException e) {
            throw new IllegalSelectorException();
        }
    }

    /**
     * Crée une discussion en BDD et enregistre le salon dans la map du serveur.
     * owner est un User BDD, pas un stub RMI : le client s'inscrira via inscription() à la connexion.
     */
    @Override
    public synchronized Discussion creationSalon(String nom_Salon, String username, Boolean est_privee) throws RemoteException {
        User owner = findUserByUsername(username);
        map_salons.put(nom_Salon, new Salon(nom_Salon, owner, est_privee));
        return salonToDiscussion(map_salons.get(nom_Salon));
    }

    /**
     * serveur.close gère le stockage en BDD des données courantes.
     */
    public void close() {
        try {
            map_salons.values().stream()
                .forEach((Salon salon) -> DiscussionDAO.updateDiscussion(salon.getSalonNom(), salon.getEstPrivee(), salon.getAdmin(), salon.getUser()));
        } catch (SQLException e) {
            System.out.println("[Server] Erreur lors de la fermeture du serveur : " + e.getMessage());
        }
    }
}
