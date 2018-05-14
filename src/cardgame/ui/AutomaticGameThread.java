package cardgame.ui;

import cardgame.ai.KiPlayer;
import cardgame.ai.TestPlayer;
import cardgame.classes.GameEndException;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

public class AutomaticGameThread extends Thread{
    private Game game;

    public AutomaticGameThread(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        try {
            KiPlayer p1 = new TestPlayer(game, 1);
            //KiPlayer p2 = new TestPlayer(game, 2);
            int k = 0;
            game.changePlayer(p1.getId());
            try {
                game.getMyField(p1.getId()).addCard();
            } catch (GameEndException e) {
                game.setPlayerWon(2);
                game.setGameEnd(true);
                return;
            }
            try {
                p1.yourTurn();
            } catch (LogicException e) {
                System.out.println(e);
            }
            //} catch (LogicException ex) {
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
