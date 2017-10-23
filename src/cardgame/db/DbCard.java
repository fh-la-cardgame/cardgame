package cardgame.db;

import cardgame.classes.Card;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Zugriff auf die DB-Tabelle der Karten. (Lesen, Schreiben, Aktualisieren)
 * @author BishaThan
 */
public class DbCard {
    
    private Connection c;
    private PreparedStatement pst;
    private ResultSet rs;

    
    public List<Card> selectAll(){

        List<Card> l = new LinkedList<>();
        
        try {
            c = DbConnection.getPostgresConnection();
            pst = c.prepareStatement("Select g.\"gid\", g.\"name\" from public.\"Gamecard\" g;");
            rs = pst.executeQuery();
            
            while(rs.next()){
                System.out.println("Datensatz");
                System.out.println(rs.getInt(1) + ":" +  rs.getString(2));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return l;
         }
        
    
}
