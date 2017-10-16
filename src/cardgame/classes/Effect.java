package cardgame.classes;
/**
 * Bildet eine Struktur eines Effektes ab.
 * @author BishaThan
 */
public class Effect {
    
        /** Identifikationsnummer des Effekts. **/
	private final int id;
        /** Effektbeschreibung. **/
	private final String description;
	
	
	/**
         * Konstruktor
         * @param id Identifikationsnummer
         * @param description Effektbeschreibung
         */
	public Effect(final int id, final String description) {
		this.id = id;
		this.description = description;
	}
        
        /**
         * Copy-Konstruktor
         * @param e 
         */
        public Effect(final Effect e){
            this(e.id, e.description);
        }
        /**
         * Anwendung des Effekts.
         * @param c Spielkarte
         */
	public void useEffect(GameCard c){
		//Logik
	}
	public int getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	

}
