package cardgame.ui;

import cardgame.classes.Type;
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
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * Bildet eine Teilstruktur einer Karte ab.
 *
 * @author BishaThan
 */
public abstract class CardControl extends GridPane {

    /* Kartenname **/
    private final Label gName;
    /* Abbildung des Kartenbildes **/
    private final StackPane imageholder;
    /* Kartenbeschreibung **/
    private final List<Label> gDescription;
    /* Hintergrund, Kartenbild **/
    private final Background bg;
    /* Button zum Spielen der Karte auf das Feld **/
    private final Button play;


    /**
     * Konstruktor
     * @param name
     * @param description
     * @param type
     * @param image
     */
    protected CardControl(final String name, final String description, final Type type, final byte[] image) {
        setAppearance();

        //GUI
        this.gName = new Label(name);
        this.imageholder = new StackPane();
        this.play = new Button("Spielen");
        this.play.setVisible(false);
        this.gDescription = new ArrayList<>();
        this.gDescription.add(new Label(description));

        //Kartenhintergrund als Bild setzen
        BackgroundImage bi = new BackgroundImage(new Image(new ByteArrayInputStream(image)), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        bg = new Background(bi);
        if (image != null) {
            imageholder.setBackground(bg);

        }

        posititionElements();
        setSizesAndPosition();
        //ID fuer CSS
        setCSS();
    
    }
        /**
         * 
         */
    public CardControl() {
        setAppearance();
        this.bg = new Background(new BackgroundImage(new Image(new ByteArrayInputStream(new byte[1])), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));
        this.gName = new Label("");
        this.imageholder = new StackPane();
        this.gDescription = new ArrayList<>();
        this.play = new Button();
        this.play.setVisible(false);
        //this.isGamecard = false;
    }

    private void setAppearance() {
        this.setMinHeight(130);
        this.setMinWidth(120);
        this.setMaxHeight(130);
        this.setMaxWidth(120);
        setConstraints();
    }

    private void setCSS(){
        this.setId("card_css");
        this.gName.setId("card_name");
        StyleSetting.setButtonCss(this.play);
    }

    /**
     * Positionierung der Elemente im Grid.
     */
    private void posititionElements() {
        this.add(imageholder, 0, 0, 2, 1);
        this.add(gName, 0, 1, 2, 1);
        this.add(play, 0, 4, 2, 1);

    }

    protected abstract void positionAdditionalElements();

    protected abstract void setAdditionalSizesAndPosition();

    /**
     * Anpassung der Groesse und Ausrichtung.
     */
    private void setSizesAndPosition() {
        //Padding
        Insets i = new Insets(2, 2, 2, 2);
        this.gName.setPadding(i);
        this.imageholder.setPadding(i);
        this.play.setPadding(i);
    }

    /**
     * Anlegen und Setzen der Constraints.
     */
    private void setConstraints() {
        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();
        RowConstraints r4 = new RowConstraints();
        RowConstraints r5 = new RowConstraints();

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();

        r1.setPercentHeight(40);
        r2.setPercentHeight(10);
        r3.setPercentHeight(10);
        r4.setPercentHeight(10);
        r5.setPercentHeight(30);
        c1.setPercentWidth(50);;
        c2.setPercentWidth(50);
        this.getRowConstraints().addAll(r1, r2, r3, r4, r5);
        this.getColumnConstraints().addAll(c1, c2);

        //      this.getColumnConstraints().addAll(new ColumnConstraints(50),new ColumnConstraints(50));
    }

    public Label getgName() {
        return gName;
    }

    public StackPane getImageholder() {
        return imageholder;
    }

    public List<Label> getgDescription() {
        return gDescription;
    }

    public Background getBg() {
        return bg;
    }

    public Button getPlay() {
        return play;
    }


}
