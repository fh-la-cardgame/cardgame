package cardgame.ui;

import cardgame.classes.Card;
import cardgame.classes.SpecialCard;
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

    private SpecialCard card;

    private SpecialCardControl(final String name, final String description, final Type type, final byte[] raw, final List<Effect> effects) {
        super(name, description, type, raw);

        if (effects != null && effects.size() > 0) {
            Label l = new Label("ALLGEMEINE EFFEKTE:");
            l.setId("boldtext_css");
            getgDescription().add(l);
            for (Effect e : effects) {
                if (e != null) {

                    getgDescription().add(new Label(e.getDescription()));
                }

            }

            getgDescription().add(new Label(""));
        }


    }
    public SpecialCardControl(SpecialCard s){
        this(s.getName(),s.getDescription(),s.getType(),s.getImage(),s.getEffects());
        this.card = s;
    }
    public SpecialCardControl(){
        this("", "", Type.human, new byte[1], null);
    }

    @Override
    protected void positionAdditionalElements() {
    }

    public SpecialCard getCard() {
        return card;
    }

    @Override
    protected void setAdditionalSizesAndPosition() {
    }



}
