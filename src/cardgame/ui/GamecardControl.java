package cardgame.ui;

import cardgame.classes.Effect;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
public class GamecardControl extends GridPane {


    /* Schwarze Schilder **/
    private Label card_black_shield;
    /* Weiße Schilder **/
    private Label card_white_shield;
    /* Kartenname **/
    private Label cardname;
    /* Abbildung des Kartenbildes **/
    private StackPane imageholder;
    /* Kartenbeschreibung **/
    private List<Label> description;
    /* Hintergrund, Kartenbild **/
    private Background bg;
    /* Button zum Spielen der Karte auf das Feld **/
    private Button play;
    /* Unterscheidung zwischen Spielkarte und Spezialkarte **/
    private BooleanProperty isGamecard;

    public GamecardControl(String blackshield, String whiteshield, String name, byte[] raw, List<Effect> effects, boolean isGamecard) {
        this(blackshield, whiteshield, name, raw, isGamecard);

        if (effects != null && effects.size() > 0) {
            description.add(new Label("Allgemeine Effekte:"));
            for (Effect e : effects) {
                if (e != null) {

                    description.add(new Label(e.getDescription()));
                }

            }

            description.add(new Label("***************************************"));
        }
        System.out.println("raw:" + raw);
        //Kartenhintergrund als Bild setzen
        if (raw != null) {
            //BackgroundImage bi = new BackgroundImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNk9bpvm5mT-SKVAgJU2Js8ocq5ctJrCYUUUw8AQ15ho7sxA4x"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            BackgroundImage bi = new BackgroundImage(new Image(new ByteArrayInputStream(raw)), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

            // imageholder.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
            bg = new Background(bi);
            imageholder.setBackground(bg);

        }

        posititionElements();
        setSizesAndPosition();

    }

    public GamecardControl(String blackshield, String whiteshield, String name, byte[] raw, Effect[] effects, Effect[] evoeffects, boolean isGamecard) {
        this(blackshield, whiteshield, name, raw, isGamecard);

        if (effects != null && effects.length > 0) {
            description.add(new Label("Schwarze Schildereffekte:"));
            for (int i = 0; i < effects.length; i++) {
                if (effects[i] != null) {

                    description.add(new Label(effects[i].getDescription()));
                }

            }

            description.add(new Label("***************************************"));
        }

        if (evoeffects != null && evoeffects.length > 0) {
            description.add(new Label("Evo Schildereffekte:"));
            for (int i = 0; i < evoeffects.length; i++) {
                if (evoeffects[i] != null) {

                    description.add(new Label(evoeffects[i].getDescription()));
                }

            }

            description.add(new Label("***************************************"));
        }

        System.out.println("raw:" + raw);
        //Kartenhintergrund als Bild setzen
        if (raw != null) {
            //BackgroundImage bi = new BackgroundImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNk9bpvm5mT-SKVAgJU2Js8ocq5ctJrCYUUUw8AQ15ho7sxA4x"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            BackgroundImage bi = new BackgroundImage(new Image(new ByteArrayInputStream(raw)), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

            // imageholder.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
            bg = new Background(bi);
            imageholder.setBackground(bg);

        }

        posititionElements();
        setSizesAndPosition();

    }

    public GamecardControl(String blackshield, String whiteshield, String name, byte[] raw, boolean isGamecard) {
        this.setMinHeight(120);
        this.setMinWidth(120);
        this.setMaxHeight(120);
        this.setMaxWidth(120);
        setConstraints();
        this.card_black_shield = new Label(blackshield);
        this.card_white_shield = new Label(whiteshield);
        this.cardname = new Label(name);
        this.imageholder = new StackPane();
        this.play = new Button("Spielen");
        this.play.setDisable(true);
        this.isGamecard = new SimpleBooleanProperty(isGamecard);
        this.description = new ArrayList<>();

        System.out.println("raw:" + raw);
        //Kartenhintergrund als Bild setzen
        if (raw != null) {
            //BackgroundImage bi = new BackgroundImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNk9bpvm5mT-SKVAgJU2Js8ocq5ctJrCYUUUw8AQ15ho7sxA4x"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            BackgroundImage bi = new BackgroundImage(new Image(new ByteArrayInputStream(raw)), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

            // imageholder.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
            bg = new Background(bi);
            imageholder.setBackground(bg);

        }
        //ID 
        this.setId("pl1_specialcard3");

    }

