/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ai;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;



public class MonteCarloTreeSearch {
    
	private Deque<Node> path = new LinkedList<>();
    private static final double C = Math.sqrt(2);
    
    /**Sucht den Knoten mit dem hoechsten UCT Wert aus dem gesamten Baum.
     * 
     * @param root Root Knoten.
     * @return Knoten mit dem hoechsten UCT Wert.
     */
    public Node selection(Node root){
    	Objects.requireNonNull(root);
    	Node bestNode = root;
    	long simulations = root.getSimulations();
    	double bestValue = root.getWins(0) / (double)root.getSimulations() + C * Math.sqrt(Math.log(simulations) / root.getSimulations());
    	double tempValue = 0.0;
    	
    	LinkedList<Node> stack = new LinkedList<>();
    	stack.add(root);
    	
    	while(!stack.isEmpty()){
    		Node current = stack.remove();
    		for(Node child : current.getChildren()){
    			stack.add(child);
    			tempValue = child.getWins(0) / (double)child.getSimulations() + C * Math.sqrt(Math.log(simulations) / child.getSimulations());
    			if(tempValue > bestValue){
    				bestValue = tempValue;
    				bestNode = child;
    			}
    		}
    	}
    	return bestNode;
    }
    
    /*expansion() -Methode*/
    
    /*simulation() -Methode*/
    
    /*backPropagation() - Methode*/
    
    
    
    
    
    
    
    }
    
   