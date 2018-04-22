package cardgame.classes;

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
import static javafx.scene.layout.BackgroundSize.AUTO;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * Bildet eine Teilstruktur einer Karte ab.
 *
 * @author BishaThan
 */
public abstract class Card{

    /**
     * Identifikationsnummer der Karte. *
     */
    private final int cid;
    /**
     * Name der Karte. *
     */
    private final String name;
    /**
     * Kartenbeschreibung. Zusammensetzung aus der Kartenbeschreibung und den
     * Effektbeschreibungen. *
     */
    private final String description;
    /**
     * Typ einer Karte. *
     */
    private final Type type;
    /**
     * Bild einer Karte *
     */
    private final byte[] image;

    /**
     * Konstruktor
     *
     * @param id Identifikationnummer
     * @param name Name der Karte
     * @param description Kartenbeschreibung
     * @param type Typ der Karte
     */
    protected Card(final int cid, final String name, final String description, final Type type, final byte[] image) {

        this.cid = cid;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;


    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public byte[] getImage() {
        return image;
    }

    public int getCid() {
        return cid;
    }

    
    
    
    @Override
    public String toString() {
        return getCid() + " " + getName() + " " + getDescription() + "\nWerte: " + getType().toString();
    }

}
