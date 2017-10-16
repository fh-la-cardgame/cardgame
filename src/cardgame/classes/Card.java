package cardgame.classes;
/**
 * Bildet eine Teilstruktur einer Karte ab.
 * @author BishaThan
 */
public abstract class Card {
	
	/** Identifikationsnummer der Karte. **/
	protected final int id;
        /** Name der Karte. **/
	protected final String name;
        /** Beschreibung der Karte. Zusammensetzung aus der Kartenbeschreibung und Effektbeschreibungen. **/
	protected final String description;
        /** Typ einer Karte. **/
	protected final Type type;
        
        /**
         * Konstruktor
         * @param id Identifikationnummer
         * @param name Name der Karte
         * @param description Kartenbeschreibung
         * @param type Typ der Karte
         */
        public Card(int id, String name, String description, Type type) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
	}
	
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
	

}
