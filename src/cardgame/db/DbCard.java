package cardgame.db;

import cardgame.classes.Card;
import cardgame.classes.Effect;
import cardgame.classes.EffectType;
import cardgame.classes.GameCard;
import cardgame.classes.Shield;
import cardgame.classes.Type;

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
 * @author BishaThan, Dennis, David
 */
public class DbCard {
    /** Die Connection zur DB **/
    private static Connection c;
    /** Eine Variable zur Verarbeitung von Querys**/
    private PreparedStatement pst;
    /** Die Ergebnismenge, die ein Query liefert**/
    private ResultSet rs;
    /** Die maximale Anzahl an Effekten die eine Karte haben kann**/
    public static final int MAX_EFFEKTS = 5;

    
    /** Gibt eine Liste aller Karten zurueck
     * @return List<Card> Eine Liste aller Karten, ausgelesen aus der DB
     * @throws IllegalArgumentException Wenn die Enums, die aus der DB ausgelesen wurden, nicht existent sind
     */
    public List<Card> getAllCards(){
        List<Card> l = new LinkedList<>();
        
        try {
            c = DbConnection.getPostgresConnection();
            /*
             * Ausfuehren des Selects um alle notwendigen Infos aus Gamecard zu beziehen
             */
            pst = c.prepareStatement("Select g.\"gid\", g.\"name\", g.\"description\", g.\"monster_type\", g.\"atk\", g.\"shield_curr\","+" "
            		+ "g.\"shield_max\", g.\"evo_shield_curr\", g.\"evo_shield_max\" from public.\"Gamecard\" g order by g.\"gid\"");
            rs = pst.executeQuery();
            
            while(rs.next()){
                /*
                 * Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
                 * dem zugehoerigem Enum zugeordnet.
                 */
                Type type = null;
                switch(rs.getString(4)){
                case "Daemon":  type = Type.daemon;		break;
                case "Pflanze": type = Type.pflanze;	break;
                case "Fairy": 	type = Type.fairy;		break;
                case "Human": 	type = Type.human;		break;
                case "Orc":		type = Type.orc;		break;
                case "Dragon": 	type = Type.dragon;		break;
                default: throw new IllegalArgumentException("Kein existenter Type(Enum)");
                }
                /*
                 * Ausfuerung des Joins(ueber Card_Effekt) um die Effekte einer Karte auszulesen
                 */
                PreparedStatement join = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield"+""
                		+ " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g where g.gid = c_e.gid and c_e.eid = e.eid and g.gid = "
                		+ rs.getInt(1));
                ResultSet rs2 = join.executeQuery();
                
                EffectType effect = null;
                Effect[] effects = new Effect[MAX_EFFEKTS];
                int i = 0;
                /*
                 * Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
                 * dem zugehoerigem Enum zugeordnet.
                 */
                while(rs2.next()){
                	switch(rs2.getString(3)){
                    case "destroy": effect = EffectType.destroy; 					break;
                    case "substraction_one": effect = EffectType.substraction_one; 	break;
                    case "substraction_all": effect = EffectType.subtraction_all; 	break;
                    case "addition_one": effect = EffectType.addition_one; 			break;
                    case "addition_all": effect = EffectType.addition_all; 			break;
                    default: throw new IllegalArgumentException("Kein existenter EffectType(Enum)");
                    }
                	effects[i] = new Effect(rs2.getInt(1), rs2.getString(2), effect);
                	i++;
                }
                
                l.add(new GameCard(rs.getInt(1), rs.getString(2), rs.getString(3), type, rs.getInt(5), 
                		new Shield(rs.getShort(6), rs.getShort(7)), new Shield(rs.getShort(8), rs.getShort(9)), null, effects));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return l;
     }
}
