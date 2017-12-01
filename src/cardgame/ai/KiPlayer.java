package cardgame.ai;

//noch nicht entgueltig
public interface KiPlayer {

	void initialize();
	
	void yourTurn();
	
	void endGame(boolean won);
	
	int getId();
}
