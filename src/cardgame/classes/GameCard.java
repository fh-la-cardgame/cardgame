package cardgame.classes;

import java.util.Arrays;

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
	}
        
        /**
         * Copy-Konstruktor
         * @param c Spielkarte
         */
        public GameCard(final GameCard c){
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
	
}
