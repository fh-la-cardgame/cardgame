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
        /** Effekttyp (z.B. Verminderung) **/
      private final EffectType effectType;
      	/** Angabe der Staerke **/
      private final int effectNumber;
	
	
	/**
         * Konstruktor
         * @param id Identifikationsnummer
         * @param description Effektbeschreibung
         * @param effectType Effekttyp (Verminderung, Erhöhung)
         */
	public Effect(final int id, final String description, final EffectType effectType, final int effectNumber) {
		this.id = id;
		this.description = description;
        this.effectType = effectType;
        this.effectNumber = effectNumber;
                
	}
        
        /**
         * Copy-Konstruktor
         * @param e 
         */
        public Effect(final Effect e){
            this(e.id, e.description, e.effectType, e.effectNumber);
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

    public EffectType getEffectType() {
        return effectType;
    }
    
    public int getEffectNumber(){
    	return effectNumber;
    }
    
    @Override
    public String toString(){
    	return getId()+" "+getDescription() +" "+ getEffectType()+"::"+getEffectNumber();
    }
        
    
	

}
