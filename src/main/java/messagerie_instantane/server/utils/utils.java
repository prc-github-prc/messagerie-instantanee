package messagerie_instantane.server.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import messagerie_instantane.server.database.DatabaseConnection;

public class utils {
    public static ResultSet executeSQLQuerry(String querry) throws SQLException{
        Connection conn = DatabaseConnection.get_conn();
        return conn.createStatement().executeQuery(querry);
    }
}
