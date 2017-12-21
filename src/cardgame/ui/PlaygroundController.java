/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ui;

import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.Player;
import cardgame.classes.Playground;
import cardgame.db.DbCard;
import cardgame.logic.Game;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import static javafx.application.Application.launch;

import cardgame.logic.LogicException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

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
    @FXML
    private Label countCards2;
    @FXML
    private Label countCards1;

    private Player p1;
    private Player p2;
    private Deck d1;
    private Deck d2;
    private Game g;
    @FXML
    private AnchorPane pl1_specialcard1;
    @FXML
    private Label pl1_specialcard2;
    @FXML
    private Label pl1_specialcard3;
    @FXML
    private Label pl1_specialcard4;
    @FXML
    private Label pl1_card1;
    @FXML
    private Label pl1_card2;
    @FXML
    private Label pl1_card3;
    @FXML
    private Label pl1_card4;
    @FXML
    private Label pl2_card1;
    @FXML
    private Label pl2_card2;
    @FXML
    private Label pl2_card3;
    @FXML
    private Label pl2_card4;
    @FXML
    private Label pl2_specialcard1;
    @FXML
    private Label pl2_specialcard2;
    @FXML
    private Label pl2_specialcard3;
    @FXML
    private Label pl2_specialcard4;
    @FXML
    private StackPane cardPreviewPane;
    @FXML
    private ListView<?> pl2_shields;
    @FXML
    private ListView<?> pl1_shields;
    @FXML
    private ListView<?> pl1_cardsOnHand;
    @FXML
    private ListView<?> pl2_cardsOnHand;
    @FXML
    private Label card_black_shield;
    @FXML
    private Label card_white_shield;

    int id1 = 1;
    int id2 = 2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        id1 = 1;
        id2 = 2;
        DbCard db = new DbCard();
        p1 = new Player(id1, "Dennis");
        p2 = new Player(id2, "David");
        //g = new Game();
        d1 = new Deck(1, "Flora", db.getDeck("Flora"));
        d2 = new Deck(2, "David", db.getDeck("civitas diaboli"));
        g = new Game(p1, p2, d1, d2);
//
//        stretchElements();
//        setBindings();
    }

    private void setBindings() {

        try {
            //Bindings
            //Player Namen
            player1.textProperty().bind(g.getMyField(id1).getPlayer().getpName());
            player2.textProperty().bind(g.getEnemyField(id2).getPlayer().getpName());
            //Kartenanzahl Binding
            countCards2.textProperty().bind(g.getEnemyField(id1).getDeck().getCountCards().asString());
            countCards1.textProperty().bind(g.getMyField(id1).getDeck().getCountCards().asString());

            bindPhases(main1, battle1, end1, g.getMyPhase(id1), g.getEnemyPhase(id1));
            bindPhases(main2, battle2, end2, g.getEnemyPhase(id1), g.getMyPhase(id1));
        } catch (LogicException ex) {
            Logger.getLogger(PlaygroundController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void bindButtonPerValue(Button b, IntegerProperty ip, int v) {
        b.disableProperty().bind(new BooleanBinding() {
            {
                bind(ip);
            }

            @Override
            protected boolean computeValue() {
                return ip.getValue() != v;
            }
        });
    }

    private void bindPhases(Button m, Button b, Button e, IntegerProperty phase1, IntegerProperty phase2) {
        m.disableProperty().bind(new BooleanBinding() {
            {
                bind(phase1);
            }

            @Override
            protected boolean computeValue() {
                System.out.println("main: " + (phase1.getValue() != 0));

                return phase1.getValue() != 0;
            }
        });

        b.disableProperty().bind(new BooleanBinding() {
            {
                bind(phase1);
            }

            @Override
            protected boolean computeValue() {

                return phase1.getValue() == 2; // Hinweis: nicht in einzel Variable abspeichert
            }
        });
        e.disableProperty().bind(new BooleanBinding() {
            {
                bind(phase1);
            }

            @Override
            protected boolean computeValue() {
                return phase1.getValue() == 2;
            }
        });

    }

    @FXML
    private void battle1Action(ActionEvent event) {
        //g.setpPlayer1Phase(1);
    }

    @FXML
    private void end1Action(ActionEvent event) {
        //g.setpPlayer1Phase(2);
        //g.setpPlayer2Phase(0);
    }

    @FXML
    private void battle2Action(ActionEvent event) {
        //g.setpPlayer2Phase(1);
    }

    @FXML
    private void end2Action(ActionEvent event) {
        // g.setpPlayer2Phase(2);
        // g.setpPlayer1Phase(0);
    }

    private void stretchElements() {
        setPrefSizeMax(main1);
        setPrefSizeMax(main2);
        setPrefSizeMax(end1);
        setPrefSizeMax(end2);
        setPrefSizeMax(battle1);
        setPrefSizeMax(battle2);

        setPrefSizeMax(countCards1);
        setPrefSizeMax(countCards2);

       // setPrefSizeMax(pl1_specialcard1);
        setPrefSizeMax(pl1_specialcard2);
        setPrefSizeMax(pl1_specialcard3);
        setPrefSizeMax(pl1_specialcard4);

        setPrefSizeMax(pl2_specialcard1);
        setPrefSizeMax(pl2_specialcard2);
        setPrefSizeMax(pl2_specialcard3);
        setPrefSizeMax(pl2_specialcard4);

        setPrefSizeMax(pl1_card1);
        setPrefSizeMax(pl1_card2);
        setPrefSizeMax(pl1_card3);
        setPrefSizeMax(pl1_card4);

        setPrefSizeMax(pl2_card1);
        setPrefSizeMax(pl2_card2);
        setPrefSizeMax(pl2_card3);
        setPrefSizeMax(pl2_card4);

    }

    void setPrefSizeMax(Control c) {
        c.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

    }

}
