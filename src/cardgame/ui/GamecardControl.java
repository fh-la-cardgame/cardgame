/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    
    public GamecardControl(String blackshield, String whiteshield, String name, Image img) {
        
        setConstraints();
        this.card_black_shield = new Label(blackshield);
        this.card_white_shield = new Label(whiteshield);
        this.cardname = new Label(name);
        this.imageholder = new StackPane();
        
        //Kartenhintergrund als Bild setzen
        BackgroundImage bi = new BackgroundImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNk9bpvm5mT-SKVAgJU2Js8ocq5ctJrCYUUUw8AQ15ho7sxA4x"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        
        //imageholder.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        imageholder.setBackground(new Background(bi));

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
        
    }
    private void setSizesAndPosition(){
        //Padding
        Insets i = new Insets(2, 2, 2, 2);
        this.card_black_shield.setPadding(i);
        this.card_white_shield.setPadding(i);
        this.cardname.setPadding(i);
        this.imageholder.setPadding(i);
        

        
     //   this.card_black_shield.
//        this.card_black_shield.setAlignment(Pos.CENTER);
//        
//        this.card_white_shield.setAlignment(Pos.CENTER);
//        this.cardname.setAlignment(Pos.CENTER);
//        this.imageholder.setAlignment(Pos.CENTER);
        
        this.cardname.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
        this.imageholder.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
        this.card_white_shield.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
        this.card_black_shield.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
        
    }
    
     public GamecardControl() {
         this("0/0", "0/0", "Unknown", new Image("https://www.w3schools.com/w3css/img_fjords.jpg"));
        
    }

    private void setConstraints() {
      RowConstraints r1 = new RowConstraints();      
        RowConstraints r2 = new RowConstraints();        
        RowConstraints r3 = new RowConstraints();
        
        ColumnConstraints c1 = new ColumnConstraints();      
        ColumnConstraints c2 = new ColumnConstraints();   
        
        
//        r1.setPercentHeight(80);        
//        r2.setPercentHeight(10);        
//        r3.setPercentHeight(10);        
//        c1.setPercentWidth(50);;        
//        c2.setPercentWidth(50);
//        this.getRowConstraints().addAll(r1,r2,r3);
//        this.getColumnConstraints().addAll(c1,c2);
        
        
  //      this.getColumnConstraints().addAll(new ColumnConstraints(50),new ColumnConstraints(50));
    
    }
     

    
}
