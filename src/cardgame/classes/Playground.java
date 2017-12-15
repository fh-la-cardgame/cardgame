package cardgame.classes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Abbildung der Feldseite eines Spielers.
 * @author BishaThan
 */
public class Playground {
	
    /** Anzahl an Karten der Arrays**/
	private final static int ROW = 4;
	private final static int CARDSONHANDSTART = 4;
	private final static int MAXCARDSONHAND = 5;
	
    /** Spieler. **/
    private final Player player;
    /** Deck mit Karten **/
    private final Deck deck;
    /** Karten auf der Hand. **/
    private final List<Card> cardsOnHand;
    
    /** Array fuer  Monsterkarten**/
	private final GameCard[] battlegroundMonster = new GameCard[ROW];        
    /** Array fuer die Zauber- bzw. Fallenkarten**/
	private final SpecialCard[] battlegroundSpecials = new SpecialCard[ROW];

	private int countBattlegroundSpecials;

	private int countBattlegroundMonster;
	
	
    /**
     * Konstruktor
     * @param player
     * @param deck
     */
    public Playground(final Player player, final Deck deck) {
		this.player = player;
		this.deck = deck; 
		this.cardsOnHand = new LinkedList<>(); //LOGIK: Shuffle, 5 Karten aus dem Deck zu weisen
        deck.shuffle();
        if(deck.getCards().size() > 0){
        for(int i=0;i<CARDSONHANDSTART;i++){
            try {
                cardsOnHand.add(deck.popCard());
            } catch (GameEndException e) {
                throw new RuntimeException("Deck hat nicht genug Karten");
            }

        }
        }
        countBattlegroundMonster = 0;
        countBattlegroundSpecials = 0;
    }


    /**
     * Getter fuer Karten auf der Hand.
     * @return Karten
     */
    public List<Card> getCardsOnHand() {
		return Collections.unmodifiableList(cardsOnHand);
    }
    
    public Player getPlayer() {
		return player;
	}

    /**
     * Entfernt Karte aus der Hand.
     * @param c Spiel- bzw. Spezialkarte
     */
    private void removeCardFromHand(Card c){
        cardsOnHand.remove(c);
    }

    /**
     * Fuegt der Hand eine Karte vom Deck und entfernt diese aus dem Maindeck.
     */
    public void addCard() throws GameEndException {
    	if(cardsOnHand.size() < MAXCARDSONHAND) {
    		cardsOnHand.add(deck.popCard());
    	}
        
    }

    /**
     * Loescht die Karte aus der Hand und fuegt sie als Monsterkarte auf das Feld ein.
     * @param card Monsterkarte die eingefuegt werden soll.
     */
    public void addMonsterCard(GameCard card){
       if(!getCardsOnHand().contains(card)) throw new IllegalArgumentException("Monsterkarte nicht in der Hand !");
       removeCardFromHand(card);
       //Karten werden von links nach rechts gelegt Eventuell von mitte aus starten !
        for(int i=0;i<battlegroundMonster.length;i++){
            if(battlegroundMonster[i] == null) {
                battlegroundMonster[i] = card; //Clonen ?
                countBattlegroundMonster++;
                return;
            }
        }
        throw new RuntimeException("Feld ist voll !");
    }

    public void addSpecialCardToField(SpecialCard card){
        if(!getCardsOnHand().contains(card)) throw new IllegalArgumentException("Special Karte nicht in der Hand");
        removeCardFromHand(card);
        for(int i=0;i<battlegroundSpecials.length;i++){
            if(battlegroundSpecials[i] == null){
                battlegroundSpecials[i] = card; //Clonen ?
                countBattlegroundSpecials++;
                return;
            }
        }
        throw new RuntimeException("Special Card Feld ist voll ! ");
    }
    /**
     * Entfernt eine GameCard.
     * Falls GameCard nicht auf dem Feld wird nichts entfernt.
     * @param gameCard Karte die entfernt werden soll.
     */
    public void removeBattlegroundMonster(GameCard gameCard) {
    	for(int i = 0; i < ROW; i++) {
    		if(battlegroundMonster[i] == gameCard) {
    			battlegroundMonster[i] = null;
    			countBattlegroundMonster--;
    			return;
    		}
    	}
    }
    /**
     * Entfernt eine Spezialkarte vom Feld.
     * Falls Specialcard nicht auf dem Feld wird nichts entfernt.
     * @param special Karte die entfernt werden soll.
     * @return gibt True zurueck wenn eine Karte entfernt wurde;
     */
    public boolean removeBattlegroundSpecial(SpecialCard special) {
        for(int i = 0; i < ROW; i++) {
            if(battlegroundSpecials[i] == special) {
                battlegroundSpecials[i] = null;
                countBattlegroundSpecials--;
                return true;
            }
        }
        return false;
    }

    public boolean updateBattlegroundMonster(GameCard oldCard,GameCard newCard){
        for(int i=0;i< ROW;i++){
            if(battlegroundMonster[i] == oldCard){
                battlegroundMonster[i] = newCard;
                return true;
            }
        }
        return false;
    }

    public boolean containsBattlegroundMonster(GameCard card){
        for(GameCard c:battlegroundMonster)
            if(c == card) return true;
        return false;
    }

	public GameCard[] getBattlegroundMonster() {
		return battlegroundMonster;
	}

	public SpecialCard[] getBattlegroundSpecials() {
		return battlegroundSpecials;
	}

    public Deck getDeck() {
        return deck;
    }

    public boolean canPlaySpecialCard(){ return countBattlegroundSpecials < ROW; }

    public boolean canPlayMonsterCard(){return countBattlegroundMonster < ROW;}

    public int getCountBattlegroundSpecials() {return countBattlegroundSpecials;}

    public int getCountBattlegroundMonster() { return countBattlegroundMonster; }

        
}
