package cardgame.classes;
/**
 * 
 * @author shinzoabe
 * Bildet die Struktur ab.
 *
 */
public abstract class Card {
	
	/** id - Identifikationsnummer **/
	protected  int id;
	protected String name;
	protected String description;
	protected Type type;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Type getType() {
		return type;
	}
	public Card(int id, String name, String description, Type type) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
	}
	
	
	
	

}
