package cardgame.classes;
import java.util.LinkedList;

public class Player {
	
	private int id;
	private String name;
	private Shield shields;
	private static final byte MAX_SHIELD=3;
	
	
	/**
	 * @param name
	 * @param shields
	 */
	public Player(String name) {
	this(0, name);
	}
	
	public Player(int id, String name) {
		this.id = id;
		this.name = name;
		this.shields=new Shield(MAX_SHIELD);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public Shield getShields() {
		return shields;
	}
	
	

}
