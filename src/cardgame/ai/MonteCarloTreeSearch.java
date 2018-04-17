/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ai;

import java.util.Map;



public class MonteCarloTreeSearch {
    
    private static final double C = Math.sqrt(2);
    
    public Map.Entry<Transition, Node> selectNonTerminalChildOf(Node node, int player){
        double v = Double.NEGATIVE_INFINITY;
        Map.Entry<Transition, Node> best = null;
         for (Map.Entry<Transition, Node> e : node.getTransitionsAndNodes().entrySet()) {
         if (!e.getValue().isTerminal()) {
            	// w/n + C * Math.sqrt(ln(n(p)) / n)
            	// TODO : add a random hint to avoid ex-aequo
                double value = (e.getValue().getSimulations() == 0 ? 0 : (e.getValue().getWins(player) / e.getValue().getSimulations())
                        + C * Math.sqrt(Math.log(node.getSimulations()) / e.getValue().getSimulations()));
                if (value > v) {
                    v = value;
                    best = (Map.Entry<Transition, Node>) e;
                }
            }
        }
        return best;
    }
    
    
    /*selection() -Methode*/
    
    /*expansion() -Methode*/
    
    /*simulation() -Methode*/
    
    /*backPropagation() - Methode*/
    
    
    
    
    
    
    
    }
    
   