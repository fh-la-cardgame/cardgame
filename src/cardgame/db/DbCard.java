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
     */
    public List<Card> getAllCards(){
        List<Card> l = new LinkedList<>();
        
        try {
            c = DbConnection.getPostgresConnection();
             //Ausfuehren des Selects um alle notwendigen Infos aus Gamecard zu beziehen.
            pst = c.prepareStatement("Select g.\"gid\", g.\"name\", g.\"description\", g.\"monster_type\", g.\"atk\", g.\"shield_curr\","+" "
            		+ "g.\"shield_max\", g.\"evo_shield_curr\", g.\"evo_shield_max\", g.\"evo\" from public.\"Gamecard\" g order by g.\"gid\"");
            rs = pst.executeQuery();
            
            while(rs.next()){
                 // Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
                 // in der Hilfsmethode StringToType dem zugehoerigem Enum zugeordnet.
                Type type = null;
                type = stringToType(rs.getString(4));
                
                 //Ausfuerung des Joins(ueber Card_Effekt) um die Effekte einer Karte auszulesen.
                PreparedStatement join = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield"+""
                		+ " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g where g.gid = c_e.gid and c_e.eid = e.eid and g.gid = "
                		+ rs.getInt(1));
                ResultSet rs2 = join.executeQuery();
                
                EffectType effect = null;
                Effect[] effects = new Effect[MAX_EFFEKTS];
                int i = 0;

                while(rs2.next()){
                    //Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
                    // in der Hilfsmethode stringToEffectType dem zugehoerigem Enum zugeordnet.
                	effect = stringToEffectType(rs2.getString(3));
                	effects[i] = new Effect(rs2.getInt(1), rs2.getString(2), effect);
                	i++;
                }
                
                GameCard evo = null;
                l.add(new GameCard(rs.getInt(1), rs.getString(2), rs.getString(3), type, rs.getInt(5), 
                		new Shield(rs.getShort(6), rs.getShort(7)), new Shield(rs.getShort(8), rs.getShort(9)), evo, effects));
            
        	}
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return l;
     }
    
    
    /** Ordnet einem String einen Enum zu.
     * @param string Der String der als Enum zurueckgegeben werden soll.
     * @return Type Gibt den zum String gehoerenden Enum zurueck.
     * @throws new IllegalArgumentException Wenn der String keinem Enum zugeordnet werden kann.
     */
    private Type stringToType(String string){
    	Type type = null;
    	switch(string){
        case "Daemon":  type = Type.daemon;		break;
        case "Pflanze": type = Type.pflanze;	break;
        case "Fairy": 	type = Type.fairy;		break;
        case "Human": 	type = Type.human;		break;
        case "Orc":		type = Type.orc;		break;
        case "Dragon": 	type = Type.dragon;		break;
        default: throw new IllegalArgumentException("Kein existenter Type(Enum)");
        }
    	return type;
    }
    
    /** Ordnet einem String einen Enum zu.
     * @param string Der String der als Enum zurueckgegeben werden soll.
     * @return EffectType Gibt den zum String gehoerenden Enum zurueck .
     * @throws new IllegalArgumentException Wenn der String keinem Enum zugeordnet werden kann.
     */
    private EffectType stringToEffectType(String string){
    	EffectType effect = null;
    	switch(string){
        case "destroy": effect = EffectType.destroy; 					break;
        case "substraction_one": effect = EffectType.substraction_one; 	break;
        case "substraction_all": effect = EffectType.subtraction_all; 	break;
        case "addition_one": effect = EffectType.addition_one; 			break;
        case "addition_all": effect = EffectType.addition_all; 			break;
        default: throw new IllegalArgumentException("Kein existenter EffectType(Enum)");
        }
    	return effect;
    }
    
   
    /** Fuegt neue Beziehungen zwischen Effekte und Cards in die Tabelle ein.
     * Um Redundanz zu vermeiden verhindert diese Funktion doppelte Eintraege.
     * @param gid Die Id der GameCard.
     * @param eid Die Id des Effects.
     * @param shield Der Shield bei dem der Effekt ausgeloest wird.
     * @return boolean true, falls der Eintrag noch nicht vorhanden war, false, falls der Eintrag schon existiert.
     */
    public boolean insert_Card_Effect(int gid, int eid, int shield){
    	PreparedStatement pst, pst2;
    	int length_old = 0, length_new = 0;
    	ResultSet result;
    	try{
    		c = DbConnection.getPostgresConnection();
    		pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
    		result = pst.executeQuery();
    		result.next();
    		length_old = result.getInt(1);
    		pst2 = c.prepareStatement("insert into \"Card_Effect\" (gid, eid, shield) select "
    				+ ""+gid+", "+eid+", "+shield+" where not exists (select gid, eid, shield from \"Card_Effect\" where gid = "
    				+ ""+gid+" and eid = "+eid+" and shield = "+shield+")");
    		pst2.executeUpdate();
    		pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
    		result = pst.executeQuery();
    		result.next();
    		length_new = result.getInt(1);
    		
    	}catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    	return length_old < length_new;
    }
}
