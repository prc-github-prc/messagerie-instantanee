package messagerie_instantane.server.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import messagerie_instantane.server.database.DatabaseConnexion;

public class utils {
    public static ResultSet executeSQLQuerry(String querry) throws SQLException{
        Connection conn = DatabaseConnexion.getConn();
        return conn.createStatement().executeQuery(querry);
    }
}
