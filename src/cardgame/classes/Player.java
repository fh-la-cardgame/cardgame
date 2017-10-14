package cardgame.classes;
import java.util.LinkedList;

public class Player {
	
	private int id;
	private String name;
	private Shield shields;
	private LinkedList<Card> cards;
	/**
	 * @param name
	 * @param shields
	 */
	public Player(String name, LinkedList cards) {
		
		this.name = name;
		this.cards=cards;
	}
	public String getName() {
		return name;
	}
	public Shield getShields() {
		return shields;
	}
	
	

}
