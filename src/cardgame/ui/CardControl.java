package cardgame.ui;

import cardgame.classes.Effect;
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
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * Abbildung einer allgemeinen Spielkarte als Controlelement der GUI
 *
 * @author BishaThan
 */
public abstract class CardControl extends GridPane {

    /* Kartenname **/
    private Label gName;
    /* Abbildung des Kartenbildes **/
    private StackPane imageholder;
    /* Kartenbeschreibung **/
    private List<Label> gDescription;
    /* Hintergrund, Kartenbild **/
    private Background bg;
    /* Button zum Spielen der Karte auf das Feld **/
    private Button play;
    /* Unterscheidung zwischen Spielkarte und Spezialkarte **/
    private BooleanProperty isGamecard;
    
    	/** Identifikationsnummer der Karte. **/
	private final int id;
        /** Name der Karte. **/
	private final String name;
        /** Kartenbeschreibung. Zusammensetzung aus der Kartenbeschreibung und den Effektbeschreibungen. **/
	private final String description;
        /** Typ einer Karte. **/
	private final Type type;
		/** Bild einer Karte **/
	private final byte[] image;
        
        /**
         * Konstruktor
         * @param id Identifikationnummer
         * @param name Name der Karte
         * @param description Kartenbeschreibung
         * @param type Typ der Karte
         */
        public CardControl(final int id, final String name, final String description, final Type type, final byte[] image) {
		this.setMinHeight(130);
        this.setMinWidth(120);
        this.setMaxHeight(130);
        this.setMaxWidth(120);
        setConstraints();
            
                this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.image = image;
                
                
                //GUI
                this.gName = new Label(name);
        this.imageholder = new StackPane();
        this.play = new Button("Spielen");
        //this.play.setDisable(true);
        //this.isGamecard = new SimpleBooleanProperty(isGamecard);
        this.isGamecard = new SimpleBooleanProperty(true);
        this.gDescription = new ArrayList<>();

        
        //Kartenhintergrund als Bild setzen
        if (image != null) {
            BackgroundImage bi = new BackgroundImage(new Image(new ByteArrayInputStream(image)), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            bg = new Background(bi);
            imageholder.setBackground(bg);

        }
        
        posititionElements();
        setSizesAndPosition();
        //ID fuer CSS
        this.setId("card_css1");
	}

    
    /**
     * Positionierung der Elemente im Grid.
     */
    private void posititionElements() {
        this.add(imageholder, 0, 0, 2, 1);
        this.add(gName, 0, 1, 2, 1);
        this.add(play, 0, 4);

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

        r1.setPercentHeight(50);
        r2.setPercentHeight(10);
        r3.setPercentHeight(10);
        r4.setPercentHeight(10);
        r5.setPercentHeight(20);
        c1.setPercentWidth(50);;
        c2.setPercentWidth(50);
        this.getRowConstraints().addAll(r1, r2, r3, r4, r5);
        this.getColumnConstraints().addAll(c1, c2);

        //      this.getColumnConstraints().addAll(new ColumnConstraints(50),new ColumnConstraints(50));
    }

    public Label getgName() {
        return gName;
    }

    public void setgName(Label gName) {
        this.gName = gName;
    }

    public StackPane getImageholder() {
        return imageholder;
    }

    public void setImageholder(StackPane imageholder) {
        this.imageholder = imageholder;
    }

    public List<Label> getgDescription() {
        return gDescription;
    }

    public void setgDescription(List<Label> gDescription) {
        this.gDescription = gDescription;
    }

    public Background getBg() {
        return bg;
    }

    public void setBg(Background bg) {
        this.bg = bg;
    }

    public Button getPlay() {
        return play;
    }

    public void setPlay(Button play) {
        this.play = play;
    }

    public BooleanProperty getIsGamecard() {
        return isGamecard;
    }

    public void setIsGamecard(BooleanProperty isGamecard) {
        this.isGamecard = isGamecard;
    }
    
    	
    
//	public int getId() {
//		return id;
//	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Type getType() {
		return type;
	}
	
	public byte[] getImage(){
		return image;
	}
	@Override
	public String toString(){
		return getId()+" "+getName()+" "+getDescription()+"\nWerte: "+getType().toString();
	}
    
}
