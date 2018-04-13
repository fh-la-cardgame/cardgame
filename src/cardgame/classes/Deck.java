package cardgame.classes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

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
    /** Laufvariable fuer GUI-Anzahl Karten **/
    private IntegerProperty countCards;

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
        this.countCards = new SimpleIntegerProperty(this.cards.size());
    }

    public Card popCard() throws GameEndException {
        reduceCountCards();
        if(cards.isEmpty()) {
            throw new GameEndException();
        }
        return cards.remove(0);
    }

    public boolean hasCards(){
        return cards.size() > 0;
    }

    public void shuffle(){
        Collections.shuffle(cards);
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

    public IntegerProperty getCountCards() {
        return countCards;
    }

    public void reduceCountCards() {
        if(countCards.getValue() > 0)
            Platform.runLater(()-> this.countCards.setValue(countCards.getValue()-1));
        System.out.println("countCards:"+countCards.getValue());
    }

    
    
}
