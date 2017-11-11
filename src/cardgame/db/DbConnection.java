package cardgame.db;


import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Baut eine Verbindung zu einer Postgresql Datenbank auf.
 *
 * @author BishaThan
 */
public class DbConnection {

    public static String[] readData() {
        String fileData = System.getProperty("user.home") + "\\daten";
        File f = new File(fileData);
        if (f.isDirectory()) {
            throw new IllegalArgumentException("Der File ist eine Directory");
        }
        if (!f.exists()) {
            try (Writer w = new FileWriter(fileData)) {
                String user = JOptionPane.showInputDialog("Username");
                String p = JOptionPane.showInputDialog("PW");
                String ip = JOptionPane.showInputDialog("IP:Port");
                String datenbank = JOptionPane.showInputDialog("Datenbankname");
                w.write(user + "\n" + p + "\n" + ip + "\n" + datenbank);
                w.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        try (Reader r = new FileReader(fileData);
             BufferedReader b = new BufferedReader(r)) {
            String user = b.readLine();
            String p = b.readLine();
            String ip = b.readLine();
            String datenbank = b.readLine();
            return new String[]{user, p, ip + "/" + datenbank};
        } catch (IOException err) {
            System.out.println(err);
        }
        return null;
    }

    /**
     * Baut eine DB-Verbindung auf
     *
     * @return Verbindung
     * @throws SQLException
     */
    public static Connection getPostgresConnection() throws SQLException, ClassNotFoundException {
        String[] daten = readData();
        String user = daten[0];
        String pw = daten[1];
        String host = daten[2];
        Class.forName("org.postgresql.Driver");
        return DriverManager
                .getConnection("jdbc:postgresql://" + host,
                        user, pw);
    }
}
