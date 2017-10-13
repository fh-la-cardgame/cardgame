package cardgame.classes;
public abstract class Card {
	
	//Variables
	private int id;
	private String name;
	private String description;
	private Type type;
	
	//Functions
	public abstract void getId();
	public abstract void getName();
	public abstract void getDescription();
	public abstract void getType();
	
	

}
