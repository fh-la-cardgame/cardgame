package cardgame.classes;

import java.util.LinkedList;
import java.util.List;

/**
 * Bildet Struktur eines Decks ab.
 * @author BishaThan
 */
public class Deck {
    
    /** Identifikationsnummer. **/
    private final int id;
    /** Deckname. **/
    private final String name;
    /** Karten im Deck. **/
    private final List<Card> cards;

    /**
     * Konstruktor
     * @param id Identifikationsnummer
     * @param name Deckname
     * @param cards Karten im Deck
     */
    public Deck(final int id, final String name, final List<Card> cards) {
        this.id = id;
        this.name = name;
        this.cards = new LinkedList<>();
        for(Card c:cards){
            if(c instanceof GameCard) this.cards.add(new GameCard((GameCard) c));
            else this.cards.add(new SpecialCard((SpecialCard) c));
        }
    }
        
    /**
     * Copy-Konstruktor
     * @param d Deck
     */
    public Deck(final Deck d) {
        this(d.getId(), d.getName(), new LinkedList<>(d.getCards()));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return cards;
    }

    
    
}
