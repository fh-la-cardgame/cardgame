package cardgame.classes;
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
            cardsOnHand.add(deck.popCard());
        }
        }
    }


    /**
     * Getter fuer Karten auf der Hand.
     * @return Karten
     */
    public List<Card> getCardsOnHand() {
		return cardsOnHand;
    }
    
    public Player getPlayer() {
		return player;
	}

    /**
     * Entfernt Karte aus der Hand.
     * @param c Spiel- bzw. Spezialkarte
     */
    public void removeCardFromHand(Card c){
        cardsOnHand.remove(c);
    }

    /**
     * Fuegt der Hand eine Karte vom Deck und entfernt diese aus dem Maindeck.
     */
    public void addCard(){
    	if(cardsOnHand.size() < MAXCARDSONHAND) {
    		cardsOnHand.add(deck.popCard());
    	}
        
    }

    /**
     * Loescht die Karte aus der Hand und fuegt sie als Monsterkarte auf das Feld ein.
     * @param card Monsterkarte die eingefuegt werden soll.
     */
    public void addMonsterCard(GameCard card){
       if(!getCardsOnHand().contains(card)) throw new IllegalArgumentException();
       removeCardFromHand(card);
       //Karten werden von links nach rechts gelegt Eventuell von mitte aus starten !
        for(int i=0;i<battlegroundMonster.length;i++){
            if(battlegroundMonster[i] == null) {
                battlegroundMonster[i] = card; //Clonen ?
                return;
            }
        }
        throw new RuntimeException("Feld ist voll !");
    }

    public void addSpecialCardToField(SpecialCard card){
        if(!getCardsOnHand().contains(card)) throw new IllegalArgumentException();
        removeCardFromHand(card);
        for(int i=0;i<battlegroundSpecials.length;i++){
            if(battlegroundSpecials[i] == null){
                battlegroundSpecials[i] = card; //Clonen ?
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
                battlegroundMonster[i] = null;
                return true;
            }
        }
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
	
 
        
}
