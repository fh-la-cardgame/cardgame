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
		/** Bild einer Karte **/
	private final byte[] image;
        
        /**
         * Konstruktor
         * @param id Identifikationnummer
         * @param name Name der Karte
         * @param description Kartenbeschreibung
         * @param type Typ der Karte
         */
        public Card(final int id, final String name, final String description, final Type type, final byte[] image) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.image = image;
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
	
	public byte[] getImage(){
		return image;
	}
	@Override
	public String toString(){
		return getId()+" "+getName()+" "+getDescription()+"\nWerte: "+getType().toString();
	}
	

}
