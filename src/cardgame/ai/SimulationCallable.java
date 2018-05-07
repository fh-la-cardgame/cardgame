package cardgame.ai;

import cardgame.classes.GameEndException;
import cardgame.logic.Game;
import cardgame.logic.LogicException;
import java.util.concurrent.Callable;

public class SimulationCallable implements Callable<java.lang.Boolean> {

    private final Node node;

    private final int myId;

    SimulationCallable(Node node, int myId) {
        this.node = node;
        this.myId = myId;
    }

    @Override
    public java.lang.Boolean call() {
        Game g = new Game(node.getGame());

        while(g.isGameRunning()) {

            KiPlayer isPlaying;

            if(g.getPlayersTurn() == node.getP1().getId()) {
                isPlaying = node.getP2();
            } else {
                isPlaying = node.getP1();
            }
            g.changePlayer(isPlaying.getId());
            try {
                g.getMyField(isPlaying.getId()).addCard();
            } catch (GameEndException e) {
                g.setPlayerWon(isPlaying.getId());
                g.setGameEnd(true);
                break;
            }
            try {
                isPlaying.yourTurn();

            } catch (LogicException e) {
                throw new RuntimeException(e);
            }

        }

        int id = g.getPlayerWon();

        if(id == myId) {
            node.result(true);
            return true;
        } else {
            node.result(false);
            return false;
        }
    }
}
