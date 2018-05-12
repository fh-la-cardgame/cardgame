package cardgame.ui;

import cardgame.classes.Effect;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import cardgame.classes.GameCard;
import cardgame.classes.Type;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * Abbildung der Spielkarte als Controlelement der GUI
 *
 * @author BishaThan
 */
public class GamecardControl extends CardControl {


    /* Angriffspunkte **/
    private Label atk;
    /* Schwarze Schilder **/
    private Label card_black_shield;
    /* Weiße Schilder **/
    private Label card_white_shield;
    /* Button zum Kämpfen der Karte auf das Feld **/
    private Button fight;
    /* Card Referenz */
    private GameCard card;


    private GamecardControl(){
        this("-/-","-/-","","","",Type.human, new byte[1],null,null);
        
    }
    public GamecardControl(GameCard card){
        this(card.getEvolutionShields().toString(), card.getShields().toString(),card.getName(), card.getDescription(), Integer.toString(card.getAtk()), card.getType(), card.getImage(),card.getEffects(),card.getEvoEffects());
        this.card = card;
    }

    private GamecardControl(String blackshield, String whiteshield, String name, String description, String atk, Type type, byte[] raw, Effect[] effects, Effect[] evoeffects) {
        super(name, description, type, raw);
        this.fight = new Button("Kämpfen");
        this.fight.setVisible(false);
        this.card_black_shield = new Label(blackshield);
        this.card_white_shield = new Label(whiteshield);
        this.atk = new Label(atk);

        if (effects != null && effects.length > 0) {
            Label l = new Label("SCHWARZE SCHILDEFFEKTE:");
            l.setId("boldtext_css");
            this.getgDescription().add(l);
            for (int i = 0; i < effects.length; i++) {
                if (effects[i] != null) {

                    this.getgDescription().add(new Label(effects[i].getDescription()));
                }

            }

            this.getgDescription().add(new Label(""));
        }

        if (evoeffects != null && evoeffects.length > 0) {
            Label l = new Label("EVO SCHILDEFFEKTE:");
            l.setId("boldtext_css");
            this.getgDescription().add(l);
            for (int i = 0; i < evoeffects.length; i++) {
                if (evoeffects[i] != null) {

                    this.getgDescription().add(new Label(evoeffects[i].getDescription()));
                }

            }

            this.getgDescription().add(new Label("***************************************"));
        }



        positionAdditionalElements();
        setAdditionalSizesAndPosition();

    }



    /**
     * Positionierung der Elemente im Grid.
     */
    @Override
    protected void positionAdditionalElements() {
        this.add(card_black_shield, 0, 3);
        this.add(card_white_shield, 1, 3);
        this.add(atk, 0, 2, 2, 1);
        this.add(fight, 0, 4, 2, 1);

    }

    /**
     * Anpassung der Groesse und Ausrichtung.
     */
    @Override
    protected void setAdditionalSizesAndPosition() {
        //Padding
        Insets i = new Insets(2, 2, 2, 2);
        this.card_black_shield.setPadding(i);
        this.card_white_shield.setPadding(i);}

    /**
     * Getter
     * @return schwarze Schilder
     */
    public Label getCard_black_shield() {
        return card_black_shield;
    }

    /**
     * Setter
     * @param card_black_shield schwarze Schilder
     */
    public void setCard_black_shield(Label card_black_shield) {
        this.card_black_shield = card_black_shield;
    }

    /**
     * Getter
     * @return weiße Schilder 
     */
    public Label getCard_white_shield() {
        return card_white_shield;
    }

    /**
     * Setter
     * @param card_white_shield weiße Schilder
     */
    public void setCard_white_shield(Label card_white_shield) {
        this.card_white_shield = card_white_shield;
    }

    public GameCard getCard() {
        return card;
    }

    public void setCard(GameCard card) {
        this.card = card;
    }

    public Button getFight() {
        return fight;
    }
}
