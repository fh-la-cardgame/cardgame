package cardgame.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;

/**
 * Abbildung der gegnerischen Spielkarte auf dem Spielfeld.
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
