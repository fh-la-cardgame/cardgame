package cardgame.ai;

import cardgame.logic.Game;
import cardgame.logic.LogicException;
import javafx.beans.property.IntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class MCTSPlayer implements KiPlayer {


    private final MonteCarloTreeSearch mcts;

    private final int id;

    private final Game game;

    public MCTSPlayer(int myId, int enemyId, Game game) {
        mcts = new MonteCarloTreeSearch(myId, enemyId);
        this.id = myId;
        this.game = game;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void yourTurn() throws LogicException {
        String best = mcts.run(new Game(game));
        List<String> commands = new ArrayList<>();
        StringBuilder build = new StringBuilder();
        boolean lastDigit = false;
        for (int i = 0; i < best.length(); i++) {
            char act = best.charAt(i);
            if (Character.isDigit(act) || act == '-') {
                lastDigit = true;
                build.append(act);
            } else if (act != '|') {
                if (lastDigit) {
                    lastDigit = false;
                    commands.add(build.toString());
                    build.setLength(0);
                }
                build.append(act);
            }
        }
        commands.add(build.toString());
        for (String command : commands) {
            if (command.startsWith("ng")) {
                game.playCard(id, charToInt(command.charAt(2)));
            } else if (command.startsWith("ns")) {
                int specialCardPlay = charToInt(command.charAt(2));
                int specialCardForEffect = -1;
                if (command.length() == 4) specialCardForEffect = charToInt(command.charAt(3));
                game.playSpecialCard(id,specialCardPlay,specialCardForEffect);
            }else if(command.startsWith("g")){
                int attacker = charToInt(command.charAt(1));
                if(command.length() == 4){
                    game.attack(id,attacker,Integer.parseInt(command.substring(2)));
                }else {
                    int enemy = charToInt(command.charAt(2));
                    game.attack(id, attacker, enemy);
                }
            }
        }

    }

    @Override
    public void endGame(boolean won) {

    }

    @Override
    public int getId() {
        return id;
    }


    private int charToInt(char c) {
        return c - '0';
    }
}
