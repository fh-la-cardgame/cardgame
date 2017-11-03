/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.console;

import cardgame.classes.Card;
import cardgame.classes.GameCard;
import cardgame.db.DbCard;
import cardgame.db.DbConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
//        List<Card> list = d.getDeck("civitas diaboli");
//        System.out.println(list);
        System.out.println( d.insert_Card_Effect_Shields(3, 2, 4));
                
   }
    
    
}
