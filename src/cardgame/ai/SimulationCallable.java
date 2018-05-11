package cardgame.ai;

import cardgame.classes.GameEndException;
import cardgame.logic.Game;
import cardgame.logic.LogicException;
import java.util.concurrent.Callable;

public class SimulationCallable implements Callable<java.lang.Boolean> {

    private final Node node;

    private final int myId;

    private final int enemyId;

    SimulationCallable(Node node, int myId, int enemyId) {
        this.node = node;
        this.myId = myId;
        this.enemyId = enemyId;
    }

    @Override
    public java.lang.Boolean call() {
        Game game = new Game(node.getGame());

        KiPlayer myPlayer = new TestPlayer(game, myId); // ? RandomPlayer
        KiPlayer enemyPlayer = new TestPlayer(game, enemyId);

        KiPlayer isPlaying;

        if(game.getPlayersTurn() == myPlayer.getId()) {
            isPlaying = myPlayer;
        } else {
            isPlaying = enemyPlayer;
        }

        while(true) {

            try {
                isPlaying.yourTurn();

            } catch (LogicException e) {
                throw new RuntimeException(e);
            }

            if(!game.isGameRunning()) {
                break;
            }

            if(isPlaying == myPlayer) {
                isPlaying = enemyPlayer;
            } else {
                isPlaying = myPlayer;
            }

            game.changePlayer(isPlaying.getId());
            try {
                game.getMyField(isPlaying.getId()).addCard();
            } catch (GameEndException e) {
                game.setPlayerWon(isPlaying.getId());
                game.setGameEnd(true);
                break;
            }

        }

        int id = game.getPlayerWon();

        if(id == myId) {
            node.result(true);
            return true;
        } else {
            node.result(false);
            return false;
        }
    }
}
