/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ui;

import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.Player;
import cardgame.logic.Game;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author BishaThan
 */
public class PlaygroundController implements Initializable {

    @FXML
    private GridPane gridPlayGround;
    @FXML
    private Button main1;
    @FXML
    private Button battle1;
    @FXML
    private Button end1;
    @FXML
    private Button main2;
    @FXML
    private Button battle2;
    @FXML
    private Button end2;
    @FXML
    private Label zuege;
    @FXML
    private Label zuege_anzahl;
    @FXML
    private Label player2;
    @FXML
    private Label player1;
    @FXML
    private ListView<?> description;
    Player p;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        p = new Player("hey");
        
        player1.textProperty().bind(p.getpName());
    }    

    @FXML
    private void Aendere(ActionEvent event) {
        p.setpName("Neu");
        //System.out.println(vorname.getValue());
        //System.out.println(player1.textProperty().toString());
                
    }
    
     public static void main(String[] args) {
        launch(args);
    } 
    
}
