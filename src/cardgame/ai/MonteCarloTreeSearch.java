/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ai;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cardgame.classes.Card;
import cardgame.classes.GameCard;
import cardgame.classes.Playground;
import cardgame.classes.SpecialCard;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

public class MonteCarloTreeSearch {

    /*Unser Baum als LinkedList, die alle Knoten enthaelt.*/
    private Deque<Node> path = new LinkedList<>();
    private static final double C = Math.sqrt(2);

    /**
     * Sucht den Knoten mit dem hoechsten UCT Wert aus dem gesamten Baum.
     *
     * @param root Root Knoten.
     * @return Knoten mit dem hoechsten UCT Wert.
     */
    public Node selection(Node root) {
        Objects.requireNonNull(root);
        Node bestNode = root;
        long simulations = root.getSimulations();
        double bestValue = root.getWins(0) / (double) root.getSimulations() + C * Math.sqrt(Math.log(simulations) / root.getSimulations());
        double tempValue = 0.0;

        LinkedList<Node> stack = new LinkedList<>();
        stack.add(root);

        while (!stack.isEmpty()) {
            Node current = stack.remove();
            for (Node child : current.getChildren()) {
                if (!child.isTerminal()) {
                    stack.add(child);
                    tempValue = child.getWins(0) / (double) child.getSimulations() + C * Math.sqrt(Math.log(simulations) / child.getSimulations());
                    if (tempValue > bestValue) {
                        bestValue = tempValue;
                        bestNode = child;
                    }
                }
            }
        }
        return bestNode;
    }

    /**
     * Fuegt einen Knoten in den Path ein.
     *
     * @param t Die ausgefuehrte Transition.
     * @throws Exception
     */
    public void expand(Node n) throws Exception {
        Objects.requireNonNull(n);
        //simulate this transition
        Node new_Node = makeTransition(n);
        //add Node to Path
        path.addLast(new_Node);
    }

    /**
     * Spezielle Transition: Spieler toeten. Spieler anzugreifen, ohne zu
     * toeten, ist theoretisch betrachtet nicht immer die beste Wahl, da durch
     * den Angriff, die Evo-Schilder veraendert werden, wodurch der weitere
     * Spielverlauf entscheidend, nachteilig, beeinflusst werden kann.
     * Bedingungen: - Enemy hat keine Karten mehr auf Battleground. - Enemy hat
     * weniger Schilde, als self Karten, die noch nicht angegriffen haben.
     *
     * @param g, aktuelles Game.
     * @param n, zugehoerige Node.
     * @param playedCardIndixes
     * @throws LogicException
     */
    private Node spTr(Game g, Node n, List<GameCard> cardsAttack, StringBuilder transition) throws LogicException {

        if (g.getRound() == -1) {
            return null;
        }
        int self_id = n.getP1().getId() == n.getGame().getPlayersTurn() ? n.getP1().getId() : n.getP2().getId();

        if (g.getEnemyField(self_id).getCountBattlegroundMonster() == 0
                && g.getMyField(self_id).getCountBattlegroundMonster() - cardsAttack.size() >= g.getEnemyField(self_id).getPlayer().getShields().getCurrentShields()) {
            int i = 0;
            GameCard card = null;
            while (g.getMyField(n.getP1().getId()).getPlayer().getShields().getCurrentShields() > 0
                    && g.getMyField(n.getP2().getId()).getPlayer().getShields().getCurrentShields() > 0) {
                card = g.getMyField(self_id).getBattlegroundMonster()[i];
                final GameCard tempCard = card;
                //FEHLER!
                if (card != null && !cardsAttack.stream().anyMatch(gc -> gc == tempCard)) {
                    transition.append("g" + i + "-1");
                    g.attack(self_id, card, null);

                    cardsAttack.add(card);
                }
                i++;
            }
            Node finish = new Node(n, true, g);
            finish.setTransition(transition.toString());
            return finish;
        } else {
            return null;
        }
    }

