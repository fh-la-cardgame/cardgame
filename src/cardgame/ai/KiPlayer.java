package cardgame.ai;

import cardgame.logic.LogicException;

//noch nicht entgueltig
public interface KiPlayer {

	void initialize();
	
	void yourTurn() throws LogicException;
	
	void endGame(boolean won);
	
	int getId();
}
