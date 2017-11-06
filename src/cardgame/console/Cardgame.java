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
        //Der Deckname wird nun auch case insensitive eingelesen:
        List<Card> list = d.getDeck("Civitas Diaboli");
        System.out.println(list);
       // System.out.println( d.insert_Card_Effect_Shields(14, 3, -1));
                
   }
    
    
}
