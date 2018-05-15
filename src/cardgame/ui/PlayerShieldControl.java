package cardgame.ui;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;

/**
 * Bildcontainer zur Abbildung der Spielerschilder.
 * @author BishaThan
 */
public class PlayerShieldControl extends StackPane{

    public PlayerShieldControl() {
        //Image img = new Image("http://i63.tinypic.com/2m3hj07.png");
        //this.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, true))));
        this.setMinHeight(20);
        this.setMinWidth(26);
        //this.setPadding(new Insets(1));
        this.setId("playershield_css");
    }
    
    
}
