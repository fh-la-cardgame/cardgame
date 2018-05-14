package cardgame.classes;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

import java.util.Objects;

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
     * @param max     Maximale Schilderanzahl
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
            if(gShield != null) changegShield();
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
            if(gShield != null) changegShield();
            return true;
        } else {
            return false;
        }

    }


    public StringProperty getgShield() {
        return gShield;
    }

    public void setgShield() {
        this.gShield = new SimpleStringProperty(toString());
    }

    private void changegShield() {
        Platform.runLater(()-> this.gShield.setValue(toString()));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shield shield = (Shield) o;
        return maxShields == shield.maxShields &&
                currentShields == shield.currentShields;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxShields, currentShields);
    }

    @Override
    public String toString() {
        return getCurrentShields() + "/" + getMaxShields();
    }
}
