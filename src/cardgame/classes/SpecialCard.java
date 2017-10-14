package cardgame.classes;
import java.util.LinkedList;

public class SpecialCard extends Card{
	
	private LinkedList<Effect> effects;

	
	public SpecialCard(int id, String name, String description, Type type, LinkedList<Effect> effects) {
		super(id, name, description, type);
		this.effects = effects;
	}	

	/**
	 * @return the effects
	 */
	public LinkedList<Effect> getEffects() {
		return effects;
	}

	

}
