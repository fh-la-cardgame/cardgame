package cardgame.logic;

import cardgame.ai.KiPlayer;
import cardgame.ai.MCTSPlayer;
import cardgame.ai.RandomPlayer;
import cardgame.ai.TestPlayer;
import cardgame.ai.TestPlayerProtokoll;
import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.GameEndException;
import cardgame.classes.Player;
import cardgame.db.DbCard;
import cardgame.db.EmbeddedDB;

import java.sql.SQLException;
import java.util.List;

public class ConsolTest {

	public static void main(String[] args) throws LogicException, ClassNotFoundException, SQLException{


		EmbeddedDB db = new EmbeddedDB();
		List<Card> c1 = db.getDeck("Flora");
		List<Card> c2 = db.getDeck("Flora");

		Deck d1 = new Deck(1,"Alpha",c1);
		Deck d2 = new Deck(2, "Beta", c2);

		for (int i = 0; i < 1 ; i++) {
			Game g = new Game(new Player(1, "Spieler1"), new Player(2, "Spieler2"), new Deck(d1), new Deck(d2), true);
			KiPlayer p1 = new TestPlayerProtokoll(g, 1);
			KiPlayer p2 = new TestPlayerProtokoll(g, 2);
			
			if(KiPlayer.TIME_DELAY == 0 && KiPlayer.DELAY)
				System.out.println("Spiel muss durch Eingaben fortgesetzt werden(delays)");
			else if(KiPlayer.DELAY)
				System.out.println("Spiel enthaelt an jeweiligen Stellen eine Verzoegerung um: "+ p1.TIME_DELAY);
			
			if(KiPlayer.TIME_DELAY == 0 && KiPlayer.DELAY)
				System.out.println("Spiel muss durch Eingaben fortgesetzt werden(delays)");
			else if(KiPlayer.DELAY)
				System.out.println("Spiel enthaelt an jeweiligen Stellen eine Verzoegerung um: "+ p2.TIME_DELAY);
			
			
			int k = 0;
			while (g.isGameRunning()) {
				if (g.changePlayer(p1.getId())) {
					break;
				}
				try {
					p1.yourTurn();
					if(k == 0)
					System.out.println(g.getCardsOnHand(p1.getId()).size()+" "+g.getCardsOnHand(p2.getId()).size());
					
				} catch (LogicException e) {
					System.out.println(e);
					
				}
				if (!g.isGameRunning()) {
					break;
				}
				if(g.changePlayer(p2.getId())) {
					break;
				}
				try {
					p2.yourTurn();
				} catch (LogicException e) {
					System.out.println(e);
				}
				k++;
			}


			System.out.println("Spieler " + g.getMyField(g.getPlayerWon()).getPlayer().getName() + " hat gewonnen");
		}
	}

}
