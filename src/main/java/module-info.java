module messagerie_instantanee {

    requires javafx.controls;
    requires transitive javafx.graphics;
    requires transitive javafx.fxml;
    requires transitive java.sql;

    requires java.rmi;
    requires com.google.gson;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    
    // UI
    opens messagerie_instantanee.UI.controllers to javafx.fxml;
    exports messagerie_instantanee.UI;
    
    // client 
    opens messagerie_instantanee.client to javafx.fxml;
    exports messagerie_instantanee.client;

    // client models — opens à Gson pour la désérialisation par réflexion
    opens messagerie_instantanee.client.models to com.google.gson;
    exports messagerie_instantanee.client.models;

    // client services
    exports messagerie_instantanee.client.services;

    // interfaces
    exports messagerie_instantanee.interfaces;

    // server
    exports messagerie_instantanee.server;

    // database
    exports messagerie_instantanee.server.database;
    exports messagerie_instantanee.server.database.DAO;

    // models
    exports messagerie_instantanee.server.models;

    // utils
    exports messagerie_instantanee.server.utils;
}
