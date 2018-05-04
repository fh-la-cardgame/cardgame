package cardgame.classes;

import cardgame.logic.Game;
import cardgame.ui.SpecialCardControl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Abbildung der Feldseite eines Spielers.
 *
 * @author BishaThan
 */
public class Playground {

    /**
     * Anzahl an Karten der Arrays
     **/
    public final static int ROW = 4;
    private final static int CARDSONHANDSTART = 4;
    private final static int MAXCARDSONHAND = 5;

    /**
     * Spieler.
     **/
    private final Player player;
    /**
     * Deck mit Karten
     **/
    private final Deck deck;
    /**
     * Karten auf der Hand.
     **/
    private final List<Card> cardsOnHand;
    /**
     * Array fuer  Monsterkarten
     **/
    private final GameCard[] battlegroundMonster = new GameCard[ROW];
    /**
     * Array fuer die Zauber- bzw. Fallenkarten
     **/
    private final SpecialCard[] battlegroundSpecials = new SpecialCard[ROW];

    /**
     * Anzahl der SpecialCards auf den Feld.
     */
    private int countBattlegroundSpecials;

    /**
     * Anzahl der Monsterkarten auf dem Feld.
     */
    private int countBattlegroundMonster;


    private ObservableList<GameCard> observableBattlegroundMonster;

    private ObservableList<SpecialCard> observableBattlegroundSpecials;

    private ObservableList<Card> observableCardsOnHand;


