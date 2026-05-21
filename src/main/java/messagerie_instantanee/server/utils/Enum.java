package messagerie_instantanee.server.utils;

public class Enum {
    
    public enum Role {
        Admin, User;

        /**
        * Génère le string de la colonne SQL adapter à la requête
        * 
        * @return String de la colonne SQL correspondant à la granularité sélectionnée
        */
        public String getColonneSQL() {
            return switch (this) {
                case Admin -> "Admin";
                case User -> "User";
            };
        }
    }
}
