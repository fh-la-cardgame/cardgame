package cardgame.classes;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

/**
 * Klasse zur Abbildung der aktuellen und max. Schilder.
 *
 * @author BishaThan
 */
public class Shield {

    /**
     * Maximale Anzahl an Schildern. *
     */
    private final int maxShields;
    /**
     * Aktuelle Anzahl an Schildern. *
     */
    private int currentShields;
    /* Schwarze Schilder - Schutzschilder **/
    private StringProperty gShield;

    /**
     * Konstruktor
     *
     * @param max Maximale/Aktuelle Schilderanzahl
     */
    public Shield(final int max) {
        this(max, max);
    }

    /**
     * Konstruktor
     *
     * @param current Aktuelle Schilderanzahl
     * @param max Maximale Schilderanzahl
     */
    public Shield(final int current, final int max) {
        this.currentShields = current;
        // this.pCurrentShields = new SimpleIntegerProperty(current);
        this.maxShields = max;
        this.gShield = null;
    }

    /**
     * Konstruktor
     *
     * @param s Schilder
     */
    public Shield(final Shield s) {
        this(s.currentShields, s.maxShields);
    }
//
//    public IntegerProperty getpCurrentShields() {
//        return pCurrentShields;
//    }
//
//    private void setpCurrentShields(int shield) {
//        this.pCurrentShields.setValue(shield);
//
//    }

    public int getMaxShields() {
        return maxShields;
    }

    public int getCurrentShields() {
        return currentShields;
    }

    /**
     * Reduzierung der Schilder.
     *
     * @return true falls sich Anzahl veae�ndert, sonst false
     */
    public boolean dropShield() {
        if (currentShields > 0) {
            //setpCurrentShields(--currentShields);
            currentShields--;
            setgShield();
            return true;
        } else {
            return false;
        }

    }

    /**
     * Erhoehung der Schilder.
     *
     * @return true falls sich Anzahl veraendert, sonst false
     */
    public boolean addShield() {
        if (currentShields < maxShields) {
            //setpCurrentShields(++currentShields);
            currentShields++;
            setgShield();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return getCurrentShields() + "/" + getMaxShields();
    }

    public StringProperty getgShield() {
        return gShield;
    }
    private void setgShield() {
        if(this.toString() == null || this.toString().isEmpty()){
            this.gShield.set("-/-");
        }
        this.gShield.set(this.toString());
    }

}
