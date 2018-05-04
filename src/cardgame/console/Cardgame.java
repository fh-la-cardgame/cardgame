/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.console;

import cardgame.ai.KiPlayer;
import cardgame.ai.MonteCarloTreeSearch;
import cardgame.ai.Node;
import cardgame.ai.TestPlayerProtokoll;
import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.GameCard;
import cardgame.classes.Player;
import cardgame.db.DbCard;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author HortensiaX
 */
public class Cardgame {


    
    public static void main(String[] args) throws Exception {
        /*
         * Exemplarische Ausfuehrung von "getDeck(String deckName)"
         */
       // DbCard d = new DbCard();
//        d.showAmountOfCards("civitas diaboli");
//        System.out.println(d.getGameCard("Astrator"));
//        d.updateEffect(c_eid, gid, eid, shield, evo_shield)
        
       // System.out.println(d.getGameCard("Marbas"));
//        d.insert_Card_Effect_Shields(36, 4, -1);
//        d.updateShields(3, 4);
      //  System.out.println(d.getGameCard("Marbas"));
//      System.out.println(d.insert_Card_Effect_EvoShields(76, 24, 3));
//        Der Deckname wird nun auch case insensitive eingelesen:
//        List<Card> list = d.getDeck("civitas diaboli");
//        System.out.println(list);
//        System.out.println( d.insert_Card_Effect_EvoShields(22, 3, 0));
//        d.updateEvoShield(3, 8);
      //  d.add_change_Effects(18, 17, -1, null);
    	
        DbCard db = new DbCard();
		List<Card> c1 = db.getDeck("civitas diaboli");
		List<Card> c2 = db.getDeck("Engel");

		Deck d1 = new Deck(1,"Alpha",c1);
		Deck d2 = new Deck(2, "Beta", c2);
		
		Node newNode = null;
		Game g = new Game(new Player(1, "Spieler1"), new Player(2, "Spieler2"), new Deck(d1), new Deck(d2), true);
		KiPlayer p1 = new TestPlayerProtokoll(g, 1);
		KiPlayer p2 = new TestPlayerProtokoll(g, 2);
		MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
		newNode = mcts.makeTransition(new Node(null, false, g, p1, p2));
		log(g, newNode);
//		try{
			while(g.isGameRunning() && g.getMyField(newNode.getP1().getId()).getPlayer().getShields().getCurrentShields() > 0 && 
					g.getEnemyField(newNode.getP1().getId()).getPlayer().getShields().getCurrentShields() > 0){
				System.out.println("Shield:"+g.getEnemyField(newNode.getP1().getId()).getPlayer().getShields().getCurrentShields());
				System.out.println("Shield:"+g.getMyField(newNode.getP1().getId()).getPlayer().getShields().getCurrentShields());
				System.out.println("CardsOnDeck:"+g.getMyField(newNode.getP1().getId()).getDeck().getCards().size());
				System.out.println("CardsOnDeck:"+g.getEnemyField(newNode.getP1().getId()).getDeck().getCards().size());
				Thread.sleep(100);
				
				g.changePlayer(p2.getId());
				g.getMyField(p2.getId()).addCard();
				newNode.setGame(g);
				newNode = mcts.makeTransition(newNode);
				log(g, newNode);
				
				if(g.isGameRunning() && g.getMyField(newNode.getP1().getId()).getPlayer().getShields().getCurrentShields() == 0 || 
						g.getEnemyField(newNode.getP1().getId()).getPlayer().getShields().getCurrentShields() == 0){
						break;
					}
				
				g.changePlayer(p1.getId());
				g.getMyField(p1.getId()).addCard();
				newNode.setGame(g);
				newNode = mcts.makeTransition(newNode);
				log(g, newNode);
			}
//		}catch(Exception ex){
//			System.out.println(newNode.getTransition());
//			throw new Exception();
//		}
		
		System.out.println("Ende");
		System.out.println(g.getMyField(p1.getId()).getPlayer().getShields().getCurrentShields());
		System.out.println(g.getEnemyField(p1.getId()).getPlayer().getShields().getCurrentShields());
                
   }
    
    public static void log(Game g, Node n) throws IOException, LogicException{
    	if(false){
    	BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\David\\Documents\\GameLog.txt", true));
    	writer.write("Battleground p1:");
    	writer.newLine();
    	for(GameCard c : g.getMyField(n.getP1().getId()).getBattlegroundMonster()){
    		if(c != null){
    		writer.write(c.getName()+" ");
    		writer.write(c.getAtk()+" ");
    		writer.write(c.getShields().getCurrentShields()+" ");
    		writer.newLine();
    		}
    	}
    	writer.flush();
    	writer.write("--------------------------------------------------------------------------");
    	writer.newLine();
    	writer.flush();
    	writer.write("Battleground p2:");
    	writer.newLine();
    	for(GameCard c : g.getMyField(n.getP2().getId()).getBattlegroundMonster()){
    		if(c != null){
    		writer.write(c.getName()+" ");
    		writer.write(c.getAtk()+" ");
    		writer.write(c.getShields().getCurrentShields()+" ");
    		writer.newLine();
    		}
    	}
    	writer.flush();
    	writer.write("--------------------------------------------------------------------------");
    	writer.newLine();
    	writer.flush();
    	writer.write("Cards on Hand p1:");
    	writer.newLine();
    	for(Card c : g.getMyField(n.getP1().getId()).getCardsOnHand()){
    		writer.write(c.getName().toString());
    		writer.newLine();
    		writer.flush();
    	}
    	writer.write("--------------------------------------------------------------------------");
    	writer.newLine();
    	writer.flush();
    	writer.write("Cards on Hand p2:");
    	writer.newLine();
    	for(Card c : g.getMyField(n.getP2().getId()).getCardsOnHand()){
        	writer.write(c.getName().toString());
        	writer.newLine();
        	writer.flush();
    	}
        writer.write("--------------------------------------------------------------------------");
        writer.newLine();
        writer.write("--------------------------------------------------------------------------");
        writer.newLine();
        writer.write("--------------------------------------------------------------------------");
        writer.newLine();
        writer.newLine();
        writer.flush();
    	
    	
    	}
    }
    
    
}
