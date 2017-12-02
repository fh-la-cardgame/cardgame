package cardgame.ai;

import cardgame.classes.*;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlayer implements KiPlayer{

    private final int id;
    private final Game game;
    private final Playground myPlayground;
    private final Random random;


    public RandomPlayer(Game game,int id) throws LogicException{
        this.game = game;
        this.id = id;
        myPlayground = game.getMyField(id);
        random = new Random();
    }
    @Override
    public void initialize() {

    }

    @Override
    public void yourTurn() throws LogicException{
        playCards();
        attack();
    }

    @Override
    public void endGame(boolean won) {

    }

    @Override
    public int getId() {
        return id;
    }

    private void playCards() throws LogicException{
            List<Card> cardsHand = game.getCardsOnHand(id);

            List<SpecialCard> specialCardsHand = new ArrayList<>();
            List<GameCard> gameCardsHand = new ArrayList<>();
            for(Card c:cardsHand){
                if(c instanceof SpecialCard) specialCardsHand.add((SpecialCard) c);
                else gameCardsHand.add((GameCard) c);
            }

            int zahl = random.nextInt(gameCardsHand.size()+1);
            //Spielt eine Monsterkarte oder keine
            if(myPlayground.canPlayMonsterCard() && zahl != gameCardsHand.size()) game.playCard(id,gameCardsHand.get(zahl));

            zahl = random.nextInt(specialCardsHand.size()+1);
            while(zahl != specialCardsHand.size()  && myPlayground.canPlaySpecialCard()){
                SpecialCard cardPlay = specialCardsHand.get(zahl);
                specialCardsHand.remove(zahl);
                boolean needGameCard = cardPlay.needGameCard();
                    if(needGameCard){
                        if(myPlayground.getCountBattlegroundMonster() != 0) {
                            GameCard gameCard = nextGameCard(myPlayground.getBattlegroundMonster(),zahl);
                            game.playSpecialCard(id,cardPlay,gameCard);
                        }
                    }else game.playSpecialCard(id,cardPlay,null);
            }
    }

    private void attack() throws LogicException{
            if (game.getRound() != 0 && myPlayground.getCountBattlegroundMonster() != 0) {
                Playground enemyPlayground = game.getEnemyField(id);
                GameCard[] myBattleground = myPlayground.getBattlegroundMonster().clone();
                GameCard[] enemyBattleground = enemyPlayground.getBattlegroundMonster().clone();
                int length = myBattleground.length;
                int zahl = random.nextInt(length + 1);
                while (zahl != length) {
                    GameCard attckCard = nextGameCard(myBattleground, zahl);
                    myBattleground[zahl] = null;
                    if (enemyPlayground.getCountBattlegroundMonster() == 0) game.attack(id, attckCard, null);
                    else {
                        zahl = random.nextInt(length);
                        game.attack(id, attckCard, nextGameCard(enemyBattleground, zahl));
                    }
                }
            }
    }

    private GameCard nextGameCard(GameCard[] battleground,int start){
        int count = start;
        while(true){
            if(battleground[count] != null) return battleground[count];
            count = (count+1) % battleground.length;
            if(count == start) throw new IllegalArgumentException();
        }
    }
}