    /**
     * Konstruktor
     *
     * @param player
     * @param deck
     */
    public Playground(final Player player, final Deck deck, boolean isTest) {
        this.player = player;
        this.deck = deck;
        this.cardsOnHand = new LinkedList<>(); //LOGIK: Shuffle, 5 Karten aus dem Deck zu weisen
        if (!isTest) {
            deck.shuffle();
        }
        if (deck.getCards().size() > 0) {
            for (int i = 0; i < CARDSONHANDSTART; i++) {
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

    public Playground(Playground playground) {
        this.player = new Player(playground.player);
        this.deck = new Deck(playground.deck);
        this.cardsOnHand = new LinkedList<>();
        this.countBattlegroundMonster = playground.countBattlegroundMonster;
        this.countBattlegroundSpecials = playground.countBattlegroundSpecials;
        for (Card c : playground.cardsOnHand) {
            if (c instanceof GameCard) cardsOnHand.add(new GameCard((GameCard) c));
            else cardsOnHand.add(new SpecialCard((SpecialCard) c));
        }
        for (int i = 0; i < ROW; i++) {
            if (playground.battlegroundMonster[i] != null)
                this.battlegroundMonster[i] = new GameCard(playground.battlegroundMonster[i]);
            if (playground.battlegroundSpecials[i] != null)
                this.battlegroundSpecials[i] = new SpecialCard(playground.battlegroundSpecials[i]);
        }
    }


    /**
     * Getter fuer Karten auf der Hand.
     *
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
     *
     * @param c Spiel- bzw. Spezialkarte
     */
    public void removeCardFromHand(Card c) {
        cardsOnHand.remove(c);
    }

    public Card removeCardFromHand(int index) {
        removeObservableCardsOnHand(index);
        return cardsOnHand.remove(index);
    }


    /**
     * Fuegt der Hand eine Karte vom Deck und entfernt diese aus dem Maindeck.
     */
    public void addCard() throws GameEndException {
        if (cardsOnHand.size() < MAXCARDSONHAND) {
            Card card = deck.popCard();
            cardsOnHand.add(card);
            addObservableCardsOnHand(card);
        }
    }

    /**
     * Loescht die Karte aus der Hand und fuegt sie als Monsterkarte auf das Feld ein.
     *
     * @param card Monsterkarte die eingefuegt werden soll.
     */
    public void addMonsterCard(GameCard card) {
        if (!getCardsOnHand().contains(card)) throw new IllegalArgumentException("Monsterkarte nicht in der Hand !");
        GameCard c = (GameCard)removeCardFromHand(cardsOnHand.indexOf(card));
        //Karten werden von links nach rechts gelegt Eventuell von mitte aus starten !
        for (int i = 0; i < battlegroundMonster.length; i++) {
            if (battlegroundMonster[i] == null) {
                battlegroundMonster[i] = c;
                changeObservableBattlegroudMonster(i,card);
                countBattlegroundMonster++;
                return;
            }
        }
        throw new RuntimeException("Feld ist voll !");
    }

    public int addSpecialCardToField(int index) {
        Card card = removeCardFromHand(index);
        if (!(card instanceof SpecialCard)) throw new IllegalArgumentException("Special Karte nicht in der Hand");
        SpecialCard c = (SpecialCard) card;
        for (int i = 0; i < battlegroundSpecials.length; i++) {
            if (battlegroundSpecials[i] == null) {
                battlegroundSpecials[i] = c;
                changeObservableBattlegroudSpecial(i,c);
                countBattlegroundSpecials++;
                return i;
            }
        }
        throw new RuntimeException("Special Card Feld ist voll !");
    }

    /**
     * Entfernt eine GameCard.
     * Falls GameCard nicht auf dem Feld wird nichts entfernt.
     *
     * @param gameCard Karte die entfernt werden soll.
     */
    public void removeBattlegroundMonster(GameCard gameCard) {
        for (int i = 0; i < ROW; i++) {
            if (battlegroundMonster[i] == gameCard) {
                battlegroundMonster[i] = null;
                changeObservableBattlegroudMonster(i,GameCard.DUMMY);
                countBattlegroundMonster--;
                return;
            }
        }
    }

    /**
     * Entfernt eine Specialcard vom Feld.Anhand des Index.
     * Falls am Index keine SpecialCard ist wird nichts gemacht.
     *
     * @param index Karte die entfernt werden soll.
     * @return gibt True zurueck wenn eine Karte entfernt wurde;
     */
    public boolean removeBattlegroundSpecial(int index) {
        if (battlegroundSpecials[index] == null) return false;
        battlegroundSpecials[index] = null;
        changeObservableBattlegroudSpecial(index,SpecialCard.DUMMY);
        countBattlegroundSpecials--;
        return true;
    }

    public int indexOfBattlegroundSpecial(SpecialCard specialCard) {
        for (int i = 0; i < battlegroundSpecials.length; i++) {
            if (battlegroundSpecials[i] == specialCard) return i;
        }
        return -1;
    }

    public int indexOfBattlegroundMonster(GameCard gameCard) {
        for (int i = 0; i < battlegroundMonster.length; i++) {
            if (battlegroundMonster[i] == gameCard) return i;
        }
        return -1;
    }


    public boolean updateBattlegroundMonster(GameCard oldCard, GameCard newCard) {
        for (int i = 0; i < ROW; i++) {
            if (battlegroundMonster[i] == oldCard) {
                battlegroundMonster[i] = newCard;
                changeObservableBattlegroudMonster(i,newCard);
                return true;
            }
        }
        return false;
    }

    public boolean containsBattlegroundMonster(GameCard card) {
        for (GameCard c : battlegroundMonster)
            if (c == card) return true;
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

    public boolean canPlaySpecialCard() {
        return countBattlegroundSpecials < ROW;
    }

    public boolean canPlayMonsterCard() {
        return countBattlegroundMonster < ROW;
    }

    public int getCountBattlegroundSpecials() {
        return countBattlegroundSpecials;
    }

    public int getCountBattlegroundMonster() {
        return countBattlegroundMonster;
    }


    public void setObservableBattlegroundMonster() {
        this.observableBattlegroundMonster = FXCollections.observableArrayList();
        for(int i=0;i<ROW;i++){
            observableBattlegroundMonster.add(GameCard.DUMMY);
        }
    }

    public void setObservableBattlegroundSpecials() {
        this.observableBattlegroundSpecials = FXCollections.observableArrayList();
        for(int i=0;i<ROW;i++){
            observableBattlegroundSpecials.add(SpecialCard.DUMMY);
        }
    }

    public void setObservableCardsOnHand() {
        this.observableCardsOnHand = FXCollections.observableArrayList(cardsOnHand);
    }

    private void changeObservableBattlegroudMonster(int index, GameCard card){
        if(observableBattlegroundMonster != null) Platform.runLater(()->observableBattlegroundMonster.set(index,card));
    }

    private void changeObservableBattlegroudSpecial(int index,SpecialCard card){
        if(observableBattlegroundSpecials != null) Platform.runLater(()->observableBattlegroundSpecials.set(index,card));
    }
    private void addObservableCardsOnHand(Card card){
        if(observableCardsOnHand != null) Platform.runLater(()->observableCardsOnHand.add(card));
    }
    private void removeObservableCardsOnHand(int index){
        if(observableCardsOnHand != null) Platform.runLater(()->observableCardsOnHand.remove(index));
    }

    public ObservableList<GameCard> getObservableBattlegroundMonster() {
        return observableBattlegroundMonster;
    }

    public ObservableList<SpecialCard> getObservableBattlegroundSpecials() {
        return observableBattlegroundSpecials;
    }

    public ObservableList<Card> getObservableCardsOnHand() {
        return observableCardsOnHand;
    }

    public static int getRow() {
        return ROW;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playground that = (Playground) o;
        if (countBattlegroundSpecials != that.countBattlegroundSpecials ||
                countBattlegroundMonster != that.countBattlegroundMonster ||
                !Objects.equals(player, that.player)) return false;

        List<Card> c1 = new ArrayList<>(cardsOnHand);
        List<Card> c2 = new ArrayList<>(that.cardsOnHand);
        Iterator<Card> i1 = c1.iterator();
        while (i1.hasNext()) {
            Card next = i1.next();
            Iterator<Card> i2 = c2.iterator();
            boolean contains = false;
            while (i2.hasNext()) {
                if (i2.next().equals(next)) {
                    contains = true;
                    i1.remove();
                    i2.remove();
                    break;
                }
            }
            if (!contains) return false;
        }

        Card[] g1 = battlegroundMonster.clone();
        Card[] g2 = that.battlegroundMonster.clone();
        for (int i = 0; i < g1.length; i++) {
            if (g1[i] != null) {
                boolean contains = false;
                for (int j = 0; j < g2.length; j++) {
                    if (g1[i].equals(g2[j])) {
                        contains = true;
                        g2[j] = null;
                        break;
                    }
                }
                if (!contains) return false;
            }
        }
        g1 = battlegroundSpecials.clone();
        g2 = that.battlegroundSpecials.clone();
        for (int i = 0; i < g1.length; i++) {
            if (g1[i] != null) {
                boolean contains = false;
                for (int j = 0; j < g2.length; j++) {
                    if (g1[i].equals(g2[j])) {
                        contains = true;
                        g2[j] = null;
                        break;
                    }
                }
                if (!contains) return false;
            }
        }
        return Objects.equals(deck, that.deck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, deck, countBattlegroundSpecials, countBattlegroundMonster);
    }
}
