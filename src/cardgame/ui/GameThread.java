package cardgame.ui;

import cardgame.ai.KiPlayer;
import cardgame.ai.TestPlayer;
import cardgame.classes.GameEndException;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

public class GameThread extends Thread {

    private Game game;

    public GameThread(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        try {
            KiPlayer p1 = new TestPlayer(game, 1);
            KiPlayer p2 = new TestPlayer(game, 2);
            int k = 0;
            while (game.isGameRunning()) {
                game.changePlayer(p1.getId());
                try {
                    game.getMyField(p1.getId()).addCard();
                } catch (GameEndException e) {
                    game.setPlayerWon(p2.getId());
                    game.setGameEnd(true);
                    break;
                }
                try {
                    p1.yourTurn();
                    if (k == 0)
                        System.out.println(game.getCardsOnHand(p1.getId()).size() + " " + game.getCardsOnHand(p2.getId()).size());

                } catch (LogicException e) {
                    System.out.println(e);
                }
                if (!game.isGameRunning()) {
                    break;
                }
                game.changePlayer(p2.getId());
                try {
                    game.getMyField(p2.getId()).addCard();
                } catch (GameEndException e) {
                    game.setPlayerWon(p1.getId());
                    game.setGameEnd(true);
                    break;
                }
                try {
                    p2.yourTurn();
                } catch (LogicException e) {
                    System.out.println(e);
                }
                k++;
            }
            System.out.println("Spieler " + game.getMyField(game.getPlayerWon()).getPlayer().getName() + " hat gewonnen");
        } catch (LogicException ex) {
            System.out.println(ex);
        }
    }
}