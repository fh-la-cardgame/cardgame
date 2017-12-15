package cardgame.logic;

import cardgame.ai.KiPlayer;
import cardgame.ai.RandomPlayer;
import cardgame.ai.TestPlayer;
import cardgame.ai.TestPlayerProtokoll;
import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.GameEndException;
import cardgame.classes.Player;
import cardgame.db.DbCard;

import java.util.List;

public class ConsolTest {

	public static void main(String[] args) throws LogicException{


		DbCard db = new DbCard();
		List<Card> c1 = db.getDeck("Flora");
		List<Card> c2 = db.getDeck("civitas diaboli");

		Deck d1 = new Deck(1,"Flora",c1);
		Deck d2 = new Deck(2, "David", c2);

		for (int i = 0; i < 1 ; i++) {
			Game g = new Game(new Player(1, "Spieler1"), new Player(2, "Spieler2"), new Deck(d1), new Deck(d2));
			KiPlayer p1 = new TestPlayer(g, 1);
			KiPlayer p2 = new TestPlayer(g, 2);


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


			System.out.println("Spieler " + g.getMyField(g.getPlayerWon()).getPlayer().getName() + " hat gewonnen");
		}
	}

}
