package cardgame.classes;

import java.util.*;

import cardgame.db.DbCard;
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
        for(Card c:cards) {
            if (c instanceof GameCard) this.cards.add(new GameCard((GameCard) c));
            else this.cards.add(new SpecialCard((SpecialCard) c));
        }
    }


    /**
     * Copy-Konstruktor
     * @param d Deck
     */
    public Deck(final Deck d) {
        this(d.getId(), d.getName(), new LinkedList<>(d.getCards()));
        shuffle();
    }

    public Card popCard() {
        //if(cards.isEmpty()) throw new GameEndException();
        if(cards.isEmpty()) return null;
        if(countCards != null) reduceCountCards();
        return cards.remove(0);
    }

    public boolean hasCards(){
        return cards.size() > 0;
    }

    public void shuffle(){
        Collections.shuffle(cards);
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
        Platform.runLater(()-> this.countCards.setValue(cards.size()));
    }

    public void setCountCards() {
        this.countCards = new SimpleIntegerProperty(cards.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        //Prüft ob die gleichen Elemente enthalten sind !
        List<Card> thisCards = new ArrayList<>(cards);
        List<Card> thatCard = new ArrayList<>(deck.cards);
        thatCard.sort(null);
        thisCards.sort(null);
        return thisCards.equals(thatCard);
    }
    @Override
    public int hashCode() {
        return Objects.hash(cards);
    }
}
