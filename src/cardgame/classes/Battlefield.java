package cardgame.classes;
public class Battlefield {
	private Playground side1;
	private Playground side2;
	private GameCard[][] battlegroundMonster;
	private SpecialCard[][] battlegroundSpecials;
	private static final int ROW=2;
	
	
	public Battlefield(Playground side1, Playground side2) {
		this.side1 = side1;
		this.side2 = side2;
		this.battlegroundMonster = new GameCard[ROW][ROW*2];
		this.battlegroundSpecials = new SpecialCard[ROW][ROW*2];
	}

	public int removeCard(int row, int col){
		return 0;};
		
	public int addCard(int row, int col){
		return 0;
	}

}
