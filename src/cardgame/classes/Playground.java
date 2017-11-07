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
     * @param cards
     */
    public Playground(final Player player, final Deck deck) {
		this.player = player;
		this.deck = deck; 
		this.cardsOnHand = new LinkedList<>(); //LOGIK: Shuffle, 5 Karten aus dem Deck zu weisen
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
    public void removeCard(Card c){
        //Logik
    }

    /**
     * Fuegt der Hand eine Karte vom Deck und entfernt diese aus dem Maindeck.
     */
    public void addCard(){
        //Logik
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
    			//Effektkarten die auf diese Karte wirken entfernen nicht implementiert
    		}
    	}
    }

	public GameCard[] getBattlegroundMonster() {
		return battlegroundMonster;
	}


	public SpecialCard[] getBattlegroundSpecials() {
		return battlegroundSpecials;
	}
	

}
