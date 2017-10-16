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
    /** Karten im Deck / Maindeck. **/
    private final List<Card> cards;
    /** Karten auf der Hand. **/
    private final List<Card> cardsOnHand;
	
    /**
     * Konstruktor
     * @param player
     * @param cards
     */
    public Playground(final Player player, final List<Card> cards) {
		this.player = player;
		this.cards = new LinkedList<>(cards); 
		this.cardsOnHand = new LinkedList<>(cards); //LOGIK: Shuffle, 5 Karten aus dem Deck zu weisen
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
