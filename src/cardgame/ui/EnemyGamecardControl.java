/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

/**
 *
 * @author BishaThan
 */
public class EnemyGamecardControl extends Button{
    


    public EnemyGamecardControl() {
        
        this.getStyleClass().clear();
        this.setId("pl2_cards_on_hand");
        this.setPrefWidth(120);
        this.setPadding(new Insets(2));
    }
    
    
}
