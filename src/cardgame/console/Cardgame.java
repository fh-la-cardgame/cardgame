/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.console;

import cardgame.classes.Card;
import cardgame.db.DbCard;

import java.util.List;


/**
 *
 * @author HortensiaX
 */
public class Cardgame {


    
    public static void main(String[] args) {
        /*
         * Exemplarische Ausfuehrung von "getDeck(String deckName)"
         */
        DbCard d = new DbCard();
//        d.showAmountOfCards("civitas diaboli");
//        System.out.println(d.getGameCard("Astrator"));
//        d.updateEffect(c_eid, gid, eid, shield, evo_shield)
        
        System.out.println(d.getGameCard("Marbas"));
//        d.insert_Card_Effect_Shields(36, 4, -1);
//        d.updateShields(3, 4);
        System.out.println(d.getGameCard("Marbas"));
//      System.out.println(d.insert_Card_Effect_EvoShields(76, 24, 3));
//        Der Deckname wird nun auch case insensitive eingelesen:
//        List<Card> list = d.getDeck("civitas diaboli");
//        System.out.println(list);
//        System.out.println( d.insert_Card_Effect_EvoShields(22, 3, 0));
//        d.updateEvoShield(3, 8);
      //  d.add_change_Effects(18, 17, -1, null);
                
   }
    
    
}
