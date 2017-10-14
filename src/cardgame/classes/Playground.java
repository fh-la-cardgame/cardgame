package cardgame.classes;
import java.util.LinkedList;

public class Playground {
	
	private Player player;
	private LinkedList<Card> cards;
	private LinkedList<Card> cardsOnHand;
	
	
	
	public Playground(Player player, LinkedList<Card> cards) {
		this.player=player;
		this.cards = cardsOnHand; //clone
	}
	public LinkedList<Card> getCardsOnHand() {
		return cardsOnHand;
	}
	public void removeCard(Card c){
		
	}
	public void addCard(){}
	
	public Player getPlayer() {
		return player;
	}
	

}
