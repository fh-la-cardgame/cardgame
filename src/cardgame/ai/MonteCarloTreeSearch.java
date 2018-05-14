/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ai;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cardgame.classes.*;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

public class MonteCarloTreeSearch {
    /**
     * Zeit nach dem die Suche abgebrochen wird in ms.
     */
    private static int TIME_LIMIT = 1000;
    /*Unser Baum als LinkedList, die alle Knoten enthaelt.*/
    private Deque<Node> path = new LinkedList<>();
    /**
     * Wurzel aus 2
     **/
    private static final double C = Math.sqrt(2);
    /**
     * Anzahl der Iterationen(Child_Nodes) in expand
     **/
    private static final int ITERATIONS = 20;

    private final int myId;

    private final int enemyId;

    public MonteCarloTreeSearch(int myId, int enemyId) {
        this.myId = myId;
        this.enemyId = enemyId;
    }

    public String run(Game game) throws LogicException {
        Node root = new Node(null, false, game, new RandomPlayer(game, myId), new RandomPlayer(game, enemyId));
        long endtime = System.currentTimeMillis() + TIME_LIMIT;
        while (System.currentTimeMillis() < endtime) {
            selection(root);
        }

        int wins = 0;
        String transition = null;
        for(Node n:root.getChildren()){
           if(n.getWins() > wins){
                wins = n.getWins();
                transition = n.getTransition();
           }
        }
        return transition;
    }

    /**
     * Sucht den Knoten mit dem hoechsten UCT Wert aus dem gesamten Baum.
     *
     * @param root Root Knoten.
     * @return Knoten mit dem hoechsten UCT Wert.
     * @throws LogicException 
     */
    public void selection(Node root) throws LogicException {
        Objects.requireNonNull(root);
        Node bestNode = root;
        long simulations = root.getSimulations();
        double bestValue = root.getWins(0) / (double) root.getSimulations() + C * Math.sqrt(Math.log(simulations) / root.getSimulations());
        double tempValue = 0.0;

        LinkedList<Node> stack = new LinkedList<>();
        stack.add(root);

        while (!stack.isEmpty()) {
            Node current = stack.remove();
            bestValue = 0;
            for (Node child : current.getChildren()) {
                tempValue = child.getWins(0) / (double) child.getSimulations() + C * Math.sqrt(Math.log(simulations) / child.getSimulations());
                if (tempValue > bestValue) {
                    bestValue = tempValue;
                    bestNode = child;
                }
            }
            if(bestNode.isLeaf()){
	            break;
            }
            stack.add(bestNode);
        }
        if(bestNode.isTerminal()){
        	if(bestNode.getGame().getPlayerWon() == bestNode.getGame().getPlayersTurn())
        		backPropagation(bestNode, 1, 1);
        	else backPropagation(bestNode, 0, 1);
        }else{
        	expand(bestNode);
        }
    }

    /**
     * Fuegt einen Knoten in den Path ein.
     *
     * @param t Die ausgefuehrte Transition.
     * @throws Exception
     */
    public void expand(Node n) throws LogicException {
        Objects.requireNonNull(n);
        Set<Node> setOfNodes = new HashSet<>();
        //n.getGame().changePlayer(enemyId);
        
        //simulate this transition
        
        Node new_Node = null;
        for (int i = 0; i < ITERATIONS; i++) {
        	try{
        		new_Node = makeTransition(n);
        		setOfNodes.add(new_Node);
        	}catch(GameEndException ex){
                n.setTerminal(true);
                if(n.getGame().getPlayersTurn() == myId){
                    n.getGame().setPlayerWon(myId);
                    backPropagation(n,1,1);
                }else{
                    n.getGame().setPlayerWon(enemyId);
                    backPropagation(n,0,1);
                }
                return;
                //setOfNodes.add(new_Node);
        	}
            
        }
       n.getChildren().addAll(setOfNodes);
       simulation(n);

    }

