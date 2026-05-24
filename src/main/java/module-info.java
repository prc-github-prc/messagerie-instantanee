module messagerie_instantanee {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires java.rmi;
    requires com.google.gson;
    requires javafx.graphics;
    requires org.kordamp.ikonli.javafx;

    // UI
    opens messagerie_instantanee.UI.controllers to javafx.fxml;
    exports messagerie_instantanee.UI;
    
    // client 
    opens messagerie_instantanee.client to javafx.fxml;
    exports messagerie_instantanee.client;

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