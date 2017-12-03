package cardgame.logic;

import cardgame.ai.KiPlayer;
import cardgame.ai.RandomPlayer;
import cardgame.ai.TestPlayer;
import cardgame.classes.Deck;
import cardgame.classes.GameEndException;
import cardgame.classes.Player;
import cardgame.db.DbCard;

public class ConsolTest {

	public static void main(String[] args) throws LogicException{


		DbCard db = new DbCard();
		Game g = new Game(new Player(1, "Spieler1"), new Player(2, "Spieler2"), new Deck(1,"Flora", db.getDeck("Flora")), new Deck(2, "David", db.getDeck("civitas diaboli")));
		KiPlayer p1 = new RandomPlayer(g, 1);
		KiPlayer p2 = new RandomPlayer(g, 2);
		
		
		while(g.isGameRunning()) {
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
			} catch (LogicException e ) {
				System.out.println(e);
			}
			if(!g.isGameRunning()) {
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
			} catch (LogicException e ) {
				System.out.println(e);
			}
		}

		System.out.println("Spieler " + g.getMyField(g.getPlayerWon()).getPlayer().getName() + " hat gewonnen");
		
	}

}
