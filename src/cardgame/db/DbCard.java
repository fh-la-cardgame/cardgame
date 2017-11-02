package cardgame.db;

import cardgame.classes.Card;
import cardgame.classes.Effect;
import cardgame.classes.EffectType;
import cardgame.classes.GameCard;
import cardgame.classes.Shield;
import cardgame.classes.SpecialCard;
import cardgame.classes.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
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
    /** Die maximale Anzahl an Effekten die eine Karte haben kann**/
    public static final int MAX_EFFEKTS = 5;

    
    /** Gibt eine Liste aller Karten zurueck
     * @return List<Card> Eine Liste aller Karten, ausgelesen aus der DB
     */
    public List<Card> getAllCards(){
        List<Card> l = new LinkedList<>();
        PreparedStatement pst, join;
        ResultSet rs, rs2;
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
                join = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield"+""
                		+ " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g where g.gid = c_e.gid and c_e.eid = e.eid and g.gid = "
                		+ rs.getInt(1));
                rs2 = join.executeQuery();
                
                EffectType effect = null;
                Effect[] effects = new Effect[MAX_EFFEKTS];
                int i = 0;

                while(rs2.next()){
                    //Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
                    // in der Hilfsmethode stringToEffectType dem zugehoerigem Enum zugeordnet.
                	effect = stringToEffectType(rs2.getString(3));
                	effects[i] = new Effect(rs2.getInt(1), rs2.getString(2), effect, rs2.getInt(4));
                	i++;
                }
                
                GameCard evo = null;
                if(rs.getInt(10) != 0){
                    evo = integerToGamecard(rs.getInt(10));
                }
                l.add(new GameCard(rs.getInt(1), rs.getString(2), rs.getString(3), type, rs.getInt(5), 
                		new Shield(rs.getShort(6), rs.getShort(7)), new Shield(rs.getShort(8), rs.getShort(9)), evo, effects));
            
        	}
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally{
    		try{
    		c.close();
    		}catch(SQLException ex){
    			Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
    		}
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
    
    private Effect[] getEffects(ResultSet rs, ResultSet rs2){
    	EffectType effect = null;
    	Effect[] effects = null;
    	try{
	        effects = new Effect[rs.getShort(7)];
	        while(rs2.next()){	
				effect = stringToEffectType(rs2.getString(3));
				if(rs2.getShort(5) != -1){
	        	effects[rs2.getShort(5)] = new Effect(rs2.getInt(1), rs2.getString(2), effect, rs2.getInt(4));
				}else{
					int i = 0;
					while(i<rs.getShort(7)){
						effects[i]= new Effect(rs2.getInt(1), rs2.getString(2), effect, rs2.getInt(4));
						i++;
					}
						
				}
	        }
    	}catch(SQLException ex){
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return effects;
    }
    /**
     * Weist der id der Gamecard(integer) eine Gamecard zu
     * @param id Id der Gamecard die Evolution
     * @return Gamecard 
     */
    private GameCard integerToGamecard(int id){
    	GameCard result = null;
    	ResultSet rs, rs2;
    	PreparedStatement pst;
    	Effect[] effects = null;
    	try{
    		c = DbConnection.getPostgresConnection();
	         //Ausfuehren des Selects um alle notwendigen Infos aus Gamecard zu beziehen.
	        pst = c.prepareStatement("Select g.\"gid\", g.\"name\", g.\"description\", g.\"monster_type\", g.\"atk\", g.\"shield_curr\","+" "
	        		+ "g.\"shield_max\", g.\"evo_shield_curr\", g.\"evo_shield_max\" from public.\"Gamecard\" g where g.gid = "+id);
	        rs = pst.executeQuery();
	        rs.next();
            // Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
            // in der Hilfsmethode StringToType dem zugehoerigem Enum zugeordnet.
           Type type = null;
           type = stringToType(rs.getString(4));
           
           PreparedStatement join = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield"+""
           		+ " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g where g.gid = c_e.gid and c_e.eid = e.eid and g.gid = "
           		+ rs.getInt(1));
           rs2 = join.executeQuery();
           
           effects = getEffects(rs, rs2);
        	result = new GameCard(rs.getInt(1), rs.getString(2), rs.getString(3), type, rs.getInt(5), 
                		new Shield(rs.getShort(6), rs.getShort(7)), new Shield(rs.getShort(8), rs.getShort(9)),
                		null, effects.clone());
        
    	} catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } 
    	
        return result;
    
    }
    
    
    
    public List<Card> getDeck(String deckName){
    	PreparedStatement pst1, pst2;
    	ResultSet rs1, rs2;
    	//EffectType effect;
    	Effect[] effects = null;
    	//deckName = deckName.toLowerCase();
    	List<Card> deck = new ArrayList<>();
    	try{
    		c = DbConnection.getPostgresConnection();
    		pst1 = c.prepareStatement("select g.gid, g.name, g.description, monster_type, atk, shield_curr, "
    				+"shield_max, evo_shield_curr, evo_shield_max, evo "
    				+"from \"Gamecard\" g , \"Deck_Cards\" dc, \"Deck\" d "
    				+"where g.gid = dc.gid "
    				+"and d.did = dc.did "
    				+"and d.name = '"+deckName+"'");
	    	rs1 = pst1.executeQuery();
	    	while(rs1.next()){
	    		System.out.println("KONTROLLE: "+rs1.getInt(8)+" "+rs1.getInt(9));
	    		pst2 = c.prepareStatement("select e.eid, e.description, effect_type, effect_number, shield "
	    				+"from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
	    				+"where e.eid = c_e.eid "
	    				+"and c_e.gid = g.gid "
	    				+"and g.gid = "+rs1.getInt(1));
	    		
	    		rs2 = pst2.executeQuery();
	    		effects = getEffects(rs1, rs2);
	    		 GameCard evo = null;
	             if(rs1.getInt(10) != 0){
	                 evo = integerToGamecard(rs1.getInt(10));
	             }
	           
	             deck.add(new GameCard(rs1.getInt(1), rs1.getString(2), rs1.getString(3), 
	            		 stringToType(rs1.getString(4)), rs1.getInt(5), new Shield(rs1.getShort(6), 
	            		rs1.getShort(7)), new Shield(rs1.getShort(8), rs1.getShort(9)), evo, effects.clone()));
	    	}
	    	pst1 = c.prepareStatement("select sc.sid, sc.name, description, type "
	    			+"from \"Specialcard\" sc, \"Deck_Cards\" dc, \"Deck\" d "
	    			+" where d.name = '"+deckName+"'"
	    			+" and sc.sid = dc.sid"
	    			+" and dc.did = d.did"
	    			+" order by sc.sid");
	    	rs1 = pst1.executeQuery();
	    	
	    	
	    	while(rs1.next()){
	    		
	    		List<Effect> effects_list = new ArrayList<>();
	    		pst2 = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number "
	    				+"from \"Effecte\" e, \"Specialcard_Effect\" ce, \"Specialcard\" "
	    				+"where sid = "+rs1.getInt(1)
	    				+"and e.eid = ce.eid "
	    				+"and scid = sid");
	    		rs2 = pst2.executeQuery();
	    		while(rs2.next())
	    		effects_list.add(new Effect(rs2.getInt(1), rs2.getString(2), stringToEffectType(rs2.getString(3)), rs2.getInt(4)));
	    		deck.add(new SpecialCard(rs1.getInt(1), rs1.getString(2), rs1.getString(3), stringToType(rs1.getString(4)), effects_list));
	    	}
	    	
    	} catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
    		try{
    		c.close();
    		}catch(SQLException ex){
    			Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
    		}
    	}
    	return deck;
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
    		if(shield == -1){
    			pst2 = c.prepareStatement("insert into \"Card_Effect\" (gid, eid, shield) select "
        				+ ""+gid+", "+eid+", -1 where not exists (select gid, eid, shield from \"Card_Effect\" where gid = "
        				+ ""+gid+" and (shield > -1 or shield = -1))");
    		}else if(shield > -1){
    		pst2 = c.prepareStatement("insert into \"Card_Effect\" (gid, eid, shield) select "
    				+ ""+gid+", "+eid+", "+shield+" where not exists (select gid, eid, shield from \"Card_Effect\" where gid = "
    				+ ""+gid+" and (shield = "+shield+" or shield = -1))");
    		}else{
    			throw new IllegalArgumentException("Wertebereich fuer Shield: [-1,"+MAX_EFFEKTS+"]");
    		}
    		pst2.executeUpdate();
    		pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
    		result = pst.executeQuery();
    		result.next();
    		length_new = result.getInt(1);
    		c.close();
    	}catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    	finally{
    		try{
    		c.close();
    		}catch(SQLException ex){
    			Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
    		}
    	}
    	return length_old < length_new;
    }
    
    
}
