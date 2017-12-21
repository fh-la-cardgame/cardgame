/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ui;

import cardgame.classes.Effect;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javax.swing.GroupLayout;
import static javax.swing.text.StyleConstants.Alignment;

/**
 * Abbildung der Spielkarte als Controlelement der GUI
 *
 * @author BishaThan
 */
public class GamecardControl extends GridPane {


    private Label card_black_shield;
    private Label card_white_shield;
    private Label cardname;
    private StackPane imageholder;
    private List<Label> description;
    private Background bg;
    private Button play;

    
    public GamecardControl(String blackshield, String whiteshield, String name, byte[] raw, Effect[] effects) {
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
        this.description = new ArrayList<>();
        for (int i = 0; i < effects.length; i++) {
            description.add(new Label(effects[i].getDescription()));
        }
        System.out.println("raw:"+raw);
        //Kartenhintergrund als Bild setzen
        if(raw != null){
        //BackgroundImage bi = new BackgroundImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNk9bpvm5mT-SKVAgJU2Js8ocq5ctJrCYUUUw8AQ15ho7sxA4x"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        BackgroundImage bi = new BackgroundImage(new Image(new ByteArrayInputStream(raw)), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        
       // imageholder.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        bg = new Background(bi);
        imageholder.setBackground(bg);
        
        }
        //ID 
        this.setId("pl1_specialcard3");
        
        posititionElements();

        
        setSizesAndPosition();
        
    }
    
    private void posititionElements(){
        this.add(imageholder, 0,0,2,1);
        this.add(cardname, 0,1,2,1);
        this.add(card_black_shield, 0, 2);
        this.add(card_white_shield, 1, 2);
        this.add(play, 0,3,2,1);
        
    }
    private void setSizesAndPosition(){
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
        this.getRowConstraints().addAll(r1,r2,r3,r4);
        this.getColumnConstraints().addAll(c1,c2);
        
        
  //      this.getColumnConstraints().addAll(new ColumnConstraints(50),new ColumnConstraints(50));
    
    }

    public Label getCard_black_shield() {
        return card_black_shield;
    }

    public void setCard_black_shield(Label card_black_shield) {
        this.card_black_shield = card_black_shield;
    }

    public Label getCard_white_shield() {
        return card_white_shield;
    }

    public void setCard_white_shield(Label card_white_shield) {
        this.card_white_shield = card_white_shield;
    }

    public Label getCardname() {
        return cardname;
    }

    public void setCardname(Label cardname) {
        this.cardname = cardname;
    }

    public StackPane getImageholder() {
        return imageholder;
    }

    public void setImageholder(StackPane imageholder) {
        this.imageholder = imageholder;
    }

    public List<Label> getDescription() {
        return description;
    }

    public Button getPlay() {
        return play;
    }

    public Background getBg() {
        return bg;
    }

    public void setDescription(List<Label> description) {
        this.description = description;
    }
     

    
}
