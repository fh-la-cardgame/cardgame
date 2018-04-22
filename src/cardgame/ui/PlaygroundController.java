/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

Log4J - Apache
 */
package cardgame.ui;

import cardgame.classes.Card;
import cardgame.classes.Deck;
import cardgame.classes.GameCard;
import cardgame.classes.Player;
import cardgame.classes.SpecialCard;
import cardgame.db.DbCard;
import cardgame.logic.ConsolTest;
import cardgame.logic.Game;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Application.launch;

import cardgame.logic.LogicException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    private ListView<CardControl> pl2_cardsOnHand;

    List<EnemyGamecardControl> pl1_l;
    List<CardControl> pl2_l;

    ObservableList<CardControl> pl2_observ_list;
    ObservableList<EnemyGamecardControl> pl1_observ_list;

    List<PlayerShieldControl> pl1_l_shields;
    List<PlayerShieldControl> pl2_l_shields;

    ObservableList<PlayerShieldControl> pl2_observ_list_shields;
    ObservableList<PlayerShieldControl> pl1_observ_list_shields;

    GamecardControl[] pl2_card_field;
    SpecialCardControl[] pl2_scard_field;

    int id1 = 1;
    int id2 = 2;
    @FXML
    private Label pl2_card1;

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
        pl2_scard_field = new SpecialCardControl[4];

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
        Thread task = new GameThread(g);
        task.start();
        try {
            g.getMyField(id1).getGuiObservableBattlegroundMonster().addListener(new ListChangeListener<GameCard>() {
                @Override
                public void onChanged(Change<? extends GameCard> change) {
                    System.out.println("CHANGED");
                    while (change.next()) {
                        System.out.println(change.getFrom());
                        System.out.println(change.getTo());
                        System.out.println(change.getList().get(change.getTo()));
                        if (change.wasReplaced()){
                            int from = change.getFrom();
                            GamecardControl c = new GamecardControl();
                            setCardonField(c,from);
                        }  else {
                            for (int i = change.getFrom(); i < change.getTo(); i++) {
                                GameCard from = (GameCard) change.getList().get(i);
                               // GamecardControl c = new GamecardControl("x/x", "y/y", from.getName(), from.getImage(), from.getEffects(), from.getEvoEffects(), true);
                                GamecardControl c = new GamecardControl(from);
                                setCardonField(c, i);
                            }
                        }

                    }
                }
            });
        } catch (LogicException ex) {

        }
