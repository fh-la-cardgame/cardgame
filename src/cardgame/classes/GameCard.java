package cardgame.classes;
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
	public GameCard(final int id, final String name, final String description, final Type type, final int atk, final Shield evolutionShields, final Shield shields, final GameCard evolution, final Effect[] effects) {
		super(id, name, description,type);
		this.atk = atk;
		this.evolutionShields = new Shield(evolutionShields);
		this.shields = new Shield(shields);
		this.evolution = evolution;
		this.effects = effects.clone();
	}
        
        /**
         * Copy-Konstruktor
         * @param c Spielkarte
         */
        public GameCard(final GameCard c){
            this(c.id, c.name, c.description, c.type, c.atk, c.evolutionShields, c.shields, c.evolution, c.effects);
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
	public void setAtk(short atk) {
		this.atk = atk >= atk ? atk : this.atk;
	}
	
}
