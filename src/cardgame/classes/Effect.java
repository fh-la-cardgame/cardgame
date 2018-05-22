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
      /** Angabe bei welchem Schild der Effekt wirkt**/
      private final int affectedShield;
	
	
	/**
         * Konstruktor
         * @param id Identifikationsnummer
         * @param description Effektbeschreibung
         * @param effectType Effekttyp (Verminderung, Erhöhung)
         * @param affectedShield Betroffenes Schild
         */
	public Effect(final int id, final String description, final EffectType effectType, final int effectNumber, final int affectedShield) {
		this.id = id;
		this.description = description;
        this.effectType = effectType;
        this.effectNumber = effectNumber;
        this.affectedShield = affectedShield;
                
	}
        
        /**
         * Copy-Konstruktor
         * @param e 
         */
        public Effect(final Effect e){
            this(e.id, e.description, e.effectType, e.effectNumber, e.affectedShield);
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
    
    public int getAffectedShield(){
        return affectedShield;
    }
    
    @Override
    public String toString(){
    	return getId()+" "+getDescription() +" "+ getEffectType()+"::"+getEffectNumber();
    }
        
    
	

}
