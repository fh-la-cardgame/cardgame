package cardgame.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Baut eine Verbindung zu einer Postgresql Datenbank auf.
 * @author BishaThan
 */
public class DbConnection {
    /**
     * Baut eine DB-Verbindung auf
     * @return Verbindung
     * @throws SQLException 
     */
    public static Connection getPostgresConnection() throws SQLException, ClassNotFoundException{
        String user = "";
        String pw = "";
        String host = "";
        Class.forName("");
        return DriverManager
            .getConnection("jdbc:postgresql://" + host,
            user, pw);
    }
}
