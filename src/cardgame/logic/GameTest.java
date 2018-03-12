package cardgame.logic;
  

import cardgame.classes.*;
import cardgame.db.*;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GameTest {
	
	
	
	
//		@Test
//		public void KartenAnsicht(){
//			for(int i = 0; i < 3; i++){
//				Card swap = c2.get(c2.size()-(i+1));
//				c2.set(c2.size()-(i+1), c2.get(i));
//				c2.set(i, swap);
//			}
//			System.out.println(c2);
//		}	
		
	
		DbCard db = new DbCard();
		List<Card> c1 = db.getDeckWithoutImage("TestDeck1");
		List<Card> c2 = db.getDeckWithoutImage("TestDeck2");

		Deck d1 = new Deck(1,"TestDeck1",c1);
		Deck d2 = new Deck(2, "TestDeck2", c2);
		
		Player p1 = new Player(1, "Alpha");
		Player p2 = new Player(2, "Beta");
		
	/**Alles im Game-Ctor auf NULL setzen.
	 */
	@Test(expected = NullPointerException.class)
	public void testGameCtorAllNull(){
		new Game(null, null, null, null);
	}
	
	/**Einen Spieler im Game-Ctor auf NULL setzen.
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void testGameCtorPlayerNull(){
		new Game(new Player(1, "Spieler1"), null, new Deck(d1), new Deck(d2));
	}
	
	/**Ein Deck im Game-Ctor auf NULL setzen.
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void testGameCtorDeckNull(){
		new Game(new Player(1, "Spieler1"), new Player(2, "Spieler2"), new Deck(d1), null);
	}
	
	/** 2 Spieler haben die selbe ID.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGameSamePlayerId(){
		new Game(new Player(1, "Spieler1"), new Player(1, "Spieler2"), new Deck(d1), new Deck(d2));
	}
	
	/** 2 Spieler haben den selben Namen.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGameSamePlayerName(){
		new Game(new Player(1, "Spieler1"), new Player(2, "Spieler1"), new Deck(d1), new Deck(d2));
	}
	
	/**Standard Test des Game-Ctor.
	 * Prueft die Initialisierung des Spiels, vorallem die Karten auf der Hand.
	 */
	@Test
	public void testGame_Standardinitialization(){
		Game game = new Game(p1, p2, d1, d2, true);
		
		List<Integer> have = new ArrayList<>();
		List<Integer> want = Arrays.asList(73, 73, 73, 74);
		int i = 0;
		try {
			while(i < game.getCardsOnHand(p1.getId()).size()){
				have.add(game.getCardsOnHand(p1.getId()).get(i).getId());
				i++;
			}
		} catch (LogicException e) {
			fail();
		}
		assertEquals(have, want);	
	}
	
	/**Standardmaessiges Spielen einer Karte.
	 * 
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test
	public void testPlayCardStandard() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		GameCard card = null;
		try {
			card = (GameCard)game.getCardsOnHand(1).get(2);
		} catch (LogicException e) {
			fail();
		}
		int gameCardId_want = 73;
		assertEquals(gameCardId_want, card.getId());
		game.playCard(p1.getId(), card);
		GameCard cardOnField = game.getMyField(1).getBattlegroundMonster()[0];
		int id_have = cardOnField.getId();
		assertEquals(73, id_have);
	}
	
	/**Spielen einer illegal Karte.
	 * Spielen einer Karte die sich nicht im eigenen Deck befindet.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPlayCard_GameCardNotInDeck() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		GameCard card = (GameCard)c2.get(0);
		try {
			game.playCard(p1.getId(), card);
		} catch (LogicException e) {
				fail();
		}
	}
	
	/**Spielen einer NULL-Karte.
	 * 
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = NullPointerException.class)
	public void testPlayCard_isNull() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		GameCard card = null;
		try {
			game.playCard(1, card);
		} catch (LogicException e) {
				fail();
		}
	}
	/**Spielen einer illegalen Karte.
	 * Spielen einer SpecialCard, die man nicht auf der Hand hat.
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPlayCard_SpecialCardInsteadOfGameCard() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		SpecialCard specialcard = null;
		for(Card card : c1){
			if(card instanceof SpecialCard){
				specialcard = (SpecialCard)card;
				break;
			}
		}
		game.playCard(1, specialcard);
	}
	
	/**Spielen mehrerer GameCards.
	 * Nicht erlaubt: Es darf nur eine GameCard pro Zug gespielt werden.
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = LogicException.class)
	public void testPlayCard_PlayingSeveralGameCards() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		GameCard cardFirst = null;
		GameCard cardSecond = null;
		try {
			cardFirst = (GameCard)game.getCardsOnHand(1).get(2);
			cardSecond = (GameCard)game.getCardsOnHand(1).get(3);
		} catch (LogicException e) {
			System.out.println("Fehler: PlayingSeveralCards");
		}
		game.playCard(p1.getId(), Objects.requireNonNull(cardFirst));
		game.playCard(p1.getId(), Objects.requireNonNull(cardSecond));
	}
	
	/**Falsche ID beim Spielen einer Karte angeben.
	 * 
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = LogicException.class)
	public void testPlayCard_WrongPlayer() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		GameCard card = null;
		card = (GameCard)game.getCardsOnHand(p1.getId()).get(2);
		game.playCard(p2.getId(), card);
	}
	
	/**Erst angreifen, dann spielen einer Karte.
	 * Nicht erlaubt: Man kann nach einem Angriff keine Karte mehr spielen. 
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = LogicException.class)
	public void testPlayCard_FirstAttackThanPlayCard() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		GameCard card = null;
		try {
			card = (GameCard)game.getCardsOnHand(p1.getId()).get(2);
		} catch (LogicException e) {
			System.out.println("Fehler");
		}
		game.playCard(p1.getId(), card);
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		try {
			card = (GameCard)game.getCardsOnHand(p2.getId()).get(2);
		} catch (LogicException e) {
			System.out.println("Fehler");
		}
		game.playCard(p2.getId(), card);
		
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.attack(p1.getId(), game.getMyField(p1.getId()).getBattlegroundMonster()[0], game.getEnemyField(p1.getId()).getBattlegroundMonster()[0]);
		try {
			card = (GameCard)game.getCardsOnHand(p1.getId()).get(2);
		} catch (LogicException e) {
			System.out.println("Fehler");
		}
		game.playCard(p1.getId(), card);
	}
	
	/** Battleground fuer GameCards ist voll.
	 *  Auf einen vollbelegten Battleground wird eine weitere Karte versucht zu legen.
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = RuntimeException.class)
	public void testPlayCard_FieldIsFull() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		GameCard card = null;
		for(int i = 0; i < 5; i++){
			
			game.changePlayer(p1.getId());
			game.getMyField(p1.getId()).addCard();
			try {
				card = (GameCard)game.getCardsOnHand(p1.getId()).get(2);
			} catch (LogicException e) {
				System.out.println("Fehler");
			}
			game.playCard(p1.getId(), card);
			
			game.changePlayer(p2.getId());
			game.getMyField(p2.getId()).addCard();
			try {
				card = (GameCard)game.getCardsOnHand(p2.getId()).get(2);
			} catch (LogicException e) {
				System.out.println("Fehler");
			}
			game.playCard(p2.getId(), card);
			
		}
	}
	
	
	
	/**Spielen einer illegalen SpecialCard.
	 * Spielen einer SpecialCard, die man nicht auf der Hand hat.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPlaySpecialCard_CardNotInHand() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playSpecialCard(p2.getId(), (SpecialCard)c2.get(c2.size()-1), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
	}
	
	
	/** Angriff auf NULL mit SpecialCard.
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = NullPointerException.class)
	public void testPlaySpecialCard_EnemyNull() throws LogicException, GameEndException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Deck d3 = new Deck(2, "TestDeck3", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d3, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		// statt null alternativ auch: game.getEnemyField(p2.getId()).getBattlegroundMonster()[1]
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), null);
	}
	
	/**Spielen einer illegalen Karte.
	 * Spielen einer SpecialCard, die sich nicht im eigenen Deck befindet.
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPlaySpecialCard_SpecialCardNotInDeck() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(1);
		game.getMyField(p1.getId()).addCard();
		int i = 0;
		SpecialCard card = null;
		while(i < c2.size()){
			if(c2.get(i) instanceof SpecialCard){
				card = (SpecialCard)c2.get(i);
			}
			i++;
		}
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		i = 0;
		GameCard enemyCard = null;
		while(i < game.getCardsOnHand(p2.getId()).size()){
			if(game.getCardsOnHand(p2.getId()).get(i) instanceof GameCard){
				enemyCard = (GameCard)game.getCardsOnHand(p2.getId()).get(i);
				break;
			}
			i++;
		}
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		try {
			game.playSpecialCard(p1.getId(), card, enemyCard);
		} catch (LogicException e) {
				fail();
		}
	}
	
	/**SpecialCard ist NULL.
	 * Spielen einer NULL-SpecialCard.
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = NullPointerException.class)
	public void testPlaySpecialCard_SpecialCardIsNull() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(2);
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.playSpecialCard(p2.getId(), null, game.getEnemyField(p1.getId()).getBattlegroundMonster()[0]);
	}
	
	/**Erst angreifen, dann SpecialCard spielen.
	 * Nicht erlaubt: Nach einem Angriff darf keine Karte mehr aufs Feld gelegt werden.
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = LogicException.class)
	public void testPlaySpecialCard_FirstAttackThanPlaySpecialCard() throws LogicException, GameEndException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Deck d3 = new Deck(2, "TestDeck3", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d3, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(3));
		game.attack(p2.getId(), game.getMyField(p2.getId()).getBattlegroundMonster()[0], game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
	}
	
	/**Meherere SpecialKarten hintereinander spielen.
	 * 
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test
	public void testPlaySpecialCard_PlayingSeveralSpecialCards() throws LogicException, GameEndException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Deck d3 = new Deck(2, "TestDeck3", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d3, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(3));
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		assertEquals(2, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(1700, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(1900, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(1, Stream.of(game.getMyField(p2.getId()).getBattlegroundSpecials()).filter(a -> a != null).count());
	}
	//TODO
	/**Es werden keine neuen Karten gezogen.
	 * Es sollen aber automatisch Neue nachgezogen werden, falls möglich(Karten auf der Hand < 5).
	 */
	@Test(expected = LogicException.class)
	public void test_NoDrawOfCard(){
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.changePlayer(p2.getId());
	}
	
	/**GameKarte anstatt SpecialKarte spielen.
	 * 
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test(expected = ClassCastException.class)
	public void testPlaySpecialCard_GameCardInsteadOfSpecialCard() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playSpecialCard(p1.getId(), (SpecialCard)game.getCardsOnHand(p1.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
	}
	/**Feld fuer SpecialCards ist voll.
	 * Trotzdem wird versucht eine weitere Speicalcard zu spielen.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = RuntimeException.class)
	public void testPlaySpecialCard_FieldIsFull() throws GameEndException, LogicException{
		for(int i = 0; i < 5; i++){
			Card swap = c1.get(c1.size()-(i+1));
			c1.set(c1.size()-(i+1), c1.get(i));
			c1.set(i, swap);
		}
		Game game = new Game(p1, p2, d2, new Deck(1, "TestDeck1", c1), true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		for(int i = 0; i < 5; i++){
			game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		}
	}
	
	//TODO
	//mMn. sinnlos
	@Test(expected = Exception.class)
	public void testPlaySpecialCard_FirstTurn() throws GameEndException, LogicException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Deck d2 = new Deck(1, "TestDeck1", c2);
		Game game = new Game(p1, p2, d2, new Deck(1, "TestDeck2", c1), true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playSpecialCard(p1.getId(), (SpecialCard)game.getCardsOnHand(p1.getId()).get(0), null);
		assertEquals(3, game.getEnemyField(p1.getId()).getPlayer().getShields().getCurrentShields());
//		assertEquals(1, Stream.of(game.getMyField(p1.getId()).getBattlegroundSpecials()).filter(a -> a != null).count());
	}
	/**Einer Karte ATK abziehen.
	 * Effekt: substraction_one
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test
	public void testPlaySpecialCard_WoundingCard() throws GameEndException, LogicException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Deck d2 = new Deck(2, "TestDeck2", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(3));
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(2), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		assertEquals(1100, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(1, Stream.of(game.getMyField(p2.getId()).getBattlegroundSpecials()).filter(a -> a != null).count());
	}
	
	/**
	 * 
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPlaySpecialCard_WrongPlayer() throws GameEndException, LogicException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Deck d2 = new Deck(2, "TestDeck2", c2);
		Game game = new Game(p1, p2, d2, new Deck(1, "TestDeck1", c1), true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		GameCard card = null;
		card = (GameCard)game.getCardsOnHand(p1.getId()).get(4);
		game.playCard(p1.getId(), card);
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p1.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		
	}
	/**Toeten einer Karte mittels SpecialKarte
	 * Effekte: meherere destroys.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test
	public void testPlaySpecialCard_KillingCard() throws GameEndException, LogicException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Deck d2 = new Deck(2, "TestDeck2", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(3));
		
		game.attack(p2.getId(), game.getMyField(p2.getId()).getBattlegroundMonster()[0], game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		assertEquals(1, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
		assertEquals(2, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(1400, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(0, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
		
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.attack(p2.getId(), game.getMyField(p2.getId()).getBattlegroundMonster()[0], game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(1), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		
		assertEquals(0, Stream.of(game.getEnemyField(p2.getId()).getBattlegroundMonster()).filter(a -> a != null).count());
		assertEquals(0, Stream.of(game.getMyField(p2.getId()).getBattlegroundMonster()).filter(a -> a != null).count());
		assertEquals(0, Stream.of(game.getMyField(p2.getId()).getBattlegroundSpecials()).filter(a -> a != null).count());
		
	}
	/**SpecialCard schwaecht eine GameCard.
	 * 
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test
	public void testPlaySpecialCard_WeakeningCard() throws GameEndException, LogicException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		
		Deck d2 = new Deck(2, "TestDeck2", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(3));
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(1), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
	
		assertEquals(1400, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(2, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(2, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(0, Stream.of(game.getMyField(p2.getId()).getBattlegroundSpecials()).filter(a -> a != null).count());
	}
	
	/** SpecialCard schwaecht eine GameCard.
	 * 
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test
	public void testPlaySpecialCard_WeakeningCard2() throws GameEndException, LogicException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
		Card swap = c1.get(9);
		c1.set(5, c1.get(0));
		c1.set(0, swap);
		Deck d2 = new Deck(2, "TestDeck2", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(3));
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(1), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		
		assertEquals(1800, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(1600, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(4, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(0, Stream.of(game.getMyField(p2.getId()).getBattlegroundSpecials()).filter(a -> a != null).count());
	}
	
	/**Spiel in Endlosschleife.
	 * Das Spiel koennte nach einer best. Anzahl an Zuegen terminieren.
	 */
	@Ignore
	@Test(timeout = 1500)
	public void testChangePlayer_endlessChangingWithoutPlaying(){
		Game game = new Game(p1, p2, d1, d2, true);
		while(true){
			game.changePlayer(p1.getId());
			game.changePlayer(p2.getId());	
		}
	}
	
	/**Den Spieler auf den selben Spieler wechseln.
	 * 
	 * @throws LogicException
	 */
	@Test(expected = Exception.class)
	public void testChangePlayer_changeWithSameID() throws LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p1.getId());
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
	}
	
	@Test
	/**Meherer Effekte die auf meherere Karten wirken.
	 * Effekte: addtion_deck, substraction_all
	 * @throws LogicException
	 * @throws GameEndException
	 */
	public void testPlaySpecialCard_EffectsOnSeveralCards() throws LogicException, GameEndException{
		for(int i = 0; i < 3; i++){
			Card swap = c1.get(c1.size()-(i+1));
			c1.set(c1.size()-(i+1), c1.get(i));
			c1.set(i, swap);
		}
		
		Deck d2 = new Deck(2, "TestDeck2", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		for(int i = 0; i < 2; i++){
			game.changePlayer(p1.getId());
			game.getMyField(p1.getId()).addCard();
			game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(3));
			
			game.changePlayer(p2.getId());
			game.getMyField(p2.getId()).addCard();
			game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		}
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		for(int i = 0; i < 2; i++){
			game.playSpecialCard(p1.getId(), (SpecialCard)game.getCardsOnHand(p1.getId()).get(i), game.getEnemyField(p1.getId()).getBattlegroundMonster()[0]);
		}
		assertEquals(1400, game.getMyField(p1.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(1400, game.getMyField(p1.getId()).getBattlegroundMonster()[1].getAtk());
		assertEquals(600, game.getEnemyField(p1.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(600, game.getEnemyField(p1.getId()).getBattlegroundMonster()[1].getAtk());
		assertEquals(2, Stream.of(game.getMyField(p1.getId()).getBattlegroundSpecials()).filter(a -> a != null).count());
	}
	
	/** Illegale Eingaben bei Attack.
	 * Eingabe des falschen Index.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = LogicException.class)
	public void testAttack1_wrongRows() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), 0, 1);
	}
	
	/**Illegale Eingabe bei Attack.
	 * Eingabe eines negativen Indizes.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testAttack1_wrongRows3() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), 0, -2);
	}
	
	/**Einen Spieler angreifen, der noch Karten auf dem Feld besitzt.
	 * @throws LogicException 
	 * @throws GameEndException 
	 * 
	 */
	@Test(expected = LogicException.class)
	public void testAttack1_attackingPlayer_Illegal() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), 0, -1);
	}
	
	/**Illegale Eingabe bei Attack.
	 * Eingabe des falschen Indizes fuer den eigenen Battleground.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = LogicException.class)
	public void testAttack1_wrongRows2() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), 1, 0);
	}
	
	/**Angriff auf einen Spieler.
	 * 
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test
	public void testAttack1_attackPlayer() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), 0, -1);
		assertEquals(2, p1.getShields().getCurrentShields());
	}
	
	/**Beim Angriff eine falsche ID benutzen.
	 * 
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = LogicException.class)
	public void testAttack1_wrongId() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p1.getId(), 0, 0);
	}
	
	/**Standard Angriff
	 * Effekte: Destroy, substraction_deck
	 * @throws LogicException
	 * @throws GameEndException
	 */
	@Test
	public void testAttack1_standard() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(1));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(4));
		game.attack(p2.getId(), 0, 0);
		
		assertEquals(2, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(1, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(1, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
		assertEquals(1400, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(1400, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
	}

	/**Angriff in der ersten Runde.
	 * Nicht erlaubt: Angriff sind fuer den Startspieler in der ersten Runde nicht erlaubt.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test(expected = LogicException.class)
	public void testAttack2_firstTurn() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.attack(p1.getId(), game.getMyField(p1.getId()).getBattlegroundMonster()[0], null);
	}
	
	/**Ein zweiter Angriff.
	 * Dieses Mal greift der Schwaechere den Staerkeren an.
	 * Dabei erhaelt auch der Staerkere ein Evo-Shild.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test
	public void testAttack2_standard() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(3));
		
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(2));
		
		System.out.println(game.getMyField(p2.getId()).getBattlegroundMonster()[0]);
		System.out.println(game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		game.attack(p2.getId(), game.getMyField(p2.getId()).getBattlegroundMonster()[0], game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		System.out.println(game.getMyField(p2.getId()).getBattlegroundMonster()[0]);
		System.out.println(game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		
		assertEquals(1600, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(2, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(1, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
		assertEquals(0, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
		assertEquals(3, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(600, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		
	
	}
	
	/**Beide Karten haben gleich viel ATK.
	 * Erwartetes Verhalten:
	 * Beide verlieren ein Lebensschield.
	 * Beide gewinnen ein Evo-Schield.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	@Test
	public void testAttack2_equalsATK() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(4));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(4));
		game.attack(p2.getId(), game.getMyField(p2.getId()).getBattlegroundMonster()[0], game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		assertEquals(null, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
		assertEquals(1, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(1850, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(1, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
	}
	//TODO: das fehlende Evolutionsschild
	@Test
	/**Wenn der Schwaechere angreift.
	 * FEHLER: Der Staerkere erhaelt kein Evo_Schild.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	public void testAttack2_lowerATK() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), 0, 0);
		assertEquals(3, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(3, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(700, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
//		Sollte eigtl 1, statt 0 sein.
		assertEquals(1, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
	}
	
	@Test(expected = NullPointerException.class)
	public void testAttack2_OwnCardNull() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), null, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
	}
	@Test
	public void testAttack2_AttackPlayer() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), game.getMyField(p2.getId()).getBattlegroundMonster()[0], null);
	}
	
	@Test(expected = LogicException.class)
	public void testAttack2_EnemyNull() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), game.getMyField(p2.getId()).getBattlegroundMonster()[0], null);
	}
	
	@Test(expected = LogicException.class)
	public void testAttack2_CardNotOnField() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), (GameCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0]);
	}
	/**Ein Angriff mit den Karten des Gegners, auf die Eigenen.
	 * @throws LogicException 
	 * @throws GameEndException 
	 * 
	 */
	@Test(expected = LogicException.class)
	public void testAttack2_Inverse() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), game.getEnemyField(p2.getId()).getBattlegroundMonster()[0], game.getMyField(p2.getId()).getBattlegroundMonster()[0]);

	}
	
	
	
	
	@Test
	public void testAttack2_killPlayer() throws LogicException, GameEndException{
		Game game = new Game(p1, p2, d1, d2, true);
		for(int i = 0; i < 2; i++){
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		for(int k = 0; k < Stream.of(game.getMyField(p2.getId()).getBattlegroundMonster()).filter(a -> a != null).count(); k++)
		game.attack(p2.getId(), k, -1);
		}
		assertEquals(0, p1.getShields().getCurrentShields());
	}
	/**WeiterSpielen nach dem Tod eines Spielers
	 * @throws LogicException 
	 * @throws GameEndException 
	 * 
	 */
	@Test(expected = LogicException.class)
	public void testAttack2_PlayingAfterDeath() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, d1, d2, true);
		for(int i = 0; i < 3; i++){
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		for(int k = 0; k < Stream.of(game.getMyField(p2.getId()).getBattlegroundMonster()).filter(a -> a != null).count(); k++)
		game.attack(p2.getId(), k, -1);
		}
	}
	/**Evolutionieren.
	 * FEHLER: Die Karte evolutioniert nicht.
	 * FEHLER: Effekt wird auf das falsche Deck ausgefuehrt.
	 * @throws GameEndException
	 * @throws LogicException
	 */
	//TODO
	@Test
	public void testAttack2_makingEvolutionAndEffects() throws GameEndException, LogicException{
		Card swap = Objects.requireNonNull(c1.stream().filter(c -> c instanceof GameCard && ((GameCard)c).getId() == 60).findFirst().orElseGet(null));
		int change = c1.indexOf(swap);
		c1.set(change, c1.get(0));
		c1.set(0, swap);
		System.out.println("Erste Ausgabe");
		System.out.println(c1.get(0)+" "+c2.get(0));
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		for(int i = 0; i < 2; i++){
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.attack(p1.getId(), game.getMyField(p1.getId()).getBattlegroundMonster()[0], game.getEnemyField(p1.getId()).getBattlegroundMonster()[0]);
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		}
		System.out.println("Zweite Ausgabe");
		System.out.println(Arrays.toString(game.getMyField(p2.getId()).getBattlegroundMonster()));
		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		System.out.println("Dritte Ausgabe");
		System.out.println(Arrays.toString(game.getMyField(p2.getId()).getBattlegroundMonster()));
		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
//		game.attack(p1.getId(), 0, 0);
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		System.out.println("Vierte Ausgabe");
		System.out.println(Arrays.toString(game.getMyField(p2.getId()).getBattlegroundMonster()));
		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.changePlayer(p2.getId());
		System.out.println("Fuenfte Ausgabe");
		System.out.println(Arrays.toString(game.getMyField(p2.getId()).getBattlegroundMonster()));
		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
		assertEquals(500, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(2, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
		assertEquals(2100, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(58, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getId());
	}
	@Ignore
	//TODO: noch nicht fertig, weil der Schwaechere keine Evo-Shilder bekommt.
	/**Komplexere Angriffe.
	 * Um auch mal Angriffe auf Karten anderer Indexe(anstatt immer 0) zu testen.
	 * @throws LogicException 
	 * @throws GameEndException 
	 */
	@Test
	public void testAttack2_attackingSeveralCards() throws GameEndException, LogicException{
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(0));
		game.attack(p2.getId(), 0, 0);
		//Auskommentiert weil der staerkere beim Angriff noch keine Evo-Shields bekommt:
//		assertEquals(1, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getEvolutionShields().getCurrentShields());
		assertEquals(3, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getShields().getCurrentShields());
//		assertEquals(1200, game.getEnemyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
		assertEquals(700, game.getMyField(p2.getId()).getBattlegroundMonster()[0].getAtk());
//		System.out.println(Arrays.toString(game.getMyField(p2.getId()).getBattlegroundMonster()));
//		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
//		System.out.println("---------------------------------------------------------------------------");
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(4));
		game.attack(p1.getId(), 0, 0);
		game.attack(p1.getId(), 1, 0);

//		System.out.println(Arrays.toString(game.getMyField(p2.getId()).getBattlegroundMonster()));
//		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
//		System.out.println("---------------------------------------------------------------------------");
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(4));
		game.attack(p2.getId(), 0, 1);
		game.attack(p2.getId(), 1, 1);
	}
	//TODO
	/**
	 * Um auch mal Angriffe auf Karten anderer Indexe(anstatt immer 0) zu testen.
	 * FEHLER: 
	 * @throws LogicException 
	 * @throws GameEndException 
	 */
	@Test
	public void playingSpecialCard_attackingSeveralCards() throws GameEndException, LogicException{
		for(int i = 0; i < 3; i++){
			Card swap = c2.get(c2.size()-(i+1));
			c2.set(c2.size()-(i+1), c2.get(i));
			c2.set(i, swap);
		}
//		System.out.println(c2.get(0)+" "+c2.get(1)+" "+c2.get(2));
//		System.out.println("--------------------------------------------------------");
//		System.out.println(c2.get(3)+" "+c2.get(4)+" "+c2.get(5)+" "+c2.get(6));
//		System.out.println("--------------------------------------------------------");
//		System.out.println(c1+" ---------------- "+c2);
		Deck d2 = new Deck(2, "TestDeck2", c2);
		Game game = new Game(p1, p2, new Deck(1, "TestDeck1", c1), d2, true);
		for(int i = 0; i < 4; i++){
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.playCard(p1.getId(), game.getCardsOnHand(p1.getId()).get(0));
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
		game.playCard(p2.getId(), game.getCardsOnHand(p2.getId()).get(3));
		}
		game.changePlayer(p1.getId());
		game.getMyField(p1.getId()).addCard();
		game.changePlayer(p2.getId());
		game.getMyField(p2.getId()).addCard();
//		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
//		System.out.println("------------------");
//		System.out.println(game.getCardsOnHand(p2.getId()));
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[3]);
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[3]);
		game.playSpecialCard(p2.getId(), (SpecialCard)game.getCardsOnHand(p2.getId()).get(0), game.getEnemyField(p2.getId()).getBattlegroundMonster()[2]);
		System.out.println(Arrays.toString(game.getEnemyField(p2.getId()).getBattlegroundMonster()));
		System.out.println(Arrays.toString(game.getMyField(p2.getId()).getBattlegroundMonster()));
		for(int i = 0; i < 3; i++){
		assertEquals(1900, game.getMyField(p2.getId()).getBattlegroundMonster()[i].getAtk());
			}
		assertEquals(1100, game.getMyField(p2.getId()).getBattlegroundMonster()[3].getAtk());
		for(int i = 0; i < 2; i++){
			assertEquals(1700, game.getMyField(p1.getId()).getBattlegroundMonster()[i].getAtk());
			}
		assertEquals(1400, game.getMyField(p1.getId()).getBattlegroundMonster()[2].getAtk());
		assertEquals(3, game.getMyField(p1.getId()).getBattlegroundMonster()[2].getShields().getCurrentShields());
		assertEquals(1900, game.getEnemyField(p2.getId()).getBattlegroundMonster()[3].getAtk());
		assertEquals(1, game.getEnemyField(p1.getId()).getBattlegroundMonster()[3].getShields().getCurrentShields());	
	}
	
	@Test
	public void testAttack1_attackingSeveralTimesWithSameCard(){
		
	}
	
	
	/**Karte toetet sich bei Angriff selbst.
	 * 
	 */
	@Test
	public void testAttack2_suicidal(){
		
	}
	@Test
	public void testAttack2_triggeringDestroyEffect(){
		
	}
	@Test
	public void testAttack2_triggeringSingleEffect(){
		
	}
	@Test
	public void testAttack2_triggeringMultipleEffekt(){
		
	}
	
	@Test
	public void testAttack2_triggeringDeckEffekt(){
		
	}
	
	
	/**
	 * Abgepruefte Eigenschaften:
	 * - meherer Evolutionen.
	 * - meherere Effektarten.
	 * - Angriffe aus unter-, ueberlegener oder gleichgestellter Position.
	 * - Angriffe auf hoehrere Indexe(z.B. 3)
	 * - Specialcards.
	 * - korrektes Entfernen von Specialcards.
	 * - Ende des Spiels. Die Spieler leben noch, wenn die Decks aufgebraucht sind. Das Spiel geht dann weiter.
	 */
	@Test
	public void testFullTest(){
		
	}
	
}
