package messagerie_instantanee.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import messagerie_instantanee.server.database.DatabaseConnection;

public class ExecSqlQuerry {
    /**
     * 
     * @param querry
     * @return
     * @throws SQLException
     * 
     * Exécute une requête SQL.
     */
    public static ResultSet executeSQLQuerry(String querry) throws SQLException{
        Connection conn = DatabaseConnection.get_conn();
        return conn.createStatement().executeQuery(querry);
    }

    /**
     * 
     * @param querry
     * @return
     * @throws SQLException
     * 
     * Effectue une insertion dans la BDD.
     */
    public static ResultSet excuteInsertSQL(String querry) throws SQLException{
        Connection conn = DatabaseConnection.get_conn();
        PreparedStatement stmt = conn.prepareStatement(querry, Statement.RETURN_GENERATED_KEYS);
        stmt.executeUpdate();
        return stmt.getGeneratedKeys();
    }
}
