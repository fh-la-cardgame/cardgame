package cardgame.classes;

/**
 * Klasse zur Abbildung der aktuellen und max. Schilder.
 * @author BishaThan
 */
public class Shield {
    /** Maximale Anzahl an Schildern. **/
    private final int maxShields;
    /** Aktuelle Anzahl an Schildern. **/
    private int currentShields;

    /**
     * Konstruktor
     * @param max Maximale/Aktuelle Schilderanzahl
     */
    public Shield(final int max){
        this(max, max);
    }
    
    /**
     * Konstruktor
     * @param current Aktuelle Schilderanzahl
     * @param max Maximale Schilderanzahl
     */
    public Shield(final int current, final int max){        
        this.currentShields = current;
        this.maxShields = max;        
    }
    
    /**
     * Konstruktor
     * @param s Schilder
     */
    public Shield(final Shield s){        
        this(s.currentShields, s.maxShields);        
    }
    
    
    public int getMaxShields() {
        return maxShields;
    }

    public int getCurrentShields() {
        return currentShields;
    }

    /**
     * Reduzierung der Schilder.
     */
    public void dropShield() {
        if(currentShields > 0) currentShields--;
    }
    
    /**
     * Erhoehung der Schilder.
     */
    public void addShield() {
        if(currentShields < maxShields) currentShields++;
    }
    
    @Override
    public String toString(){
    	return getCurrentShields()+" "+getMaxShields();
    }
    
}
