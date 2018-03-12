package cardgame.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cardgame.ai.KiPlayer;
import cardgame.ai.RandomPlayer;
import cardgame.ai.TestPlayer;
import cardgame.ai.TestPlayerProtokoll;
import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.GameEndException;
import cardgame.classes.Player;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

public class DistributionOfOdds {
	
	public static int AMOUNT = 100;
	
	public static void main(String...args) throws LogicException, SQLException, ClassNotFoundException{
		List<String> allDeckNames = new ArrayList<>();
		Connection c = DbConnection.getPostgresConnection();
		PreparedStatement pst = c.prepareStatement("Select name from \"Deck\"");
		ResultSet rs = pst.executeQuery();
		while(rs.next()){
			if(!rs.getString(1).contains("Test"))
			allDeckNames.add(rs.getString(1));
		}
		int[] result = new int[2];
		for(int i = 0; i < allDeckNames.size(); i++){
			for(int j = 0; j < allDeckNames.size(); j++){
				if(j != i){
					result = testOfDistribution(allDeckNames.get(i), allDeckNames.get(j));
					System.out.println(allDeckNames.get(i)+": "+result[0]/(AMOUNT/100)+" - "+allDeckNames.get(j)+": "+result[1]/(AMOUNT/100));
				}
			}
		}
		
		
		
	}
	
	private static int[] testOfDistribution(String deck1, String deck2) throws LogicException{
		DbCard db = new DbCard();
		List<Card> c1 = db.getDeckWithoutImage(deck1);
		List<Card> c2 = db.getDeckWithoutImage(deck2);
		Deck d1 = new Deck(1,"Test1",c1);
		Deck d2 = new Deck(2, "Test2", c2);
		
		int victory_d1 = 0;
		int victory_d2 = 0;
		for (int i = 0; i < AMOUNT ; i++) {
			Game g = new Game(new Player(1, "Spieler1"), new Player(2, "Spieler2"), new Deck(d1), new Deck(d2));
			KiPlayer p1 = new RandomPlayer(g, 1);
			KiPlayer p2 = new RandomPlayer(g, 2);
			

			while (g.isGameRunning()) {
				g.changePlayer(p1.getId());
				try {
					g.getMyField(p1.getId()).addCard();
				} catch (GameEndException e) {
					g.setPlayerWon(p2.getId());
					g.setGameEnd(true);
					break;
				}
				try {
					p1.yourTurn();
				} catch (LogicException e) {
					System.out.println(e);
				}
				if (!g.isGameRunning()) {
					break;
				}
				g.changePlayer(p2.getId());
				try {
					g.getMyField(p2.getId()).addCard();
				} catch (GameEndException e) {
					g.setPlayerWon(p1.getId());
					g.setGameEnd(true);
					break;
				}
				try {
					p2.yourTurn();
				} catch (LogicException e) {
					System.out.println(e);
				}
			}
			if(g.getPlayerWon() == p1.getId()){
				victory_d1++;
			}
			if(g.getPlayerWon() == p2.getId()){
				victory_d2++;
			}
		}
		
		return new int[]{victory_d1, victory_d2};
	}

	
	
	
}
