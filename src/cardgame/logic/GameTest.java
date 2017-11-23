/**package cardgame.logic;
import cardgame.classes.*;
import cardgame.db.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
	//Override GameCard?
	@Test 
	public void DBTest_NoEvoInDeck(){
		List<GameCard> evolutions = new ArrayList<>();
		for(Card card: deck1.getCards()){
			if(card instanceof GameCard){
				if(((GameCard)card).getEvolution() != null)
				evolutions.add(((GameCard) card).getEvolution());
			}
		}
		Set<Card> deck_set = new HashSet<Card>(deck1.getCards());
		for(GameCard evo: evolutions){
			assertTrue(deck_set.add(evo));
		}
		
	}
	//Referenz!!
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
		System.out.println("Zu wenig GameCards");
	}
	
	//Merkwurdig: Es wird keine NullPointerException geworfen: RunTimeException?
	@Test
	public void playCardTest_playNull() {
		List<Card> list = game.getMyField(1).getCardsOnHand();
		
		int want = 5;
		assertEquals(want, list.size());
		
		int i=0; 
		while(i<list.size()){
			if(list.get(i) instanceof GameCard){
				thrown.expect(NullPointerException.class);
				game.playCard(1, null);
			}
			i++;
		}
	}
	//Fehlende Kommentare in Methode!
	@Test
	public void playCardTest_playSpecialCard() {
		List<Card> list = game.getMyField(1).getCardsOnHand();
		
		int want = 5;
		assertEquals(want, list.size());
		
		int i=0;
		SpecialCard[] want_battlegroundSpecials = new SpecialCard[ROW];
		SpecialCard[] have_battlegroundSpecials = new SpecialCard[ROW];
		while(i<list.size()){
			if(list.get(i) instanceof SpecialCard){
				SpecialCard played = (SpecialCard) list.get(i);
				want_battlegroundSpecials[0] = played; 
				game.playCard(1, played);
			}
			i++;
		}
		have_battlegroundSpecials = game.getMyField(1).getBattlegroundSpecials();
		assertTrue(new HashSet( Arrays.asList( want_battlegroundSpecials )).equals( new HashSet( Arrays.asList( game.getMyField(1).getBattlegroundSpecials() ) )) && want_battlegroundSpecials.length == have_battlegroundSpecials.length);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void playCardTest_playCardYouDidntHave(){
		GameCard notOwnedCard = null;
		int i = 0;
		while(notOwnedCard  == null && i  < deck2.getCards().size()){
			if(deck2.getCards().get(i) instanceof GameCard)
				notOwnedCard = (GameCard)deck2.getCards().get(i);
			i++;
		}
		
		game.playCard(1, notOwnedCard);
		
	}
	//Fuer Angriff in nicht erster Runde:
	@Test
	public void attackTest_standard(){
		GameCard[] want_battlegroundMonster = new GameCard[ROW];
		int want_shields = 0;
		int i = 0;
		while(i < game.getCardsOnHand(2).size()){
			if(game.getCardsOnHand(2).get(i) instanceof GameCard){
				want_shields = ((GameCard)game.getCardsOnHand(2).get(i)).getShields().getCurrentShields(); //NICHT EXISTENT 
				want_battlegroundMonster[0] = Objects.requireNonNull((GameCard) game.getCardsOnHand(2).get(i));
				break;
			}		
			i++;
		}
		int k = 0;
		while(k < game.getCardsOnHand(1).size() && !(game.getCardsOnHand(1).get(k) instanceof GameCard)){
			k++;
		}
		game.getEnemyField(1).addMonsterCard((GameCard)game.getCardsOnHand(2).get(i));
		game.getMyField(1).addMonsterCard(Objects.requireNonNull((GameCard) game.getCardsOnHand(1).get(k)));
		game.attack(1, 0, 0);
		want_shields = ((GameCard)game.getCardsOnHand(2).get(i)).getAtk() < ((GameCard)game.getCardsOnHand(1).get(k)).getAtk() ? want_shields-1:want_shields;
		assertTrue(want_shields == ((GameCard)deck2.getCards().get(i)).getShields().getCurrentShields());
	}
	
	@Test(expected = RuntimeException.class)
	public void attackTest_firstRound(){
		GameCard[] want_battlegroundMonster = new GameCard[ROW];
		int i = 0;
		while(i < game.getCardsOnHand(2).size()){
			if(game.getCardsOnHand(2).get(i) instanceof GameCard){ 
				want_battlegroundMonster[0] = Objects.requireNonNull((GameCard) game.getCardsOnHand(2).get(i));
				break;
			}		
			i++;
		}
		int k = 0;
		while(k < game.getCardsOnHand(1).size() && !(game.getCardsOnHand(1).get(k) instanceof GameCard)){
			k++;
		}
		game.getEnemyField(1).addMonsterCard((GameCard)game.getCardsOnHand(2).get(i));
		game.getMyField(1).addMonsterCard(Objects.requireNonNull((GameCard) game.getCardsOnHand(1).get(k)));
		game.attack(1, 0, 0);
	}
	
	@Test(expected = RuntimeException.class)
	public void attackTest_multipleAttackSameCard(){
		GameCard[] want_battlegroundMonster = new GameCard[ROW];
		int i = 0;
		while(i < deck2.getCards().size()){
			if(deck2.getCards().get(i) instanceof GameCard){
				want_battlegroundMonster[0] = (GameCard) deck2.getCards().get(i);
			}		
			i++;
		}
		int k = 0;
		while(k < deck2.getCards().size()){
			if(deck2.getCards().get(k) instanceof GameCard){
				want_battlegroundMonster[0] = (GameCard) deck2.getCards().get(k);
			}
			k++;
		}
		game.getEnemyField(2).addMonsterCard((GameCard)deck2.getCards().get(i));
		game.getMyField(1).addMonsterCard((GameCard) deck1.getCards().get(k));
		game.attack(1, 0, 0);
		game.attack(1, 0, 0);
		
	}
	
	
	
	
	
	
	

}
**/