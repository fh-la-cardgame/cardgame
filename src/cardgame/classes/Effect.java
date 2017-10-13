package cardgame.classes;

public class Effect {
	private int id;
	private String description;
	
	
	/**
	 * @param id
	 * @param description
	 */
	public Effect(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	

}
