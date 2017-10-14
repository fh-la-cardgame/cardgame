package cardgame.classes;

public class GameCard extends Card {
	



	private short atk;
	private Shield collectorShields;
	private Shield shields;
	private GameCard evolution;
	private Effect[] effects;
	
	/**Constructor
	 * 
	 * @param id
	 * @param name
	 * @param atk
	 * @param description
	 * @param collectorShields
	 * @param shields
	 * @param evolution
	 * @param effects
	 */
	public GameCard(int id, String name, String description, Type type, short atk, Shield collectorShields, Shield shields, GameCard evolution, Effect[] effects) {
		super(id, name, description,type);
		this.atk=atk;
		this.collectorShields=collectorShields;
		this.shields=shields;
		this.evolution=evolution;
		this.effects=effects.clone();
	}
	
	
	
	
	public short getAtk() {
		return atk;
	}
	public Shield getCollectorShields() {
		return collectorShields;
	}
	public Shield getShields(){
		return shields;
	}
	public GameCard getEvolution(){
		return evolution;
	}
	public Effect[] getEffects(){
		return effects;
	}



	public void setAtk(short atk) {
		this.atk = atk;
	}
	
	
	

}
