package cardgame.db;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import cardgame.classes.Card;
import cardgame.classes.Effect;
import cardgame.classes.EffectType;
import cardgame.classes.GameCard;
import cardgame.classes.Shield;
import cardgame.classes.SpecialCard;
import cardgame.classes.Type;

public class EmbeddedDB {
	public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String JDBC_URL = "jdbc:derby:EmbDB;create=true";
	private static Connection c;
	private static Connection c2;
	

	/**Updaten der EmbeddedDB.
	 * 
	 * @return Wenn erfolgreich(EmbDB identisch zu ServerDB) true, sonst false.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean updateEmbDB(){
		try{
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
			Class.forName(DRIVER);
			if(!updateDeck())				return false;
			if(!updateEffecte())			return false;
			if(!updateGameCard())			return false;
			if(!updateSpecialCard())		return false;
			if(!updateCard_Effect()) 		return false;
			if(!updateDeck_Cards())			return false;
			if(!updateSpecialCard_Effect()) return false;
			
			
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return true;
	}
	/**Prueft ob die Anzahl der Zeilen in der EmbDB und ServerDB einer Tabelle gleich sind.
	 * 
	 * @param table Die Tabelle, die geprueft werden soll.
	 * @return Wenn beide DB in der Tabelle gleich viele Zeilen haben, dann true, sonst false.
	 * @throws SQLException
	 */
	private boolean checkRowCountEquality(String table) {
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		
		ResultSet rs1 = c.prepareStatement("SELECT COUNT(*) FROM "+table).executeQuery();
		ResultSet rs2 = c2.prepareStatement("select count(*) from \""+table+"\"").executeQuery();
		rs1.next();
		rs2.next();
		if(rs1.getInt(1) == rs2.getInt(1)){
			System.out.println("Erfolgreiches Updaten: "+table);
			return true;
		}
		
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		System.out.println("Updaten gescheitert: "+table);
		return false;
	}
	
	
	
	/**Updaten des Decks.
	 * 
	 * @return Wenn erfolgreich(EmbDB Deck identisch zu ServerDB Deck) true, sonst false.
	 * @throws SQLException
	 */
	public boolean updateDeck(){
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		
		c.prepareStatement("CREATE TABLE Deck (did INT NOT NULL PRIMARY KEY, "
				+ "name VARCHAR(30))").executeUpdate();
		ResultSet rs0 = c2.prepareStatement("select * from \"Deck\"").executeQuery();
		while(rs0.next()){
		//	System.out.println(rs0.getInt(1)+" - "+rs0.getString(2));
			PreparedStatement pst = c.prepareStatement("INSERT INTO DECK VALUES(?, ?)");
			pst.setInt(1, rs0.getInt(1));
			pst.setString(2, rs0.getString(2));
			pst.executeUpdate();
		}
		
//		ResultSet rs = c.prepareStatement("SELECT * FROM DECK").executeQuery();
//		System.out.println("Ausfuehrung");
//		while(rs.next()){
//			System.out.println(rs.getInt(1)+" "+rs.getString(2));
//		}
		
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return checkRowCountEquality("Deck");
	}
		
