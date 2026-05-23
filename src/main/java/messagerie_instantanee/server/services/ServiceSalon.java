package messagerie_instantanee.server.services;

/**
 * ServiceSalon — utilitaires liés aux salons.
 * 
 * Note : l'ancienne méthode InterfacToUserMinusOwner() a été supprimée.
 * Elle castait InterfaceAffichageClient → User, ce qui était invalide car
 * User n'implémente pas InterfaceAffichageClient.
 * Depuis le refactoring, rsToUser() retourne directement List<User>
 * et les DAO travaillent exclusivement avec User.
 */
public class ServiceSalon {
    // Ajoutez ici les futurs utilitaires salon si nécessaire.
}
