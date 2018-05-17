package cardgame.ai;

import java.util.Scanner;

import cardgame.logic.LogicException;

//noch nicht entgueltig
public interface KiPlayer {
	public static final boolean DELAY = false;
	public static final int TIME_DELAY = 0;

	void initialize();
	
	void yourTurn() throws LogicException;
	
	void endGame(boolean won);
	
	int getId();
	
	default void delay(){
		if(TIME_DELAY == 0){
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			scanner.close();
		}else{
			try {
				Thread.sleep(TIME_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	} 
}
