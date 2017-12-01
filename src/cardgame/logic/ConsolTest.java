package cardgame.logic;

import cardgame.ai.KiPlayer;
import cardgame.ai.TestPlayer;
import cardgame.classes.Deck;
import cardgame.classes.Player;
import cardgame.db.DbCard;

public class ConsolTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		KiPlayer p1 = new TestPlayer();
		KiPlayer p2 = new TestPlayer();
		DbCard db = new DbCard();
		Game g = new Game(new Player(1, "Spieler1"), new Player(2, "Spieler2"), new Deck(1,"Flora", db.getDeck("Flora")), new Deck(2, "David", db.getDeck("civitas diaboli")));
		
		
		
		
		
		while(g.isGameRunning()) {
			g.getMyField(p1.getId()).addCard();
			p1.yourTurn();
			if(!g.isGameRunning()) {
				break;
			}
			g.getMyField(p2.getId()).addCard();
			p2.yourTurn();
		}
		
	}

}
