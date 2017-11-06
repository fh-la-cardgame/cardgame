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
        List<Card> list = d.getDeck("civitas diaboli");
        System.out.println(list);
//        System.out.println( d.insert_Card_Effect_Shields(3, 2, 4));
                
   }
    
    
}