    /**
     * Positionierung der Elemente im Grid.
     */
    private void posititionElements() {
        this.add(imageholder, 0, 0, 2, 1);
        this.add(cardname, 0, 1, 2, 1);
        this.add(card_black_shield, 0, 2);
        this.add(card_white_shield, 1, 2);
        this.add(play, 0, 3, 2, 1);

    }

    /**
     * Anpassung der Groesse und Ausrichtung.
     */
    private void setSizesAndPosition() {
        //Padding
        Insets i = new Insets(2, 2, 2, 2);
        this.card_black_shield.setPadding(i);
        this.card_white_shield.setPadding(i);
        this.cardname.setPadding(i);
        this.imageholder.setPadding(i);
        this.play.setPadding(i);

        //   this.card_black_shield.
//        this.card_black_shield.setAlignment(Pos.CENTER);
//        
//        this.card_white_shield.setAlignment(Pos.CENTER);
//        this.cardname.setAlignment(Pos.CENTER);
//        this.imageholder.setAlignment(Pos.CENTER);
//        this.cardname.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
//        this.imageholder.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
//        this.card_white_shield.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
//        this.card_black_shield.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
    }

    public GamecardControl() {
        //  this("0/0", "0/0", "Unknown", new byte[0]);

    }

    /**
     * Anlegen und Setzen der Constraints.
     */
    private void setConstraints() {
        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();
        RowConstraints r4 = new RowConstraints();

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();

        r1.setPercentHeight(70);
        r2.setPercentHeight(10);
        r3.setPercentHeight(10);
        r4.setPercentHeight(10);
        c1.setPercentWidth(50);;
        c2.setPercentWidth(50);
        this.getRowConstraints().addAll(r1, r2, r3, r4);
        this.getColumnConstraints().addAll(c1, c2);

        //      this.getColumnConstraints().addAll(new ColumnConstraints(50),new ColumnConstraints(50));
    }

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

    /**
     * Getter
     * @return Kartenname 
     */
    public Label getCardname() {
        return cardname;
    }

    /**
     * Setter
     * @param cardname Kartenname
     */
    public void setCardname(Label cardname) {
        this.cardname = cardname;
    }

    /**
     * Getter
     * @return Bildcontainer
     */
    public StackPane getImageholder() {
        return imageholder;
    }

    /**
     * Setter
     * @param imageholder Bildcontainer
     */
    public void setImageholder(StackPane imageholder) {
        this.imageholder = imageholder;
    }

    /**
     * Getter
     * @return Liste mit den Effektbeschreibungen
     */
    public List<Label> getDescription() {
        return description;
    }

    /**
     * Getter
     * @return Spielbutton
     */
    public Button getPlay() {
        return play;
    }

    /**
     * Getter
     * @return Hintergrund
     */
    public Background getBg() {
        return bg;
    }

    /**
     * Setter
     * @param description Beschreibung
     */
    public void setDescription(List<Label> description) {
        this.description = description;
    }

    /**
     * Getter
     * @return Unterscheidung zwischen Spielkarte und Spezialkarte (true - Spielkarte; false - Spezialkarte)
     */
    public BooleanProperty isGamecard() {
        return isGamecard;
    }

    /**
     * Setter
     * @param isGamecard Unterscheidung zwischen Spielkarte und Spezialkarte (true - Spielkarte; false - Spezialkarte)
     */
    public void setIsGamecard(BooleanProperty isGamecard) {
        this.isGamecard = isGamecard;
    }

}
