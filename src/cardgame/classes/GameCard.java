package cardgame.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Klasse zur Abbildung der Struktur einer Spielkarte.
 * @author BishaThan
 */
public class GameCard extends Card {
	
        /** Angriffspunkte. **/
	private int atk;
        /** Notwendige und erreichte Schilder fuer Evolution der Karte. **/
	private final Shield evolutionShields;
        /** Schutzschilder der Karte. **/
	private final Shield shields;
        /** Kartenevolution. **/
	private final GameCard evolution;
        /** Karteneffekte. **/
	private final Effect[] effects;
		/** EvoEffekte		**/
	private final Effect[] evoEffects;
		/** Effect der noch ausgefuert werden muss **/
	private Effect nextEffect = null;
	/** Liste aller SpecialCards die auf diese Karte wirken. */
	private List<SpecialCard> specialCards;
		 

        /**
         * Konstruktor
         * @param id Identifikationsnummer
         * @param name Kartenname
         * @param description Kartenbeschreibung
         * @param type Kartentyp
         * @param atk Angriffspunkte
         * @param evolutionShields Evolutionsschilder
         * @param shields Schutzschilder der Spielkarte
         * @param evolution Evolutionskarte
         * @param effects Karteneffekte
         */
	public GameCard(final int id, final String name, final String description, final Type type, final int atk, final Shield evolutionShields, final Shield shields, final GameCard evolution, final Effect[] effects, final Effect[] evoEffects) {
		super(id, name, description,type);
		this.atk = atk;
		this.evolutionShields = new Shield(evolutionShields);
		this.shields = new Shield(shields);
		this.evolution = evolution;
		this.effects = effects.clone();
		this.evoEffects = evoEffects;
		this.specialCards = new ArrayList<>();
	}
        
        /**
         * Copy-Konstruktor
         * @param c Spielkarte
         */
        public GameCard(final GameCard c){
			//TODO Update Copy Ctor for SpecialCard List
			this(c.getId(), c.getName(), c.getDescription(), c.getType(), c.getAtk(), c.getEvolutionShields(), c.getShields(), c.getEvolution(), c.getEffects(), c.getEvoEffects());
        }

	/**
	 * Ändert die Atk Punkte um den Wert add.
	 * @param add Die Atk punkte die hinzugefügt oder abgezogen werden sollen.
	 */
	public void changeAtk(int add){
        	this.atk += add;
        	if(atk < 0) atk = 0;
	}
	public void addSpecialCard(SpecialCard s){
		for(SpecialCard special:specialCards){
			if(special == s) return;
		}
		specialCards.add(s);
	}

	public void removeSpecialCard(SpecialCard s){
		Iterator<SpecialCard> iterator = specialCards.iterator();
		while(iterator.hasNext()){
			if(iterator.next() == s) {
				iterator.remove();
				return;
			}
		}
	}

	public List<SpecialCard> getSpecialCards(){return specialCards;}
	public boolean isAlive(){
		return shields.getCurrentShields() > 0;
	}
	public int getAtk() {
		return atk;
	}
	public Shield getEvolutionShields() {
		return evolutionShields;
	}
	public Shield getShields(){
		return shields;
	}
	public GameCard getEvolution(){
		return evolution;
	}
	public Effect[] getEffects(){
		return effects; // Offene Frage: Clonen ?
	}
	public void setAtk(int atk) {
		this.atk = atk;
	}
	
	public Effect[] getEvoEffects(){
		return evoEffects;	//Offene Frage: Clonen?
	}
	@Override
	public String toString(){
		return super.toString() +" "+getAtk()+" "+getEvolutionShields()+" "+getShields()+" \nEvo: "+getEvolution()+"\nEffects: "+Arrays.toString(getEffects())+"\nEvoEffects: "+Arrays.toString(evoEffects)+"\n";
	}
	
	/**
	 * Erniedrigt die Schilder um ein Schild.
	 * Prueft, ob dadurch ein Effect ausgeloest wird.
	 * Dieser muss mit der Methode getNextEffect geholt und ausgefuehrt werden.
	 * @return true(wenn Karte noch am Leben) false(falls Karte keine Schilder mehr besitzt)
	 */
	public boolean dropShield() {
		shields.dropShield();
		int shield = shields.getCurrentShields();
		if(shield > 0) {
			if(shield < effects.length) {
				if(nextEffect != null) {
					throw new RuntimeException("Alter Effect wurde noch nicht ausgefuert");
				}
				nextEffect = effects[shield];
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Erhoeht die EvolutionSchilder um eins.
	 * Prueft, ob dadurch ein Effect ausgeloest wird.
	 * Dieser muss mit der Methode getNextEffect geholt und ausgefuehrt werden.
	 * @return true(wenn Karte noch am Leben) false(falls Karte keine Schilder mehr besitzt)
	 */
	public GameCard AddEvoShield() {
		int beforeShield = evolutionShields.getCurrentShields();
		evolutionShields.addShield();
		int shield = evolutionShields.getCurrentShields();
		if(beforeShield != shield) {
			if(shield < evoEffects.length) {
				if(nextEffect != null) {
					throw new RuntimeException("Alter Effect wurde noch nicht ausgefuert");
				}
				nextEffect = evoEffects[shield];
			}
		}
		if(shield == evolutionShields.getMaxShields()) {
			return evolution;
		}
		return null;
		
	}
	
	/**
	 * Gibt Effect zuruek, der noch ausgefuehrt werden muss.
	 * Setzt dabei nextEffect wieder auf null.
	 * @return
	 */
	public Effect getNextEffect() {
		Effect e = nextEffect;
		nextEffect = null;
		return e;
	}
	
}
