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
     * @return true falls sich Anzahl verändert, sonst false
     */
    public boolean dropShield() {
        if(currentShields > 0) {
        	currentShields--;
        	return true;
        } else {
        	return false;
        }
        	
    }
    
    /**
     * Erhoehung der Schilder.
     * @return true falls sich Anzahl verändert, sonst false
     */
    public boolean addShield() {
        if(currentShields < maxShields) {
        	currentShields++;
        	return true;
        } else {
        	return false;
        }
        
    }
    
    @Override
    public String toString(){
    	return getCurrentShields()+" "+getMaxShields();
    }
    
}
