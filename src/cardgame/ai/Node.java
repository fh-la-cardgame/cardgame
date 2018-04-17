/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ai;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Node {
    
    private final Map<Transition, Node> children;
    private final Map<Integer, Integer> wins;
    private int simulations = 0;
    private Node parent;
    private boolean terminal;
    
    
    public Node(Node parent, boolean terminal){
        this.parent = parent;
        this.terminal = terminal;
        children = new HashMap<>();
        wins = new HashMap<>();
               
    }
    
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public long getSimulations(){
        return simulations;
    }
    
    public long getWins(int player) {
        Integer w = wins.get(player);
        if (w==null) {
            return 0;
        }
        else {
            return w;
        }
    }
    
    public void result(int winner) {
        simulations++;
        Integer w = wins.get(winner);
        if (w==null) {
            wins.put(winner, 1);
        }
        else {
            wins.put(winner, w + 1);
        }
    }
        
    /**
     *  Gibt die Map aller Transitions und das Kind dieses Knotens zurueck.
     * @return
     */
    public Map<Transition, Node> getTransitionsAndNodes() {
        return children;
    }
    
    /**
     *  Gibt eine Collection aller Kinder dieses Knotes zurueck.
     * @return
     */
    public Collection<Node> getChildren() {
        return children.values();       
    }
    
    /**
     *  Gibt den Kindsknoten, welcher durch die Transition erreicht wird zurueck.
     * @param transition
     * @return
     */
    public Node getNode(Transition transition){
        return children.get(transition);
    }

    /**
     *  Ein Knoten ist terminal, wenn es kein Kind zu erkunden gibt.
     * @return
     */

    public boolean isTerminal(){
        return this.terminal;
    }
    
    public void setTerminal(boolean terminal){
        this.terminal = terminal;
    }
    
    /**
     *  Fuegt ein Kind zum Knoten hinzu und assoziert dieses mit der
     *  uebergebenen Transition.
     * @param transition Die Transition, die zum Kind fuehrt.
     * @param child Das Kind.
     */
    public void addChildNote(Transition transition, Node child){
       getTransitionsAndNodes().put(transition, child);
   }
   
    /**
     *  evtl. unnoetig, das UCT-Methode dafuer da ist.
     *  Holt den Wert des gewaehlten Spielers.
     *  Der Knoten, mit dem groesseren Wert wird ausgewaehlt.
     * @param player
     * @return
     */
    /*public abstract double value(int player);*/
    
    public Node getParent(){
        return parent;
    }
    
    /**
     *  Macht diesen Knoten zur Wurzelknoten, indem er die Referent des Elternknotens loescht.
     */
    public void makeRoot(){
        this.parent = null;
    }
    
    
    
    
}
