package cardgame.classes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Abbildung der Feldseite eines Spielers.
 * @author BishaThan
 */
public class Playground {
	
    /** Anzahl an Karten der Arrays**/
	private final static int ROW = 4;
	private final static int CARDSONHANDSTART = 4;
	private final static int MAXCARDSONHAND = 5;
        
        /** GUI */
        
       
	
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
    
    /** Array fuer  Monsterkarten**/
	//private final GameCard[] guiBattlegroundMonster = new GameCard[ROW];        
    /** Array fuer die Zauber- bzw. Fallenkarten**/
	private final SpecialCard[] guiBattlegroundSpecials = new SpecialCard[ROW];

    /** GUI **/
    private Card example;
    // private ListView<Card> guiBattlegroundMonster;
    ObservableList<Card> guiObservableBattlegroundMonster;
	private int countBattlegroundSpecials;

	private int countBattlegroundMonster;
	
    /**
     * Konstruktor
     * @param player
     * @param deck
     */
    public Playground(final Player player, final Deck deck, boolean isTest) {
		this.player = player;
		this.deck = deck; 
		this.cardsOnHand = new LinkedList<>(); //LOGIK: Shuffle, 5 Karten aus dem Deck zu weisen
        if(!isTest){
        	deck.shuffle();
        }
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
        
        /** GUI */
         example = new GameCard();
        
        createFieldsPlaceholder();
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
    public void removeCardFromHand(Card c){
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
        AtomicInteger e= new AtomicInteger(0);
        for(int i=0;i<battlegroundMonster.length;i++){
            if(battlegroundMonster[i] == null) {
                battlegroundMonster[i] = card; //Clonen ?
                e.set(i);
                Platform.runLater(()->guiObservableBattlegroundMonster.add(e.get(), card));
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
                guiBattlegroundSpecials[i] = new SpecialCard();
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
       //
        //AtomicInteger e = new AtomicInteger(0);
    	for(int i = 0; i < ROW; i++) {
    		if(battlegroundMonster[i] == gameCard) {
    			battlegroundMonster[i] = null;
    			int e = i;
                Platform.runLater(()->guiObservableBattlegroundMonster.set(e,new GameCard()));
                //guiBattlegroundMonster.getItems().add(i, new GameCard());
                    System.out.println(">>>>>>>>>>>>>");
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
                guiBattlegroundSpecials[i] = new SpecialCard();
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
                guiObservableBattlegroundMonster.add(i, newCard);
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

    private void createFieldsPlaceholder() {
        guiObservableBattlegroundMonster = FXCollections.observableArrayList();
        for(int i=0; i < battlegroundMonster.length; i++)
            guiObservableBattlegroundMonster.add(i, new GameCard());
       /* guiBattlegroundMonster = new ListView<>();
        guiBattlegroundMonster.setOrientation(Orientation.HORIZONTAL);
        guiObservableBattlegroundMonster = FXCollections.observableArrayList();
        //guiObservableBattlegroundMonster = FXCollections.observableArrayList(this.cardsOnHand);
        guiBattlegroundMonster.setItems(guiObservableBattlegroundMonster);
        
                for(int i=0; i < battlegroundMonster.length; i++){            	        
                guiObservableBattlegroundMonster.add(i, new GameCard());
                System.out.println("Size:"+guiObservableBattlegroundMonster.size());
        }
        
        for(int i=0; i < guiBattlegroundSpecials.length; i++){
            guiBattlegroundSpecials[i] = new SpecialCard();
        }
    
        
        
        guiBattlegroundMonster.setCellFactory(new Callback<ListView<Card>,ListCell<Card>>(){
            @Override
            public ListCell<Card> call(ListView<Card> param) {
                 ListCell<Card> cell = new ListCell<Card>(){
                     
                     @Override
                     protected void updateItem(Card item, boolean empty){
                     super.updateItem(item, empty);
                     if(item!=null){
                         System.out.println("Item NICHT NULL" + System.currentTimeMillis());
                         System.out.println(item.toString()); 
                     }else{
                         
//                         System.out.println("-----------------");
//                         System.out.println(item.toString());
                         System.out.println("Item NULL" + System.currentTimeMillis());
                     }
                     }
                 };
                 
                 return cell;
            }
        });*/

    }

    public ListView<Card> getGuiBattlegroundMonster() {
        return null;
    }

    public ObservableList<Card> getGuiObservableBattlegroundMonster() {
        return guiObservableBattlegroundMonster;
    }



    public SpecialCard[] getGuiBattlegroundSpecials() {
        return guiBattlegroundSpecials;
    }

    

}
