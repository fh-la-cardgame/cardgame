package cardgame.classes;
import java.util.LinkedList;
import java.util.List;

/**
 * Abbildung der Feldseite eines Spielers.
 * @author BishaThan
 */
public class Playground {
	
    /** Spieler. **/
    private final Player player;
    /** Deck mit Karten **/
    private final Deck deck;
    /** Karten auf der Hand. **/
    private final List<Card> cardsOnHand;
	
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

	

}
