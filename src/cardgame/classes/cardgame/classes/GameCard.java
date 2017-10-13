package cardgame.classes;

public class GameCard extends Card {
	
	private short atk;
	private byte[] collectorShields;
	private byte[] shields;
	private GameCard evolution;
	private byte[] effects;
	
	/**Constructor
	 * 
	 * @param id
	 * @param name
	 * @param atk
	 * @param description
	 * @param collectorShields
	 * @param shields
	 * @param evolution
	 */
	
	private GameCard (int id, String name, short atk, String description, byte collectorShields, byte shields, GameCard evolution){
		
	}
	
	
	

	public short getAtk() {
		return atk;
	}
	public byte[]getCollectorShields() {
		return collectorShields;
	}
	public byte[]getShields(){
		return shields;
	}
	public GameCard getEvolution(){
		return evolution;
	}
	public byte[] getEffects(){
		return effects;
	}

	@Override
	public void getId() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getName() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDescription() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getType() {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
