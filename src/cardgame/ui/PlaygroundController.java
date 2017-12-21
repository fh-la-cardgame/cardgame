/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.ui;

import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.Effect;
import cardgame.classes.GameCard;
import cardgame.classes.Player;
import cardgame.classes.Playground;
import cardgame.classes.SpecialCard;
import cardgame.console.Cardgame;
import cardgame.db.DbCard;
import cardgame.logic.Game;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import static javafx.application.Application.launch;

import cardgame.logic.LogicException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jdk.nashorn.internal.objects.NativeArray;

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
    private ListView<Label> description;
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
    private Label pl1_specialcard1;
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
    private ListView<PlayerShieldControl> pl2_shields;
    @FXML
    private ListView<PlayerShieldControl> pl1_shields;
    @FXML
    private ListView<EnemyGamecardControl> pl1_cardsOnHand;
    @FXML
    private ListView<GamecardControl> pl2_cardsOnHand;
    @FXML
    private Label card_black_shield;
    @FXML
    private Label card_white_shield;

    List<EnemyGamecardControl> pl1_l;
    List<GamecardControl> pl2_l;

    ObservableList<GamecardControl> pl2_observ_list;
    ObservableList<EnemyGamecardControl> pl1_observ_list;

    List<PlayerShieldControl> pl1_l_shields;
    List<PlayerShieldControl> pl2_l_shields;

    ObservableList<PlayerShieldControl> pl2_observ_list_shields;
    ObservableList<PlayerShieldControl> pl1_observ_list_shields;
    
    GamecardControl[] pl2_card_field;

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
        cardPreviewPane = new StackPane();
        cardPreviewPane.setMinHeight(200);
        cardPreviewPane.setMinWidth(100);
        
        pl2_card_field = new GamecardControl[4];
        
        setPlayersField();

        try {
            setCardsOnHandEnemy();
            setCardsOnHandPlayer();
            setPlayerShields();
            setEnemyPlayerShields();
        } catch (LogicException ex) {
            Logger.getLogger(PlaygroundController.class.getName()).log(Level.SEVERE, null, ex);
        }
        clearStyle();
        stretchElements();
        setBindings();
    }

    private void setPlayerShields() throws LogicException {

        pl2_l_shields = new ArrayList();

        int size = g.getMyField(id1).getPlayer().getShields().getCurrentShields();
        for (int i = 0; i < size; i++) {
            pl2_l_shields.add(new PlayerShieldControl());

        }

        pl2_observ_list_shields = FXCollections.observableArrayList(pl2_l_shields);
        pl2_shields.setItems(pl2_observ_list_shields);
        System.out.println("pl1_observ_list size:" + pl2_observ_list_shields.size());
    }

    private void setEnemyPlayerShields() throws LogicException {

        pl1_l_shields = new ArrayList();

        int size = g.getEnemyField(id1).getPlayer().getShields().getCurrentShields();
        for (int i = 0; i < size; i++) {
            pl1_l_shields.add(new PlayerShieldControl());

        }

        pl1_observ_list_shields = FXCollections.observableArrayList(pl1_l_shields);
        pl1_shields.setItems(pl1_observ_list_shields);
        System.out.println("pl1_observ_list size:" + pl1_observ_list_shields.size());
    }

    private void setCardsOnHandEnemy() throws LogicException {
        pl1_l = new ArrayList();
        int size = g.getEnemyField(id1).getCardsOnHand().size();
        for (int i = 0; i < size; i++) {
            pl1_l.add(new EnemyGamecardControl());

        }
        pl1_observ_list = FXCollections.observableArrayList(pl1_l);
        pl1_cardsOnHand.setItems(pl1_observ_list);

    }

    private void setCardsOnHandPlayer() throws LogicException {
        GameCard gc;
        SpecialCard sc;
        String whiteShield = "";
        String blackShield = "";
        pl2_l = new ArrayList();
        int size = g.getMyField(id1).getCardsOnHand().size();

        for (Card c : g.getMyField(id1).getCardsOnHand()) {
//            System.out.println("c.getClass().isInstance(GameCard.class)"  + (c instanceof GameCard));
//            System.out.println("c.getClass()"  + c.getClass());
//            System.out.println("GameCard.class"  + GameCard.class + "\n");
            if (c instanceof GameCard) {
                gc = (GameCard) c;
                System.out.println(gc.getShields().toString() + "-" + gc.getEvolutionShields().toString() + "-" + gc.getImage() + "-" + gc.getEffects());
                blackShield = gc.getShields().toString();
                if (gc.getEvolutionShields() != null) {
                    whiteShield = gc.getEvolutionShields().toString();
                }

                pl2_l.add(new GamecardControl(blackShield, whiteShield, gc.getName(), gc.getImage(), gc.getEffects(), true));
            } else if (c instanceof SpecialCard) {
                sc = (SpecialCard) c;
            Effect[] effects = sc.getEffects().toArray(new Effect[sc.getEffects().size()]);
                System.out.println("effects" + effects);
            pl2_l.add(new GamecardControl("", "", sc.getName(), sc.getImage(), effects, false) );

            }

        }
        pl2_observ_list = FXCollections.observableArrayList(pl2_l);
        pl2_cardsOnHand.setItems(pl2_observ_list);

        pl2_cardsOnHand.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<GamecardControl>() {

                    @Override
                    public void changed(ObservableValue<? extends GamecardControl> ov, GamecardControl oldv, GamecardControl newv) {
                        ObservableList<Label> ob = FXCollections.observableArrayList(newv.getDescription());
                        System.out.println("ob.size()" + ob.size());
                        description.setItems(ob);
                        if (newv.getBg() != null) {
                            cardPreviewPane.setBackground(newv.getBg());
                        }
                        newv.getPlay().setDisable(false);
                        newv.getPlay().setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                               gridPlayGround.add(newv, 4, 7, 2, 1);
                               pl2_cardsOnHand.getItems().remove(newv);
                            }
                        });

                    }
                });
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

        setPrefSizeMax(pl1_specialcard1);
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

       // setPrefSizeMax(pl2_card1);
        setPrefSizeMax(pl2_card2);
        setPrefSizeMax(pl2_card3);
        setPrefSizeMax(pl2_card4);

        cardPreviewPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

    }

    void setPrefSizeMax(Control c) {
        c.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

    }

    private void clearStyle() {
        pl1_shields.getStyleClass().clear();
        pl2_shields.getStyleClass().clear();
        pl2_cardsOnHand.getStyleClass().clear();
        pl1_cardsOnHand.getStyleClass().clear();

    }

    private void setPlayersField() {
        
    }

}
