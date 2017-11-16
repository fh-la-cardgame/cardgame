package cardgame.logic;
import cardgame.classes.*;
import cardgame.db.*;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GameTest {
	private Game game;
	private Player p1;
	private Player p2;
	private DbCard DB;
	private Deck deck1;
	private Deck deck2;
	private static int ROW = 4;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initializeGame(){
		p1 = new Player(1, "Tester1");
		p2 = new Player(2, "Tester2");
		DB = new DbCard();
		deck1 = new Deck(1, "Flora", DB.getDeck("Flora"));
		deck2 = new Deck(1, "Flora", DB.getDeck("civitas diaboli"));
		
		game = new Game(p1, p2, deck1, deck2);
	}
	@Test
	public void playCardTest_standard() {
		List<Card> list = game.getMyField(1).getCardsOnHand();
		int want = 5;
		assertEquals(want, list.size());
		int i=0;
		GameCard[] want_battlegroundMonster = new GameCard[ROW];
		while(i<list.size()){
			if(list.get(i) instanceof GameCard){
				GameCard played =(GameCard) list.get(i);
				game.playCard(1, played);
				want_battlegroundMonster[0] = played;
				break;
			}
			i++;
		}
		GameCard[] have_battlegroundMonster = game.getMyField(1).getBattlegroundMonster();
		assertTrue(new HashSet( Arrays.asList( want_battlegroundMonster )).equals( new HashSet( Arrays.asList( have_battlegroundMonster ) )) && want_battlegroundMonster.length == have_battlegroundMonster.length);
		want = 4;
		assertEquals(want, list.size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void playCardTest_play2Cards() {
		List<Card> list = game.getMyField(1).getCardsOnHand();
		
		int want = 5;
		assertEquals(want, list.size());
		
		int i=0;
		int times_played = 0;
		while(i<list.size() && times_played<2){
			if(list.get(i) instanceof GameCard){
				GameCard played = (GameCard) list.get(i);
				game.playCard(1, played);
				times_played++;
			}
			i++;
		}
	}
	
//	@Test
//	public void playCardTest_playNull() {
//		List<Card> list = game.getMyField(1).getCardsOnHand();
//		
//		int want = 5;
//		assertEquals(want, list.size());
//		
//		int i=0; 
//		while(i<list.size()){
//			if(list.get(i) instanceof GameCard){
//				thrown.expect(NullPointerException.class);
//				game.playCard(1, null);
//			}
//			i++;
//		}
//	}
//	
//	@Test
//	public void playCardTest_playSpecialCard() {
//		List<Card> list = game.getMyField(1).getCardsOnHand();
//		
//		int want = 5;
//		assertEquals(want, list.size());
//		
//		int i=0;
//		SpecialCard[] want_battlegroundSpecials = new SpecialCard[ROW];
//		SpecialCard[] have_battlegroundSpecials = new SpecialCard[ROW];
//		while(i<list.size()){
//			if(list.get(i) instanceof SpecialCard){
//				SpecialCard played = (SpecialCard) list.get(i);
//				want_battlegroundSpecials[0] = played; 
//				game.playCard(1, played);
//			}
//			i++;
//		}
//		have_battlegroundSpecials = game.getMyField(1).getBattlegroundSpecials();
//		assertTrue(new HashSet( Arrays.asList( want_battlegroundSpecials )).equals( new HashSet( Arrays.asList( have_battlegroundSpecials ) )) && want_battlegroundSpecials.length == have_battlegroundSpecials.length);
//	}
//	
	
	

}
