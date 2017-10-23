/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.console;

import cardgame.db.DbCard;
import cardgame.db.DbConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HortensiaX
 */
public class Cardgame {


    
    public static void main(String[] args) {
        
        DbCard d = new DbCard();
        d.selectAll();
                
   }
    
    
}