    /**
     * Spezielle Transition: Spieler toeten. Spieler anzugreifen, ohne zu
     * toeten, ist theoretisch betrachtet nicht immer die beste Wahl, da durch
     * den Angriff, die Evo-Schilder veraendert werden, wodurch der weitere
     * Spielverlauf entscheidend, nachteilig, beeinflusst werden kann.
     * Bedingungen: - Enemy hat keine Karten mehr auf Battleground. - Enemy hat
     * weniger Schilde, als self Karten, die noch nicht angegriffen haben.
     *
     * @param g,                aktuelles Game.
     * @param n,                zugehoerige Node.
     * @param playedCardIndixes
     * @throws LogicException
     */
    private Node spTr(Game g, Node n, List<GameCard> cardsAttack, StringBuilder transition) throws LogicException, GameEndException {

        if (g.getRound() <= 0) {
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


                if (card != null && !g.hasAttacked(self_id, card)) {
                    transition.append("g" + i + "-1");
                    g.attack(self_id, card, null);

                    cardsAttack.add(card);
                }
                i++;
            }
            //TODO:
            //haengt die Postion von p1 und p2 von der Node im Baum ab??
            int enemyId = n.getP1().getId() == g.getPlayersTurn() ? n.getP2().getId() : n.getP1().getId();
            g.changePlayer(enemyId);
            g.getMyField(enemyId).addCard();
            Node finish = new Node(n, true, g, n.getP1(), n.getP2());
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
     * @throws GameEndException 
     * @throws Exception
     */
    public Node makeTransition(Node n) throws LogicException, GameEndException {
        Node newNode = null;
        Game g = new Game(n.getGame());
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
                if (random.nextInt(2) == 0) {    //Zufaellig auswaehlen ob Karte gespielt wird.
                    //--------------
                    temp = random.nextInt(2);    //Zufaellig auswahlen welches Deck die SpecialKarte betrifft.
                    if (temp == 0 && g.getMyField(self_id).getCountBattlegroundMonster() > 0) {
                        int choosen = random.nextInt(g.getMyField(self_id).getCountBattlegroundMonster());
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
                    } else if (temp == 1 && g.getEnemyField(self_id).getCountBattlegroundMonster() > 0) {
                        int choosen = 4 + random.nextInt(g.getEnemyField(self_id).getCountBattlegroundMonster());
                        //Garantieren das es dazwischen keine null gibt
                        for (int k = 0; k < choosen + 1 - 4; k++) {
                            try {
                                if (g.getEnemyField(self_id).getBattlegroundMonster()[k] == null) {            //Korrektur von Myfield zu EnemyField
                                    choosen++;
                                }
                                //Fehler behoben?
                            } catch (Exception ex) {
                                System.out.println(g.getEnemyField(self_id).getBattlegroundMonster());
                                //throw new Exception("Nullpointer");
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
                            g.playCard(self_id, card);    //Referenzen?
                            break;
                        }
                        index++;
                    }
                }

            }
            cardsOnHand.remove(card);
        }
        //Wenn keine Karte gelegt wird: Versuch den Spieler zu toeten, falls moeglich:
        if((newNode = spTr(g, n, CardsAttacked, transition)) != null) return newNode;

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
        int enemyId = n.getP1().getId() == g.getPlayersTurn() ? n.getP2().getId() : n.getP1().getId();
        g.changePlayer(enemyId);
        g.getMyField(enemyId).addCard();
        newNode = new Node(n, false, g, n.getP1(), n.getP2());
        newNode.setTransition(transition.toString());
        return newNode;
    }



    /*simulation() -Methode*/


    /**
     * Laeuft den Baum vom letzten erstellten Knoten zurueck und erneuert seine
     * simulation/win - counts
     *
     * @param startNode Knoten, von dem man startet.
     * @param wins Anzahl der Gewinne des Spielers.
     * @param simulations Anzahl der Simulationen.
     */
    public void backPropagation(Node startNode, int wins, int simulations) {
        
        int playersTurn;
        if(startNode.isTerminal()) {
            playersTurn = startNode.getGame().getPlayersTurn();
        }
        
        else {
        Node childNode = startNode.getChildren().getFirst();                       
        playersTurn = childNode.getGame().getPlayersTurn();                /*Spieler mit dem man vergleicht*/
        }
        
        while (startNode != null) {
            if(playersTurn == startNode.getGame().getPlayersTurn())
            startNode.addWins(wins);
            else {
                startNode.addWins(simulations - wins);               
            }
            startNode.addSimulations(simulations);
            startNode = startNode.getParent();
        }
    }

    public void simulation(Node n) {

        //Map<Node, Future<Boolean>> map = new HashMap<>();
        List<Future<Boolean>> list = new LinkedList<>();

        ExecutorService ex = Executors.newCachedThreadPool();

        for (Node node : n.getChildren()) {
            list.add(ex.submit(new SimulationCallable(node, myId, enemyId)));
            //map.put(node, ex.submit(new SimulationCallable(node, node.getGame().getPlayersTurn())));
        }

        /*
        try {
            if(!ex.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new RuntimeException("Takes to long to execute");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } */

        int wins = 0;
        int simulations = list.size();

        for (Future<Boolean> f : list) {
            try {
                if (f.get()) {
                    wins++;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ex.shutdownNow();

        //TODO BackPropagation

        /*
        for(Map.Entry<Node, Future<Boolean>> entry: map.entrySet()) {
            try {
                entry.getKey().result(entry.getValue().get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        */
        
        backPropagation(n, wins, simulations);

    }
}
