package cardgame.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Test;

import cardgame.db.DbCard;
import cardgame.db.DbConnection;

public class DBTest {
	
	public static int MAX_CARDS_IN_DECK = 25;
	
	/**Testet die ATK Werte.
	 * Dabei gilt: 0 <= ATK <= 3000.
	 * 
	 */
	@Test
	public void testATK(){
		try {
			Connection c = DbConnection.getPostgresConnection();
			
			PreparedStatement pst = c.prepareStatement("select count(gid) from \"Gamecard\""+
					"where atk < 0 or atk > 3000");
			ResultSet rs = pst.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt(1));
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEvoConsistence(){
		try {
			Connection c = DbConnection.getPostgresConnection();
			
			PreparedStatement pst = c.prepareStatement("select count(gid) from \"Gamecard\""+
					"where evo_shield_max < 10"+
					"and evo is null");
			ResultSet rs = pst.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt(1));
			
			pst = c.prepareStatement("select count(gid) from \"Gamecard\""+
					"where evo_shield_max = 10"+
					"and evo is not null");
			rs = pst.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt(1));
			
			
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**Prueft Konsistenz von Card_Effekt.
	 * Einmal: Weder beide Schilder sind null, noch das in 2 Schildern gleichzeitig etwas drinsteht.
	 */
	@Test
	public void testCard_Effect(){
		try {
			Connection c = DbConnection.getPostgresConnection();
			
			PreparedStatement pst = c.prepareStatement("select count(*) from \"Card_Effect\" "
					+"where (shield is null and evo_shield is null)"
					+"or (shield is not null and evo_shield is not null)");
			ResultSet rs = pst.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt(1));	
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**Testen die korrekte Anzahl an Karten in einem Deck.
	 * Vorgesehen: 25
	 * Zudem ob die Werte in den Spalten "sid" & "gid" richtig eingetragen worden sind.
	 */
	@Test
	public void test_Deck_Cards(){
		try {
			Connection c = DbConnection.getPostgresConnection();
			
			PreparedStatement pst = c.prepareStatement("select did, count(did) from \"Deck_Cards\" group by did");
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				assertEquals("DeckNummer: "+String.valueOf(rs.getInt(1)), MAX_CARDS_IN_DECK, rs.getInt(2));
			}
			
			pst = c.prepareStatement("select count(*) from \"Card_Effect\" "
					+"where (shield is null and evo_shield is null)"
					+"or (shield is not null and evo_shield is not null)");
			rs = pst.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt(1));
				
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void test_Effecte(){
		try {
			Connection c = DbConnection.getPostgresConnection();
			
			PreparedStatement pst = c.prepareStatement("select eid, effect_type, effect_number from \"Effecte\"");
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString(2).contains("addition")){
					assertTrue("Effekte_ID: "+rs.getInt(1), rs.getInt(3) > 0);
				}else if(rs.getString(2).contains("subtraction") || rs.getString(2).contains("substraction")){
					assertTrue("Effekte_ID: "+rs.getInt(1), rs.getInt(3) < 0);
				}else if(rs.getString(2).contains("destroy")){
					assertTrue("Effekte_ID: "+rs.getInt(1), (rs.getInt(3) == 1 || rs.getInt(3) == -1));
				}else{
					fail("Effekt nicht erkannt");
				}
			}
			
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	

}
