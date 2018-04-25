package cardgame.ui;

import cardgame.classes.Card;
import cardgame.classes.Type;
import cardgame.classes.Effect;
import java.io.ByteArrayInputStream;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

/**
 * Abbildung der Spielkarte als Controlelement der GUI
 *
 * @author BishaThan
 */
public class SpecialCardControl extends CardControl {


    public SpecialCardControl(final String name, final String description, final Type type, final byte[] raw, final List<Effect> effects) {
        super(name, description, type, raw);

        if (effects != null && effects.size() > 0) {
            getgDescription().add(new Label("ALLGEMEINE EFFEKTE:"));
            for (Effect e : effects) {
                if (e != null) {

                    getgDescription().add(new Label(e.getDescription()));
                }

            }

            getgDescription().add(new Label("***************************************"));
        }


    }
    
    public SpecialCardControl(){
        this("", "", Type.human, new byte[1], null);
    }

    @Override
    protected void positionAdditionalElements() {
    }

    @Override
    protected void setAdditionalSizesAndPosition() {
    }



}
