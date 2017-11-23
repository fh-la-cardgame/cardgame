package cardgame.classes;
import java.util.LinkedList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * Klasse zur Abbildung eines Spielers.
 * @author BishaThan
 */
public class Player {
    
        /** Maximale Anzahl der Schutzschilder.  **/
	private static final int MAX_SHIELD = 3;
        
	/** Identifikationsnummer des Spielers. **/
	private final int id;
        /** Name des Spielers. **/
	private final String name;
        
        private final StringProperty pName;
        /** Schutzschilder des Spielers. **/
	private final Shield shields;
	
        /**
         * Konstruktor
         * @param name Spielername
         */
	public Player(final String name) {
            this(0, name);
	}
	
        /**
         * Konstruktor
         * @param id Spielerid
         * @param name Spielername
         */
	public Player(final int id, final String name) {
		this.id = id;
		this.name = name;
                this.pName = new SimpleStringProperty(name);                
		this.shields = new Shield(MAX_SHIELD);
	}

    public StringProperty getpName() {
        return pName;
    }
    public void setpName(String s) {
        pName.setValue(s);
    }

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public Shield getShields() {
		return shields;
	}
	
	

}