/*
        try {
            Card c = g.getMyField(id1).getCardsOnHand().stream().filter(e -> e instanceof GameCard).findFirst().get();
            g.playCard(id1, c);
            g.changePlayer(id2);
            g.changePlayer(id1);
            Card c2 = g.getMyField(id1).getCardsOnHand().stream().filter(e -> e instanceof GameCard).findFirst().get();
            g.playCard(id1,c2);
        } catch (LogicException ex) {
            System.out.println(ex);
        }*/
    }

    /**
     * Erzeugung von Spielerschildern
     *
     * @throws LogicException
     */
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

    /**
     * Erzeugung von Schildern des Gegenüberspielers
     *
     * @throws LogicException
     */
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

    /**
     * Setzen der Handkarten des Gegenüber.
     *
     * @throws LogicException
     */
    private void setCardsOnHandEnemy() throws LogicException {
        pl1_l = new ArrayList();
        int size = g.getEnemyField(id1).getCardsOnHand().size();
        for (int i = 0; i < size; i++) {
            pl1_l.add(new EnemyGamecardControl());

        }
        pl1_observ_list = FXCollections.observableArrayList(pl1_l);
        pl1_cardsOnHand.setItems(pl1_observ_list);

    }

    /**
     * Setzen der Handkarten bei dem Hauptspieler.
     *
     * @throws LogicException
     */
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


                System.out.println(gc + "<<<<<<< CARD");
                System.out.println("CARD\n" + gc.toString());
                System.out.println(gc.getShields().toString() + "-" + gc.getEvolutionShields().toString() + "-" + gc.getImage() + "-" + gc.getEffects() + "--------------------------------------------");
                blackShield = gc.getShields().toString();
                if (gc.getEvolutionShields() != null) {

                    whiteShield = gc.getEvolutionShields().toString();
                }
                if (gc.getShields() != null) {
                    blackShield = gc.getShields().toString();
                }

                pl2_l.add(new GamecardControl(gc));
            } else if (c instanceof SpecialCard) {
                sc = (SpecialCard) c;
                pl2_l.add(new SpecialCardControl(sc.getName(), sc.getDescription(), sc.getType(), sc.getImage(), sc.getEffects()));

            }

        }
        pl2_observ_list = FXCollections.observableArrayList(pl2_l);
        pl2_cardsOnHand.setItems(pl2_observ_list);

        pl2_cardsOnHand.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<CardControl>() {

                    @Override
                    public void changed(ObservableValue<? extends CardControl> ov, CardControl oldv, CardControl newv) {
                        ObservableList<Label> ob = FXCollections.observableArrayList(newv.getgDescription());                       description.setItems(ob);
                        if (newv.getBg() != null) {
                            cardPreviewPane.setBackground(newv.getBg());
                        }
                        newv.getPlay().setDisable(false);
                        newv.getPlay().setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                setPlayersField(newv);
                                pl2_cardsOnHand.getItems().remove(newv);
                            }
                        });

                    }
                });
    }

    /**
     * Erzeugung der Abhängigkeiten/Bindings.
     */
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

    /**
     * Binding eines Buttons.
     *
     * @param b  Button
     * @param ip IntegerProperty
     * @param v  geaenderte Wert
     */
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

    /**
     * Binding von Phasen
     *
     * @param m      Main-Phase Button
     * @param b      Battle-Phase Button
     * @param e      End-Phase Button
     * @param phase1 Phase des 1. Spielers
     * @param phase2 Phase des 2. Spielers
     */
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

    /**
     * Sizing an das Parentelement.
     */
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

        setPrefSizeMax(pl2_card1);
        setPrefSizeMax(pl2_card2);
        setPrefSizeMax(pl2_card3);
        setPrefSizeMax(pl2_card4);

        cardPreviewPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

    }

    /**
     * Hilfsmethode zur Maximierung der Groesse im Parentcontainer.
     *
     * @param c Control
     */
    void setPrefSizeMax(Control c) {
        c.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

    }

    /**
     * Entfernung des vordefinierten Styles.
     */
    private void clearStyle() {
        pl1_shields.getStyleClass().clear();
        pl2_shields.getStyleClass().clear();
        pl2_cardsOnHand.getStyleClass().clear();
        pl1_cardsOnHand.getStyleClass().clear();

    }

    private void setCardonField(GamecardControl gc, int where) {
        //pl2_card_field[where] = gc;
        gridPlayGround.add(gc, 4 + (2 * where), 7, 2, 1);

    }

    /**
     * Setzen einer Karte auf ein freies Feld.
     *
     * @param gc Spielerkarte/Spezialkarte
     */
    private void setPlayersField(CardControl gc) {
        if (gc == null) {
            throw new IllegalArgumentException("setPlayersField(GamecardControl gc) ist null");
        }
        if (gc instanceof GamecardControl) {
            for (int i = 0; i < pl2_card_field.length; i++) {
                System.out.println("pl2_card_field[i]:" + pl2_card_field[i]);
                if (pl2_card_field[i] == null) {
                    pl2_card_field[i] = (GamecardControl)gc;
                    gridPlayGround.add(pl2_card_field[i], 4 + (2 * i), 7, 2, 1);
                    break;
                }
            }
        } else {
            for (int i = 0; i < pl2_scard_field.length; i++) {
                System.out.println("pl2_card_field[i]:" + pl2_card_field[i]);
                if (pl2_scard_field[i] == null) {
                    pl2_scard_field[i] = (SpecialCardControl)gc;
                    gridPlayGround.add(pl2_scard_field[i], 4 + (2 * i), 9, 2, 2);
                    break;
                }
            }

        }

    }

}
