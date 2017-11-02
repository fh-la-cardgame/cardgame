package cardgame.classes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
/**
 * Klasse zur Abbildung der Struktur einer Spezialkarte.
 * @author BishaThan
 */
public class SpecialCard extends Card{
	
        /** Liste der Effekte der Spezialkarte. **/
	private List<Effect> effects;

	/**
         * Konstruktor
         * @param id Indentifikationsnummer
         * @param name Name der Spezialkarte
         * @param description Beschriebung
         * @param type Typ
         * @param effects Effekte der Spezialkarte
         */
	public SpecialCard(final int id, final String name, final String description, final Type type, final List<Effect> effects) {
		super(id, name, description, type);
		this.effects = new LinkedList<>(effects);
	}
        
        /**
         * Copy-Konstruktor
         * @param s Spezialkarte
         */
        public SpecialCard(final SpecialCard s){
            this(s.getId(), s.getName(), s.getDescription(), s.getType(), s.getEffects());
        }
        

	/**
	 * @return the effects
	 */
	public List<Effect> getEffects() {
		return effects; // ?? Frage offen: ob Kopie noetig
	}
	
	@Override
	public String toString(){
		return super.toString()+" "+effects.toString()+" \n";
	}

	

}
