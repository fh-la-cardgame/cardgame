/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cardgame.classes.Card;
import cardgame.classes.GameCard;
import cardgame.logic.Game;
import java.util.LinkedList;

public class Node {

    private Game game;
    private KiPlayer p1;
    private KiPlayer p2;
    private String transition;
    private LinkedList<Node> children;
    private int wins = 0;
    private int simulations = 0;
    private Node parent;
    private boolean terminal;

    public Node(Node parent, boolean terminal, Game game) {
        this.parent = parent;
        this.terminal = terminal;
        this.game = game;
    }

    public Node(Node parent, boolean terminal, Game game, KiPlayer p1, KiPlayer p2) {
        this.parent = parent;
        this.terminal = terminal;
        children = new LinkedList<>();
        this.game = game;
        this.p1 = p1;
        this.p2 = p2;
    }


    public KiPlayer getP1() {
        return p1;
    }

    public void setP1(KiPlayer p1) {
        this.p1 = p1;
    }

    public KiPlayer getP2() {
        return p2;
    }

    public void setP2(KiPlayer p2) {
        this.p2 = p2;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public String getTransition() {
        return transition;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public long getSimulations() {
        return simulations;
    }

    /**
     * Return wins. Can return wins for both players. Node stores only the wins
     * of the player who takes action in this node.
     *
     * @param player 0 for current player of this node, anything else.
     * @return the wins of this node.
     */
    public long getWins(int player) {
        if (player == 0) {
            return wins;
        }
        return (simulations - wins);

    }
    
    public void addWins(int winner){
        wins += winner;
    }
    
    public void addSimulations(int simul){
        simulations += simul;
    }
    
    /**
     * ARGUMENT WINNER NOCH DEFINIEREN!
     *
     * @param winner bei true, hat der Knoten gewonnen, bei false nicht
     *
     */
    public void result(boolean winner) {
        simulations++;
        if (winner == true) {
            wins++;
        }
    }

    /**
     * Gibt die Map aller Transitions und das Kind dieses Knotens zurueck.
     *
     * @return
     */
//    public Map<Transition, Node> getTransitionsAndNodes() {
//        return children;
//    }
    /**
     * Gibt eine Collection aller Kinder dieses Knotes zurueck.
     *
     * @return
     */
    public LinkedList<Node> getChildren() {
        return children;
    }

    /**
     * Gibt den Kindsknoten, welcher durch die Transition erreicht wird zurueck.
     *
     * @param transition
     * @return
     */
//    public Node getNode(Transition transition){
//        return children.get(transition);
//    }
    /**
     * Ein Knoten ist terminal, wenn es kein Kind zu erkunden gibt.
     *
     * @return
     */
    public boolean isTerminal() {
        return this.terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    /**
     * Fuegt ein Kind zum Knoten hinzu und assoziert dieses mit der uebergebenen
     * Transition.
     *
     * @param transition Die Transition, die zum Kind fuehrt.
     * @param child Das Kind.
     */
//    public void addChildNote(Transition transition, Node child){
//       getTransitionsAndNodes().put(transition, child);
//   }
    /**
     * evtl. unnoetig, das UCT-Methode dafuer da ist. Holt den Wert des
     * gewaehlten Spielers. Der Knoten, mit dem groesseren Wert wird
     * ausgewaehlt.
     *
     * @param player
     * @return
     */
    /*public abstract double value(int player);*/
    public Node getParent() {
        return parent;
    }

    public int getWins() {
        return wins;
    }
    /**
     * Macht diesen Knoten zur Wurzelknoten, indem er die Referenz des
     * Elternknotens loescht.
     */
    public void makeRoot() {
        this.parent = null;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transition == null) ? 0 : transition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (transition == null) {
			if (other.transition != null)
				return false;
		} else if (!transition.equals(other.transition))
			return false;
		return true;
	}
    
    

}