    /**
     * Coding of Transition: Karte legen: ng[0..4] (new gamecard). Specialcard
     * legen: ns[0..4][0..7]+ (new specialcard)(0..3: self, 4..7: enemy) .
     * Spieler angreifen: g[0..3]-1 (-1 for the player) Angreifen: g[0..3][0..3]
     *
     * @param n Node n(alte Node)
     * @return Node (neue, aus alter resultierende Node)
     * @throws Exception
     */
    public Node makeTransition(Node n) throws Exception {
        Node newNode = null;
        Game g = n.getGame();
        Random random = new Random();
        StringBuilder transition = new StringBuilder();
        int temp = 0;
        boolean playedGameCard = false;
        boolean firstRound = g.getRound() == -1 || g.getRound() == 0 ? true : false;
        List<GameCard> CardsAttacked = new ArrayList<>();
        int self_id = n.getP1().getId() == n.getGame().getPlayersTurn() ? n.getP1().getId() : n.getP2().getId();
        System.out.println("ID: " + self_id);
        /**
         * Transition: Neue Karte legen(ng) oder Specialcard spielen(ns):
         * Bedingung ng: - Platz auf eigenem Battleground. - Man hat auf der
         * Hand eine GameCard/Specialcard. - nur eine karte darf gelegt werden
         */

        //Alle Karten in eine Liste um zufaellige Reihenfolge zu gewaehleisten:
        List<Card> cardsOnHand = new ArrayList<>(g.getMyField(self_id).getCardsOnHand());
        while (cardsOnHand.size() > 0) {
            Card card = cardsOnHand.get(random.nextInt(cardsOnHand.size()));
            //Wenn Karte eine Specialkarte ist:
            if (card instanceof SpecialCard && g.getMyField(self_id).getCountBattlegroundSpecials() < Playground.ROW) {
                if (random.nextInt(2) == 0) {	//Zufaellig auswaehlen ob Karte gespielt wird.
                    //--------------
                    temp = random.nextInt(2);	//Zufaellig auswahlen welches Deck die SpecialKarte betrifft.
                    if (temp == 0 && n.getGame().getMyField(self_id).getCountBattlegroundMonster() > 0) {
                        int choosen = 0 + random.nextInt(n.getGame().getMyField(self_id).getCountBattlegroundMonster());
                        //Garantieren das es dazwischen keine null gibt: Keine Gleichverteilung!
                        for (int k = 0; k < choosen + 1; k++) {
                            if (g.getMyField(self_id).getBattlegroundMonster()[k] == null) {
                                choosen++;
                            }
                        }
                        //richtigen Index zuweisen:
                        int index = 0;
                        for (Card c : g.getMyField(self_id).getCardsOnHand()) {
                            if (c == card) {//Referenzen?
                                transition.append("ns" + index + "|" + choosen);
                                g.playSpecialCard(self_id, index, choosen);
                                //Spezielle Transition: Spieler toeten:
                                if ((newNode = spTr(g, n, CardsAttacked, transition)) != null) {
                                    return newNode;
                                }
                                break;
                            }
                            index++;
                        }
                        //Specialcard wirkt auf Gegnerfeld(temp == 1):
                    } else if (temp == 1 && n.getGame().getEnemyField(self_id).getCountBattlegroundMonster() > 0) {
                        int choosen = 4 + random.nextInt(n.getGame().getEnemyField(self_id).getCountBattlegroundMonster());
                        //Garantieren das es dazwischen keine null gibt
                        for (int k = 0; k < choosen + 1 - 4; k++) {
                            try {
                                if (g.getEnemyField(self_id).getBattlegroundMonster()[k] == null) {			//Korrektur von Myfield zu EnemyField
                                    choosen++;
                                }
                                //Fehler behoben?
                            } catch (Exception ex) {
                                System.out.println(g.getEnemyField(self_id).getBattlegroundMonster());
                                throw new Exception("Nullpointer");
                            }
                        }
                        //richtigen Index zuweisen: Wichtig immer aktuellen Index verwenden!
                        int index = 0;
                        for (Card c : g.getMyField(self_id).getCardsOnHand()) {
                            if (c == card) {//Referenzen?
                                transition.append("ns" + index + "|" + choosen);
                                g.playSpecialCard(self_id, index, choosen);
                                //Spezielle Transition: Spieler toeten:
                                if ((newNode = spTr(g, n, CardsAttacked, transition)) != null) {
                                    return newNode;
                                }
                                break;
                            }
                            index++;
                        }

                    }
                    //--------------
                }
                cardsOnHand.remove(card);
                //Wenn Karte eine GameKarte ist:
            } else if (card instanceof GameCard && g.getMyField(self_id).getCountBattlegroundMonster() < Playground.ROW && !playedGameCard) {
                if (random.nextInt(2) == 0) {//ob Karte gespielt wird
                    //g.playCard(self_id, card);
                    int index = 0;
                    for (Card c : g.getMyField(self_id).getCardsOnHand()) {
                        if (c == card) {//Referenzvergleich!
                            transition.append("ng" + index);

                            playedGameCard = true;
                            //Spezielle Transition: Spieler toeten:
                            if ((newNode = spTr(g, n, CardsAttacked, transition)) != null) {
                                return newNode;
                            }
                            g.playCard(self_id, card); 	//Referenzen?
                            break;
                        }
                        index++;
                    }
                }

            }
            cardsOnHand.remove(card);
        }
        //Wenn keine Karte gelegt wird: Versuch den Spieler zu toeten, falls moeglich:
        spTr(g, n, CardsAttacked, transition);

        /**
         * Transition: Mit GameCard angreifen g[0..3][0..3] oder Spieler
         * angreifen g[0..3]-1: Bedingungen(g[0..3][0..3]): - Nicht erste Runde
         * (!= -1). - Enemy hat Karten. - Self hat Karten, die noch nicht
         * angegriffen haben. Bedingungen(g[0..3]-1): - Nicht erste Runde (!=
         * -1). - Enemy hat keine Karten mehr auf Battleground. - Self hat
         * Karten, die noch nicht angegriffen haben. UnterBedingung: Spieler
         * toeten - Enemy hat keine Karten mehr auf Battleground. - Enemy hat
         * weniger Schilder, als Self noch Karten zum Angreifen.
         */
        if (!firstRound) {
            //Karten sammlen:
            List<GameCard> cards = Stream.of(g.getMyField(self_id).getBattlegroundMonster()).filter(gc -> gc != null).collect(Collectors.toList());
            //Auswahlen ob Karte spielt:
            cards = cards.stream().filter(gc -> random.nextInt(2) == 0).collect(Collectors.toList());

            while (cards.size() > 0) {
                GameCard current;
                do {
                    current = cards.get(random.nextInt(cards.size()));//nulls removen?
                } while (current == null);
                if (g.getEnemyField(self_id).getCountBattlegroundMonster() > 0) {//GameCards angreifen:
                    //Karten sammeln:
                    List<GameCard> enemyCards = Stream.of(g.getEnemyField(self_id).getBattlegroundMonster()).filter(gc -> gc != null).collect(Collectors.toList());
                    //Karten auswaehlen:
                    GameCard currentEnemy = enemyCards.get(random.nextInt(enemyCards.size()));
                    //aktuellen Index eigener Karte bestimmen:
                    int currentIndex = 0;
                    int currentIndexEnemy = 0;
                    boolean done = false;
                    while (currentIndex < g.getMyField(self_id).getBattlegroundMonster().length) {
                        if (g.getMyField(self_id).getBattlegroundMonster()[currentIndex] != null
                                && g.getMyField(self_id).getBattlegroundMonster()[currentIndex] == current) {
                            //Index von EnemyCard bestimmen:
                            while (currentIndexEnemy < g.getEnemyField(self_id).getBattlegroundMonster().length) {
                                if (g.getEnemyField(self_id).getBattlegroundMonster()[currentIndexEnemy] != null
                                        && g.getEnemyField(self_id).getBattlegroundMonster()[currentIndexEnemy] == currentEnemy) {
                                    transition.append("g" + currentIndex + currentIndexEnemy);
                                    g.attack(self_id, currentIndex, currentIndexEnemy);
                                    CardsAttacked.add(current); //unnoetig?
                                    done = true;
                                    if ((newNode = spTr(g, n, CardsAttacked, transition)) != null) {
                                        return newNode;
                                    }
                                    break;
                                }

                                currentIndexEnemy++;
                            }
                        }
                        if (done) {
                            break;
                        }
                        currentIndex++;
                    }
                } else {//Spieler angreifen:
                    //aktuellen index bestimmen:
                    int currentIndex = 0;
                    while (currentIndex < g.getMyField(self_id).getBattlegroundMonster().length) {
                        if (g.getMyField(self_id).getBattlegroundMonster()[currentIndex] != null
                                && g.getMyField(self_id).getBattlegroundMonster()[currentIndex] == current) {
                            transition.append("g" + currentIndex + "-1");
                            g.attack(self_id, current, null);
                            CardsAttacked.add(current);
                            if ((newNode = spTr(g, n, CardsAttacked, transition)) != null) {
                                return newNode;
                            }
                            break;
                        }
                        currentIndex++;
                    }

                }
                ListIterator<GameCard> it = cards.listIterator(0);
                while (it.hasNext()) {
                    if (it.next() == current) {
                        it.remove();
                    }
                }
//	    		int remove = 0;
//	    		for(GameCard gc : cards){
//	    			if(gc == current){
//	    				cards.remove(remove);
//	    			}
//	    			remove++;
//	    		}
                //cards.remove(current);
            }
        }
        System.out.println("Transition:" + transition.toString());
        newNode = new Node(n, false, g, n.getP1(), n.getP2());
        newNode.setTransition(transition.toString());
        return newNode;
    }

    
    
    /*simulation() -Methode*/
    

    /**
     * Laeuft den Baum vom letzten erstellten Knoten zurueck und erneuert seine
     * simulation/win - counts
     *
     * @param path Baum
     * @param winner Gewinner?(true = gewonnen, false = verloren).
     */
    public void backPropagation(Deque<Node> path, boolean winner) {

        Node lastNode = path.getLast();
        lastNode.result(winner);

        while (lastNode.getParent() != null) {
            lastNode = lastNode.getParent();
            lastNode.result(winner);
        }
    }
}