	/**Updaten der Effecte.
	 * 
	 * @return Wenn erfolgreich(EmbDB Effecte identisch zu ServerDB Effecte) true, sonst false.
	 * @throws SQLException
	 */
	public boolean updateEffecte(){
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		
		
		c.prepareStatement("CREATE TABLE Effecte (eid INT NOT NULL PRIMARY KEY,"
				+ " description VARCHAR(100),"
				+ " effect_type VARCHAR(40),"
				+ " effect_number INT)").executeUpdate();
		
		ResultSet rs0 = c2.prepareStatement("select * from \"Effecte\"").executeQuery();
		while(rs0.next()){
		//	System.out.println(rs0.getInt(1)+" - "+rs0.getString(2)+ rs0.getString(3)+" "+rs0.getInt(4));
			PreparedStatement pst = c.prepareStatement("INSERT INTO EFFECTE VALUES(?, ?, ?, ?)");
			pst.setInt(1, rs0.getInt(1));
			pst.setString(2, rs0.getString(2));
			pst.setString(3, rs0.getString(3));
			pst.setInt(4, rs0.getInt(4));
			//pst.setBlob(5, new javax.sql.rowset.serial.SerialBlob(rs0.getBytes(5)));
			pst.executeUpdate();
		}
		
//		ResultSet rs = c.prepareStatement("SELECT eid, description, effect_type, effect_number FROM Effecte").executeQuery();
//		while(rs.next()){
//			System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getInt(4));
//		}
		
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return checkRowCountEquality("Effecte");
	}
		
	
	/**Updaten der Gamecard.
	 * 
	 * @return Wenn erfolgreich(EmbDB Gamecard identisch zu ServerDB Gamecard) true, sonst false.
	 * @throws SQLException
	 */
	public boolean updateGameCard(){
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		
		c.prepareStatement("CREATE TABLE Gamecard ("
				+ " gid INT NOT NULL PRIMARY KEY,"
				+ " name VARCHAR(30),"
				+ " description VARCHAR(605),"
				+ " monster_type VARCHAR(40),"
				+ " atk INT,"
				+ " shield_curr INT,"
				+ " shield_max INT,"
				+ " evo_shield_curr INT,"
				+ " evo_shield_max INT,"
				+ " image BLOB, "
				+ " evo INT)").executeUpdate();
		
		ResultSet rs0 = c2.prepareStatement("select * from \"Gamecard\"").executeQuery();
		while(rs0.next()){
			//System.out.println(rs0.getInt(1)+" - "+rs0.getString(2)+" "+rs0.getInt(5));
			PreparedStatement pst = c.prepareStatement("INSERT INTO Gamecard (gid, name, description, monster_type, atk, shield_curr, shield_max, evo_shield_curr, evo_shield_max, image, evo) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pst.setInt(1, rs0.getInt(1));
			pst.setString(2, rs0.getString(2));
			pst.setString(3, rs0.getString(3));
			pst.setString(4, rs0.getString(4));
			pst.setInt(5, rs0.getInt(5));
			pst.setInt(6, rs0.getInt(6));
			pst.setInt(7, rs0.getInt(7));
			pst.setInt(8, rs0.getInt(8));
			pst.setInt(9, rs0.getInt(9));
			rs0.getBytes(10);
			if(rs0.wasNull()){
				pst.setNull(10, java.sql.Types.BLOB);
			}else
			pst.setBlob(10, new javax.sql.rowset.serial.SerialBlob(rs0.getBytes(10)));
			rs0.getInt(11);
			if(rs0.wasNull()){
				pst.setNull(11, java.sql.Types.INTEGER);
			}else
			pst.setInt(11, rs0.getInt(11));
			pst.executeUpdate();
		}
		
//		ResultSet rs = c.prepareStatement("SELECT gid, name, atk FROM Gamecard").executeQuery();
//		System.out.println("Ausfuehrung:");
//		while(rs.next()){
//			System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getInt(3));
//		}
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return checkRowCountEquality("Gamecard");
	}
		
	
	/**Updaten der Specialcard.
	 * 
	 * @return Wenn erfolgreich(EmbDB Specialcard identisch zu ServerDB Specialcard) true, sonst false.
	 * @throws SQLException
	 */
	public boolean updateSpecialCard(){
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		
		c.prepareStatement("CREATE TABLE Specialcard ("
				+ " sid INT NOT NULL PRIMARY KEY,"
				+ " name VARCHAR(30),"
				+ " description VARCHAR(300),"
				+ " type VARCHAR(40),"
				+ " image BLOB)").executeUpdate();
		
		ResultSet rs0 = c2.prepareStatement("select * from \"Specialcard\"").executeQuery();
		while(rs0.next()){
		//	System.out.println(rs0.getInt(1)+" - "+rs0.getString(2)+" "+rs0.getString(4));
			PreparedStatement pst = c.prepareStatement("INSERT INTO Specialcard VALUES(?, ?, ?, ?, ?)");
			pst.setInt(1, rs0.getInt(1));
			pst.setString(2, rs0.getString(2));
			pst.setString(3, rs0.getString(3));
			pst.setString(4, rs0.getString(4));
			rs0.getBytes(5);
			if(rs0.wasNull()){
				pst.setNull(5, java.sql.Types.BLOB);
			}else
			pst.setBlob(5, new javax.sql.rowset.serial.SerialBlob(rs0.getBytes(5)));
			pst.executeUpdate();
		}
		
//		ResultSet rs = c.prepareStatement("SELECT sid, name, type FROM Specialcard").executeQuery();
//		System.out.println("Ausfuehrung:");
//		while(rs.next()){
//			System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getString(3));
//		}
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return checkRowCountEquality("Specialcard");
	}
		
	
	/**Updaten der Card_Effect.
	 * 
	 * @return Wenn erfolgreich(EmbDB Card_Effecte identisch zu ServerDB Card_Effecte) true, sonst false.
	 * @throws SQLException
	 */
	public boolean updateCard_Effect(){
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		
		c.prepareStatement("CREATE TABLE Card_Effect ("
				+ " c_eid INT NOT NULL PRIMARY KEY,"
				+ " gid INT,"
				+ " eid INT,"
				+ " shield INT,"
				+ " evo_shield INT,"
				+ "FOREIGN KEY(gid) REFERENCES Gamecard(gid),"
				+ "FOREIGN KEY(eid) REFERENCES Effecte(eid))").executeUpdate();
		
		ResultSet rs0 = c2.prepareStatement("select * from \"Card_Effect\"").executeQuery();
		while(rs0.next()){
			//System.out.println(rs0.getInt(1)+" - "+rs0.getInt(2)+" "+rs0.getInt(5));
			PreparedStatement pst = c.prepareStatement("INSERT INTO Card_Effect VALUES(?, ?, ?, ?, ?)");
			pst.setInt(1, rs0.getInt(1));
			pst.setInt(2, rs0.getInt(2));
			pst.setInt(3, rs0.getInt(3));
			rs0.getInt(4);
			if(rs0.wasNull()){
				pst.setNull(4, java.sql.Types.INTEGER);
			}else
			pst.setInt(4, rs0.getInt(4));
			rs0.getInt(5);
			if(rs0.wasNull()){
				pst.setNull(5, java.sql.Types.INTEGER);
			}else
			pst.setInt(5, (rs0.getInt(5)));
			pst.executeUpdate();
		}
		
//		ResultSet rs = c.prepareStatement("SELECT c_eid, shield, evo_shield FROM Card_Effect").executeQuery();
//		System.out.println("Ausfuehrung:");
//		while(rs.next()){
//			System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getString(3));
//		}
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return checkRowCountEquality("Card_Effect");
	}
		
		//FUNKTIONIERT NOCH NICHT: VLLT WEGEN FEHLENDER PK, DIE WIEDER ENTFERNT WURDEN.
	/**Updaten der Deck_Cards.
	 * 
	 * @return Wenn erfolgreich(EmbDB Deck_Cards identisch zu ServerDB Deck_Cards) true, sonst false.
	 * @throws SQLException
	 */
	public boolean updateDeck_Cards(){
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		c.prepareStatement("CREATE TABLE DECK_CARDS ("
				+ " c_did INT NOT NULL PRIMARY KEY,"
				+ " did INT,"
				+ " gid INT,"
				+ " sid INT,"
				+ "FOREIGN KEY(gid) REFERENCES Gamecard(gid),"
				+ "FOREIGN KEY(did) REFERENCES Deck(did), "
				+ "FOREIGN KEY(sid) REFERENCES Specialcard(sid))").executeUpdate();
		
		ResultSet rs0 = c2.prepareStatement("select * from \"Deck_Cards\"").executeQuery();
		while(rs0.next()){
			//System.out.println(rs0.getInt(1)+" - "+rs0.getInt(2)+" "+rs0.getInt(3)+" "+rs0.getInt(4));
			PreparedStatement pst = c.prepareStatement("INSERT INTO Deck_Cards VALUES(?, ?, ?, ?)");
			pst.setInt(1, rs0.getInt(1));
			pst.setInt(2, rs0.getInt(2));
			rs0.getInt(3);
			if(rs0.wasNull()){
				pst.setNull(3, java.sql.Types.INTEGER);
			}else
			pst.setInt(3, rs0.getInt(3));
			
			rs0.getInt(4);
			if(rs0.wasNull()){
				pst.setNull(4, java.sql.Types.INTEGER);
			}else
			pst.setInt(4, rs0.getInt(4));
			pst.executeUpdate();
		}
		
//		ResultSet rs = c.prepareStatement("SELECT c_did, did, gid, sid FROM DECK_CARDS").executeQuery();
//		System.out.println("Ausfuehrung:");
//		while(rs.next()){
//			System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4));
//		}
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return checkRowCountEquality("Deck_Cards");
	}
		
	
	/**Updaten der SpecialCard_Effect.
	 * 
	 * @return Wenn erfolgreich(EmbDB Specialcard_Effecte identisch zu ServerDB Specicalcard_Effecte) true, sonst false.
	 * @throws SQLException
	 */
	public boolean updateSpecialCard_Effect(){
		try {
			c = DriverManager.getConnection(JDBC_URL);
			c2 = DbConnection.getPostgresConnection();
		
		c.prepareStatement("CREATE TABLE SPECIALCARD_EFFECT ("
				+ " sc_eid INT NOT NULL PRIMARY KEY,"
				+ " scid INT,"
				+ " eid INT,"
				+ "FOREIGN KEY(scid) REFERENCES Specialcard(sid),"
				+ "FOREIGN KEY(eid) REFERENCES Effecte(eid))").executeUpdate();
		
		ResultSet rs0 = c2.prepareStatement("select * from \"Specialcard_Effect\"").executeQuery();
		while(rs0.next()){
			//System.out.println(rs0.getInt(1)+" - "+rs0.getInt(2)+" "+rs0.getInt(3));
			PreparedStatement pst = c.prepareStatement("INSERT INTO SPECIALCARD_EFFECT VALUES(?, ?, ?)");
			pst.setInt(1, rs0.getInt(1));
			pst.setInt(2, rs0.getInt(2));
			pst.setInt(3, rs0.getInt(3));
			pst.executeUpdate();
		}
		
//		ResultSet rs = c.prepareStatement("SELECT sc_eid, scid, eid FROM SPECIALCARD_EFFECT").executeQuery();
//		System.out.println("Ausfuehrung:");
//		while(rs.next()){
//			System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3));
//		}
		
		} catch (SQLException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
		}finally {
            try {
                c.close();
                c2.close();
            } catch (SQLException ex) {
                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		return checkRowCountEquality("Card_Effect");
	}
	
	
	
	/**
     * Liest alle Karten und Specialcards in eine Liste ein.
     *
     * @param deckName Name des Decks
     * @return Gibt eine List aller Karten zurueck
     */
    public List<Card> getDeckWithoutImage(String deckName) {
        PreparedStatement selectGamecard, selectCard_Effect, selectCard_EvoEffect, selectSpecialcard, selectSpecialcardEffect;
        ResultSet resultCard, resultEffect, resultEvoEffect;
        Effect[] effects = null;
        Effect[] evoEffects = null;
        //deckName = deckName.toLowerCase();
        List<Card> deck = new ArrayList<>();
        try {
            c = DriverManager.getConnection(JDBC_URL);

            selectGamecard = c.prepareStatement("SELECT g.gid, g.name, g.description, monster_type, atk, shield_curr, "
                    + "shield_max, evo_shield_curr, evo_shield_max, evo "
                    + "FROM Gamecard AS g , Deck_Cards AS dc, Deck AS d "
                    + "WHERE g.gid = dc.gid "
                    + "AND d.did = dc.did "
                    + "AND d.name = ? "
//                    + "AND LOWER(d.name) = LOWER(?)" 
                    + "AND g.gid NOT IN (SELECT evo FROM Gamecard WHERE evo IS NOT NULL)");
            selectGamecard.setString(1, deckName);
            resultCard = selectGamecard.executeQuery();
            

            while (resultCard.next()) {
                int gId = resultCard.getInt(1);
                String gName = resultCard.getString(2);
                String gDescription = resultCard.getString(3);
                String gType = resultCard.getString(4);
                int gAtk = resultCard.getInt(5);
                short gShield_curr = resultCard.getShort(6);
                short gShield_max = resultCard.getShort(7);
                short gEvo_shield_curr = resultCard.getShort(8);
                short gEvo_shield_max = resultCard.getShort(9);
                int gEvo = resultCard.getInt(10);
                

                selectCard_Effect = c.prepareStatement("SELECT e.eid, e.description, effect_type, effect_number, shield "
                        + "FROM Effecte AS e, Card_Effect AS c_e, Gamecard AS g "
                        + "WHERE e.eid = c_e.eid "
                        + "AND c_e.gid = g.gid "
                        + "AND shield IS NOT NULL "
                        + "AND g.gid = ?");
                selectCard_Effect.setInt(1, gId);
                resultEffect = selectCard_Effect.executeQuery();

                selectCard_EvoEffect = c.prepareStatement("SELECT e.eid, e.description, effect_type, effect_number, c_e.evo_shield "
                        + "FROM Effecte AS e, Card_Effect AS c_e, Gamecard AS g "
                        + "WHERE e.eid = c_e.eid "
                        + "AND c_e.gid = g.gid "
                        + "AND evo_shield IS NOT NULL "
                        + "AND g.gid = ?");
                selectCard_EvoEffect.setInt(1, gId);
                resultEvoEffect = selectCard_EvoEffect.executeQuery();

                effects = getEffects(gShield_max, resultEffect);
                evoEffects = getEvoEffects(gEvo_shield_max, resultEvoEffect);

                GameCard evo = null;
                if (gEvo != 0) {
                    evo = integerToGamecard(gEvo);
                }

                deck.add(new GameCard(gId, gName, gDescription,
                        stringToType(gType), null, gAtk, new Shield(gEvo_shield_curr,
                        gEvo_shield_max), new Shield(gShield_curr, gShield_max), evo, effects.clone(), evoEffects.clone()));
            }

            //Ab hier kommen die Specialcards:
            selectSpecialcard = c.prepareStatement("SELECT sc.sid, sc.name, description, type "
                    + "FROM Specialcard AS sc, Deck_Cards AS dc, Deck AS d "
                    + " WHERE LOWER(d.name) = LOWER(?)"
                    + " AND sc.sid = dc.sid"
                    + " AND dc.did = d.did"
                    + " ORDER BY sc.sid");
            selectSpecialcard.setString(1, deckName);
            resultCard = selectSpecialcard.executeQuery();

            while (resultCard.next()) {
                int sId = resultCard.getInt(1);
                String sName = resultCard.getString(2);
                String sDescription = resultCard.getString(3);

                List<Effect> effects_list = new ArrayList<>();
                selectSpecialcardEffect = c.prepareStatement("SELECT e.eid, e.description, e.effect_type, e.effect_number "
                        + "FROM Effecte AS e, Specialcard_Effect AS ce, Specialcard "
                        + "WHERE sid = ?"
                        + "AND e.eid = ce.eid "
                        + "AND scid = sid");
                selectSpecialcardEffect.setInt(1, sId);
                resultEffect = selectSpecialcardEffect.executeQuery();
                while (resultEffect.next()) {
                    int eId = resultEffect.getInt(1);
                    String eDescription = resultEffect.getString(2);
                    String eEffectType = resultEffect.getString(3);
                    short eEffect_number = resultEffect.getShort(4);
                  //  int affectedShield = resultEffect.getInt(5);

                    effects_list.add(new Effect(eId, eDescription, stringToEffectType(eEffectType), eEffect_number, -1));
                }
                deck.add(new SpecialCard(sId, sName, sDescription, stringToType(resultCard.getString(4)), null, effects_list));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return deck;
    }
	
	/**Gibt das Deck als List zurueck.
	 * 
	 * @param deckName Name des Decks.
	 * @return Eine Liste aller Karten des Decks.
	 * @throws SQLException 
	 */
	public List<Card> getDeck(String deckName)  {
		
        PreparedStatement selectGamecard, selectCard_Effect, selectCard_EvoEffect, selectSpecialcard, selectSpecialcardEffect;
        ResultSet resultCard, resultEffect, resultEvoEffect;
        Effect[] effects = null;
        Effect[] evoEffects = null;
        //deckName = deckName.toLowerCase();
        List<Card> deck = new ArrayList<>();
        try {
			c = DriverManager.getConnection(JDBC_URL);
//			c2 = DbConnection.getPostgresConnection();
		
		

            selectGamecard = c.prepareStatement("SELECT g.gid, g.name, g.description, monster_type, atk, shield_curr, "
                    + "shield_max, evo_shield_curr, evo_shield_max, evo, g.image "
                    + "FROM Gamecard AS g , Deck_Cards AS dc, Deck AS d "
                    + "WHERE g.gid = dc.gid "
                    + "AND d.did = dc.did "
                    + "AND d.name = ? "
//                    + "AND LOWER(d.name = LOWER(?)" 
                    + "AND g.gid NOT IN (SELECT evo FROM Gamecard WHERE evo IS NOT NULL)");
            selectGamecard.setString(1, deckName);
            resultCard = selectGamecard.executeQuery();
            

            while (resultCard.next()) {
                int gId = resultCard.getInt(1);
                String gName = resultCard.getString(2);
                String gDescription = resultCard.getString(3);
                String gType = resultCard.getString(4);
                int gAtk = resultCard.getInt(5);
                short gShield_curr = resultCard.getShort(6);
                short gShield_max = resultCard.getShort(7);
                short gEvo_shield_curr = resultCard.getShort(8);
                short gEvo_shield_max = resultCard.getShort(9);
                int gEvo = resultCard.getInt(10);
                Blob blob = resultCard.getBlob(11);
                int blobLength = (int)blob.length();
                byte[] imageGamecard = blob.getBytes(1, blobLength);
                

                selectCard_Effect = c.prepareStatement("select e.eid, e.description, effect_type, effect_number, shield "
                        + "from Effecte e, Card_Effect c_e, Gamecard g "
                        + "where e.eid = c_e.eid "
                        + "and c_e.gid = g.gid "
                        + "and shield is not null "
                        + "and g.gid = ?");
                selectCard_Effect.setInt(1, gId);
                resultEffect = selectCard_Effect.executeQuery();

                selectCard_EvoEffect = c.prepareStatement("select e.eid, e.description, effect_type, effect_number, c_e.evo_shield "
                        + "from Effecte e, Card_Effect c_e, Gamecard g "
                        + "where e.eid = c_e.eid "
                        + "and c_e.gid = g.gid "
                        + "and evo_shield is not null "
                        + "and g.gid = ?");
                selectCard_EvoEffect.setInt(1, gId);
                resultEvoEffect = selectCard_EvoEffect.executeQuery();

                effects = getEffects(gShield_max, resultEffect);
                evoEffects = getEvoEffects(gEvo_shield_max, resultEvoEffect);

                GameCard evo = null;
                if (gEvo != 0) {
                    evo = integerToGamecard(gEvo);
                }

                deck.add(new GameCard(gId, gName, gDescription,
                        stringToType(gType), imageGamecard, gAtk, new Shield(gEvo_shield_curr,
                        gEvo_shield_max), new Shield(gShield_curr, gShield_max), evo, effects.clone(), evoEffects.clone()));
            }

            //Ab hier kommen die Specialcards:
            selectSpecialcard = c.prepareStatement("select sc.sid, sc.name, description, type, image "
                    + "from Specialcard sc, Deck_Cards dc, Deck d "
                    + " where LOWER(d.name) = LOWER(?)"
                    + " and sc.sid = dc.sid"
                    + " and dc.did = d.did"
                    + " order by sc.sid");
            selectSpecialcard.setString(1, deckName);
            resultCard = selectSpecialcard.executeQuery();

            while (resultCard.next()) {
                int sId = resultCard.getInt(1);
                String sName = resultCard.getString(2);
                String sDescription = resultCard.getString(3);
                Blob blob = resultCard.getBlob(5);
                int blobLength = (int)blob.length();
                byte[] imageSpecialCard = blob.getBytes(1, blobLength);

                List<Effect> effects_list = new ArrayList<>();
                //select e.eid, e.description, e.effect_type, e.effect_number, ce.shield "
                selectSpecialcardEffect = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number "
                        + "from Effecte e, Specialcard_Effect ce, Specialcard "
                        + "where sid = ?"
                        + "and e.eid = ce.eid "
                        + "and scid = sid");
                selectSpecialcardEffect.setInt(1, sId);
                resultEffect = selectSpecialcardEffect.executeQuery();
                while (resultEffect.next()) {
                    int eId = resultEffect.getInt(1);
                    String eDescription = resultEffect.getString(2);
                    String eEffectType = resultEffect.getString(3);
                    short eEffect_number = resultEffect.getShort(4);
                   // int affectedShield = resultEffect.getInt(5);
                    // effects_list.add(new Effect(eId, eDescription, stringToEffectType(eEffectType), eEffect_number, affectedShield));
                    effects_list.add(new Effect(eId, eDescription, stringToEffectType(eEffectType), eEffect_number, -1));
                }
                deck.add(new SpecialCard(sId, sName, sDescription, stringToType(resultCard.getString(4)), imageSpecialCard, effects_list));
            }

           
    		} catch (SQLException e) {
    			Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
    		}finally {
                try {
                    c.close();
                   // c2.close();
                } 
                catch (SQLException ex) {
                    Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        return deck;
    }
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	private EffectType stringToEffectType(String string) {
        EffectType effect = EffectType.valueOf(string);
        if(effect == null) throw new IllegalArgumentException("Zu dem String gibt es kein Effecttype");
        return effect;
    }
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	private Type stringToType(String string) {
        Type type = Type.valueOf(string.toLowerCase());
         if(type == null) throw new IllegalArgumentException("Kein existenter Type(Enum)");
        
        return type;
    }
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	 private GameCard integerToGamecard(int id) {
	        GameCard result = null;
	        ResultSet resultCard, resultEffect, resultEvoEffect;
	        PreparedStatement selectGamecard;
	        Effect[] effects = null;
	        Effect[] evoEffects = null;

	        try {
	            //Ausfuehren des Selects um alle notwendigen Infos aus Gamecard zu beziehen.
	            selectGamecard = c.prepareStatement("Select g.gid, g.name, g.description, g.monster_type, g.atk, g.shield_curr," + " "
	                    + "g.shield_max, g.evo_shield_curr, g.evo_shield_max, g.image from Gamecard g where g.gid = ?");
	            selectGamecard.setInt(1, id);
	            resultCard = selectGamecard.executeQuery();

	            resultCard.next();
	            int gId = resultCard.getInt(1);
	            String gName = resultCard.getString(2);
	            String gDescription = resultCard.getString(3);
	            String gType = resultCard.getString(4);
	            int gAtk = resultCard.getInt(5);
	            short gShield_curr = resultCard.getShort(6);
	            short gShield_max = resultCard.getShort(7);
	            short gEvo_Shield_curr = resultCard.getShort(8);
	            short gEvo_Shield_max = resultCard.getShort(9);
	            Blob blob = resultCard.getBlob(10);
	            int blobLength = (int)blob.length();
	            byte[] imageGamecard = blob.getBytes(1, blobLength);

	            // Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
	            // in der Hilfsmethode StringToType dem zugehoerigem Enum zugeordnet.
	            Type type = null;
	            type = stringToType(gType);

	            PreparedStatement selectCard_Effect = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield" + ""
	                    + " from Effecte e, Card_Effect c_e, Gamecard g "
	                    + "where g.gid = c_e.gid "
	                    + "and c_e.eid = e.eid "
	                    + "and c_e.shield is not null "
	                    + "and g.gid = "
	                    + "?");
	            selectCard_Effect.setInt(1, gId);
	            resultEffect = selectCard_Effect.executeQuery();

	            PreparedStatement selectCard_EvoEffect = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.evo_shield" + ""
	                    + " from Effecte e, Card_Effect c_e, Gamecard g "
	                    + "where g.gid = c_e.gid "
	                    + "and c_e.eid = e.eid "
	                    + "and c_e.evo_shield is not null "
	                    + "and g.gid = "
	                    + "?");
	            selectCard_EvoEffect.setInt(1, gId);
	            resultEvoEffect = selectCard_EvoEffect.executeQuery();

	            effects = getEffects(gShield_max, resultEffect);
	            evoEffects = getEvoEffects(gEvo_Shield_max, resultEvoEffect);

	            result = new GameCard(gId, gName, gDescription, type, imageGamecard, gAtk,
	                    new Shield(gEvo_Shield_curr, gEvo_Shield_max), new Shield(gShield_curr, gShield_max),
	                    null, effects.clone(), evoEffects.clone());

	        } catch (SQLException ex) {
	            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
	        } 

	        return result;

	    }
	 
	 /**
	  * 
	  * @param shield_max
	  * @param resultEffect
	  * @return
	  */
	 private Effect[] getEffects(int shield_max, ResultSet resultEffect) {
	        EffectType effect = null;
	        Effect[] effects = null;
	        try {					//shield_max -1
	            effects = new Effect[shield_max - 1];
	            while (resultEffect.next()) {	//e.eid, e.description, e.effect_type, e.effect_number, c_e.shield
	                int eId = resultEffect.getInt(1);
	                String eDescription = resultEffect.getString(2);
	                String eEffectType = resultEffect.getString(3);
	                int eEffect_number = resultEffect.getInt(4);
	                short eShield = resultEffect.getShort(5);
	                //Oberstes Schild hat keinen Effekte:
	                if (shield_max == 1) {
	                    throw new IllegalArgumentException("Zu wenig Schilder fuer Effekte");
	                }

	                effect = stringToEffectType(eEffectType);
	                if (eShield != -1) {
	                    effects[eShield] = new Effect(eId, eDescription, effect, eEffect_number, eShield);
	                } else {
	                    int i = 0;
	                    while (i < shield_max - 1) {
	                        effects[i] = new Effect(eId, eDescription, effect, eEffect_number, eShield);
	                        i++;
	                    }

	                }
	            }
	        } catch (SQLException ex) {
	            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        return effects;
	    }

	    /**
	     * Liefert eine Array auf EvoEffekten zurueck. Dieses Array laesst sich dann
	     * einer Gamecard zuordnen.
	     *
	     * @param resultEvoEffect Resultset mit ausgelesen Infos auf DB hinsichlich
	     * der Evo_Effekte.
	     * @return ein Array aus Effekten.
	     */
	    private Effect[] getEvoEffects(int evoShieldMax, ResultSet resultEvoEffect) {
	        EffectType effect = null;
	        Effect[] effects = null;
	        try {					//shield_max -1
	            effects = new Effect[evoShieldMax];
	            while (resultEvoEffect.next()) {			//effectType  e.description, e.effect_type, e.effect_number, c_e.evo_shield
	                int eId = resultEvoEffect.getInt(1);
	                String eDescription = resultEvoEffect.getString(2);
	                String eType = resultEvoEffect.getString(3);
	                int eEffect_number = resultEvoEffect.getInt(4);
	                short eEvo_Shield = resultEvoEffect.getShort(5);

	                effect = stringToEffectType(eType);
	                //evo_shield
	                if (eEvo_Shield != -1) {
	                	try{
	                    effects[eEvo_Shield - 1] = new Effect(eId, eDescription, effect, eEffect_number, eEvo_Shield);
	                	}catch(ArrayIndexOutOfBoundsException ex){
	                			System.out.println(effects.length);
	                			System.out.println(eEvo_Shield -1);
	                			throw ex;
	                			}
	                } else {
	                    int i = 0;
	                    while (i < evoShieldMax) {
	                        effects[i] = new Effect(eId, eDescription, effect, eEffect_number, eEvo_Shield);
	                        i++;
	                    }

	                }
	            }
	        } catch (SQLException ex) {
	            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        return effects;
	    }
	    
	    
	    /**Loescht alle Decks.
	     * 
	     * @return Wenn alle Decks 0 Zeilen enthalten, dann true, sonst false.
	     * @throws SQLException
	     */
	    public void deleteAll(){
	    	try {
				c = DriverManager.getConnection(JDBC_URL);
				c.prepareStatement("DROP TABLE CARD_EFFECT").executeUpdate();
				c.prepareStatement("DROP TABLE SPECIALCARD_EFFECT").executeUpdate();
				c.prepareStatement("DROP TABLE DECK_CARDS").executeUpdate();
				c.prepareStatement("DROP TABLE GAMECARD").executeUpdate();
				c.prepareStatement("DROP TABLE SPECIALCARD").executeUpdate();
				c.prepareStatement("DROP TABLE DECK").executeUpdate();
				c.prepareStatement("DROP TABLE EFFECTE").executeUpdate();
			
			} catch (SQLException e) {
				Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
			} finally {
	            try {
	                c.close();

	            } catch (SQLException ex) {
	                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
	            }
	        }
			
		}
		
	    /**
	     * 
	     * @param table
	     * @return
	     * @throws SQLException
	     */
		public void deleteTable(String table){
			try {
				c = DriverManager.getConnection(JDBC_URL);
				Objects.requireNonNull(table);
				c.prepareStatement("DROP TABLE "+table).executeUpdate();
			} catch (SQLException e) {
				Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, e);
			} finally {
	            try {
	                c.close();
	            } catch (SQLException ex) {
	                Logger.getLogger(EmbeddedDB.class.getName()).log(Level.SEVERE, null, ex);
	            }
	        }
		}
}
