package cardgame.classes;
/**
 * Bildet eine Teilstruktur einer Karte ab.
 * @author BishaThan
 */
public abstract class Card {
	
	/** Identifikationsnummer der Karte. **/
	private final int id;
        /** Name der Karte. **/
	private final String name;
        /** Kartenbeschreibung. Zusammensetzung aus der Kartenbeschreibung und den Effektbeschreibungen. **/
	private final String description;
        /** Typ einer Karte. **/
	private final Type type;
        
        /**
         * Konstruktor
         * @param id Identifikationnummer
         * @param name Name der Karte
         * @param description Kartenbeschreibung
         * @param type Typ der Karte
         */
        public Card(final int id, final String name, final String description, final Type type) {
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
