package cardgame.ai;

import cardgame.classes.*;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomPlayer implements KiPlayer {

    private final int id;
    private final Game game;
    private final Playground myPlayground;
    private final Random random;


    public RandomPlayer(Game game, int id) throws LogicException {
        this.game = game;
        this.id = id;
        myPlayground = game.getMyField(id);
        random = new Random();
    }

    public RandomPlayer(Game game, int id, long seed) throws LogicException {
        this.game = game;
        this.id = id;
        myPlayground = game.getMyField(id);
        random = new Random(seed);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void yourTurn() throws LogicException {
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

    private void playCards() throws LogicException {
        List<Card> cardsHand = game.getCardsOnHand(id);

        List<SpecialCard> specialCardsHand = new ArrayList<>();
        List<GameCard> gameCardsHand = new ArrayList<>();
        for (Card c : cardsHand) {
            if (c instanceof SpecialCard) specialCardsHand.add((SpecialCard) c);
            else gameCardsHand.add((GameCard) c);
        }

        GameCard playGameCard = null;
        int zeitpunkt = 0;
        int zahl = nextInt(gameCardsHand.size() + 1);
        //Gibt an ob eine Monsterkarte gespielt wird oder nicht
        if (myPlayground.canPlayMonsterCard() && zahl != gameCardsHand.size()) {
            playGameCard = gameCardsHand.get(zahl);
            zeitpunkt = nextInt(specialCardsHand.size() + 1);
            //Die GameCard wird entweder dann gespielt wenn Zeitpunkt 0 oder sicher am Ende !
            if (zeitpunkt == 0) {
                game.playCard(id, playGameCard);
                playGameCard = null;
            }
        }

        zahl = nextInt(specialCardsHand.size() + 1);
        while (zahl != specialCardsHand.size() && myPlayground.canPlaySpecialCard()) {
            SpecialCard cardPlay = specialCardsHand.get(zahl);
            specialCardsHand.remove(zahl);
            boolean needGameCard = cardPlay.needGameCard();
            if (needGameCard) {
                zahl = nextInt(2);
                if (zahl == 0 && myPlayground.getCountBattlegroundMonster() != 0) {
                    zahl = nextInt(myPlayground.getBattlegroundMonster().length);
                    GameCard gameCard = nextGameCard(myPlayground.getBattlegroundMonster(), zahl);
                    game.playSpecialCard(id, cardPlay, gameCard);
                } else if (game.getEnemyField(id).getCountBattlegroundMonster() != 0) {
                    zahl = nextInt(myPlayground.getBattlegroundMonster().length);
                    GameCard gameCard = nextGameCard(game.getEnemyField(id).getBattlegroundMonster(), zahl);
                    game.playSpecialCard(id, cardPlay, gameCard);
                }
            } else game.playSpecialCard(id, cardPlay, null);
            zahl = nextInt(specialCardsHand.size() + 1);
            if (playGameCard != null) {
                zeitpunkt--;
                if (zeitpunkt == 0) {
                    game.playCard(id, playGameCard);
                    playGameCard = null;
                }
            }
        }
        //Stellt sicher das die GameCard sicher gespielt wird
        if (playGameCard != null) game.playCard(id, playGameCard);
    }

    private void attack() throws LogicException {
        if (game.getRound() != 0 && myPlayground.getCountBattlegroundMonster() != 0) {
            Playground enemyPlayground = game.getEnemyField(id);
            GameCard[] myBattleground = myPlayground.getBattlegroundMonster();
            GameCard[] enemyBattleground = enemyPlayground.getBattlegroundMonster();
            int length = myBattleground.length;
            int zahl = nextInt(length + 1);
            while (zahl != length && myPlayground.getCountBattlegroundMonster() != 0) {
                GameCard attckCard = nextGameCard(myBattleground, zahl);
                if (attckCard == null) return;
                if (!game.hasAttacked(id, attckCard))
                    if (enemyPlayground.getCountBattlegroundMonster() == 0) {
                        game.attack(id, attckCard, null);
                        if (!game.isGameRunning()) return;
//                            System.out.println("Attack Player");
                    } else {
                        zahl = nextInt(length);
//                            System.out.println(" Attack Card");
                        game.attack(id, attckCard, nextGameCard(enemyBattleground, zahl));
                    }
                zahl = nextInt(length + 1);
            }
        }
    }

    private int nextInt(int bound) {
        return random.nextInt(bound);
    }

    private GameCard nextGameCard(GameCard[] battleground, int start) {
        int count = start;
        do {
            if (battleground[count] != null) return battleground[count];
            count = (count + 1) % battleground.length;
        } while (count != start);

        return null;
    }

}
