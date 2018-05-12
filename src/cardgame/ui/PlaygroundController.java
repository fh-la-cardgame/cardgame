/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

Log4J - Apache
 */
package cardgame.ui;

import cardgame.classes.*;
import cardgame.db.DbCard;
import cardgame.logic.Game;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Application.launch;

import cardgame.logic.LogicException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    private Playground myField;
    private Playground enemyField;
    private Game g;

    @FXML
    private Label enemy_specialcard1;
    @FXML
    private Label enemy_specialcard2;
    @FXML
    private Label enemy_specialcard3;
    @FXML
    private Label enemy_specialcard4;
    @FXML
    private Label enemy_card1;
    @FXML
    private Label enemy_card2;
    @FXML
    private Label enemy_card3;
    @FXML
    private Label enemy_card4;
    @FXML
    private Label my_card1;
    @FXML
    private Label my_card2;
    @FXML
    private Label my_card3;
    @FXML
    private Label my_card4;
    @FXML
    private Label my_specialcard1;
    @FXML
    private Label my_specialcard2;
    @FXML
    private Label my_specialcard3;
    @FXML
    private Label my_specialcard4;
    @FXML
    private StackPane cardPreviewPane;
    @FXML
    private ListView<PlayerShieldControl> my_shields;
    @FXML
    private ListView<PlayerShieldControl> enemy_shields;
    @FXML
    private ListView<EnemyGamecardControl> enemy_cardsOnHand;
    @FXML
    private ListView<CardControl> my_cardsOnHand;

    List<EnemyGamecardControl> enemy_l;
    List<CardControl> my_l;


    ObservableList<EnemyGamecardControl> enemy_observ_list;

    List<PlayerShieldControl> enemy_l_shields;
    List<PlayerShieldControl> my_l_shields;

    ObservableList<PlayerShieldControl> my_observ_list_shields;
    ObservableList<PlayerShieldControl> enemy_observ_list_shields;

    GamecardControl[] my_card_field;
    SpecialCardControl[] my_scard_field;


    GamecardControl[] enemy_card_field;
    SpecialCardControl[] enemy_scard_field;

    ComboBox gameCardsSelection;
    /**
     * KI ID
     */
    int enemyID = 1;
    /**
     * My ID
     */
    int myID = 2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        enemyID = 1;
        myID = 2;
        DbCard db = new DbCard();
        p1 = new Player(enemyID, "Dennis");
        p2 = new Player(myID, "David");
        //g = new Game();
        d1 = new Deck(1, "Flora", db.getDeck("Flora"));
        d2 = new Deck(2, "David", db.getDeck("civitas diaboli"));
        g = new Game(p1, p2, d1, d2);
        myField = g.getMyField(myID);
        enemyField = g.getEnemyField(myID);
        cardPreviewPane = new StackPane();
        cardPreviewPane.setMinHeight(200);
        cardPreviewPane.setMinWidth(100);

        my_card_field = new GamecardControl[4];
        my_scard_field = new SpecialCardControl[4];
        enemy_card_field = new GamecardControl[4];
        enemy_scard_field = new SpecialCardControl[4];

        try {
            setCardsOnHandEnemy();
            setCardsOnHandPlayer();
            setPlayerShields();
            setEnemyPlayerShields();
        } catch (LogicException ex) {
            Logger.getLogger(PlaygroundController.class.getName()).log(Level.SEVERE, null, ex);
        }
        clearStyle();
        setStyle();
        stretchElements();
        initGame();
        setBindings();
        Thread task = new GameThread(g);
        //task.start();
        g.changePlayer(myID);
        task.start();
    }

    /**
     * Stylesetzung auf Elemente.
     */
    private void setStyle() {
        StyleSetting.setDescriptionCss(description);
    }

    /**
     * Erzeugung von Spielerschildern
     *
     * @throws LogicException
     */
    private void setPlayerShields() throws LogicException {

        my_l_shields = new ArrayList();

        int size = g.getMyField(enemyID).getPlayer().getShields().getCurrentShields();
        for (int i = 0; i < size; i++) {
            my_l_shields.add(new PlayerShieldControl());

        }

        my_observ_list_shields = FXCollections.observableArrayList(my_l_shields);
        my_shields.setItems(my_observ_list_shields);
    }

    /**
     * Erzeugung von Schildern des Gegenüberspielers
     *
     * @throws LogicException
     */
    private void setEnemyPlayerShields() throws LogicException {

        enemy_l_shields = new ArrayList<>();

        int size = g.getEnemyField(enemyID).getPlayer().getShields().getCurrentShields();
        for (int i = 0; i < size; i++) {
            enemy_l_shields.add(new PlayerShieldControl());

        }

        enemy_observ_list_shields = FXCollections.observableArrayList(enemy_l_shields);
        enemy_shields.setItems(enemy_observ_list_shields);
        //System.out.println("enemy_observ_list size:" + enemy_observ_list_shields.size());
    }

    private void initGame() {
        myField.getDeck().setCountCards();
        myField.setObservableBattlegroundMonster();
        myField.setObservableBattlegroundSpecials();
        enemyField.getDeck().setCountCards();
        enemyField.setObservableCardsOnHand();
        enemyField.getDeck().setCountCards();
        enemyField.setObservableBattlegroundMonster();
        enemyField.setObservableBattlegroundSpecials();
        enemyField.setObservableCardsOnHand();
        myField.getPlayer().getShields().setgShield();
        enemyField.getPlayer().getShields().setgShield();
    }

    /**
     * Setzen der Handkarten des Gegenüber.
     *
     * @throws LogicException
     */
    private void setCardsOnHandEnemy() throws LogicException {
        enemy_l = new ArrayList();
        int size = g.getEnemyField(enemyID).getCardsOnHand().size();
        for (int i = 0; i < size; i++) {
            enemy_l.add(new EnemyGamecardControl());

        }
        enemy_observ_list = FXCollections.observableArrayList(enemy_l);
        enemy_cardsOnHand.setItems(enemy_observ_list);

    }

    void initCardsOnHandStart() {
        for (Card c : myField.getObservableCardsOnHand()) {
            if (c instanceof GameCard) {
                //GamecardControl gc = new GamecardControl((GameCard) c);
                /*gc.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        System.out.println("drin");
                        if (e.getButton() == MouseButton.SECONDARY){

                            setEnemyGameCardsComboBox(e.getSceneX(), e.getSceneY());
                        }
                    }
                });*/
                my_cardsOnHand.getItems().add(new GamecardControl((GameCard) c));

            } else {
                my_cardsOnHand.getItems().add(new SpecialCardControl((SpecialCard) c));
            }
        }
    }

    /**
     * Setzen der Handkarten bei dem Hauptspieler.
     * @throws LogicException
     */
    private void setCardsOnHandPlayer() {
        myField.setObservableCardsOnHand();
        initCardsOnHandStart();

        myField.getObservableCardsOnHand().addListener(new ListChangeListener<Card>() {
            @Override
            public void onChanged(Change<? extends Card> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        int from = change.getFrom();
                        Card c = change.getList().get(from);
                        if(c instanceof GameCard){
                            GamecardControl gc = new GamecardControl((GameCard) change.getList().get(from));
                            setCardOnMyHand(gc, from);
                        }else{

                            SpecialCardControl s = new SpecialCardControl((SpecialCard) change.getList().get(from));
                            setCardOnMyHand(s, from);
                        }

                    } else if(change.wasRemoved()){
                        int from = change.getFrom();
                        //Platform.runLater(()->my_cardsOnHand.getItems().remove(from));
                        my_cardsOnHand.getItems().remove(from);

                    }else{
                        for (int i = change.getFrom(); i < change.getTo(); i++) {
                            Card from = change.getList().get(i);
                            if(from instanceof GameCard){
                                GamecardControl gc = new GamecardControl((GameCard) from);
                                setCardOnMyHand(gc, i);
                            }else{

                                SpecialCardControl s = new SpecialCardControl((SpecialCard) from);
                                setCardOnMyHand(s, i);
                            }
                        }
                    }

                }
            }
        });

        my_cardsOnHand.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<CardControl>() {

                    @Override
                    public void changed(ObservableValue<? extends CardControl> ov, CardControl oldv, CardControl newv) {


                        ObservableList<Label> ob = FXCollections.observableArrayList(newv.getgDescription());
                        description.setItems(ob);
                        if (newv.getBg() != null) {
                            cardPreviewPane.setBackground(newv.getBg());
                        }
                        newv.getPlay().setDisable(false);
                        newv.getPlay().setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                try{
                                //setPlayersField(newv);

                                    //newv.getPlay().setVisible(false);
                                if(newv instanceof GamecardControl){


                                    g.playCard(myID, ((GamecardControl) newv).getCard());

                                    //((GamecardControl) newv).getFight().setVisible(true);

                                }else{

                                    SpecialCard s =  ((SpecialCardControl) newv).getCard();
                                    if(s.needGameCard()){
                                        //buttons
                                    }else{
                                        g.playCard(myID, s);
                                    }
                                }
                                }catch(LogicException ex){
                                    //POPUP Fenster
                                    Alert a  = new Alert(Alert.AlertType.INFORMATION);
                                    a.setContentText(ex.getMessage());
                                    a.show();
                                }
                            }
                        });

                    }
                });
    }

    private void setBindings() {
        initBindings();
        bindPlayground();
    }

    private void bindPlayground() {
        myField.getObservableBattlegroundMonster().addListener(new ListChangeListener<GameCard>() {
            @Override
            public void onChanged(Change<? extends GameCard> change) {
                while (change.next()) {
                    if (change.wasReplaced()) {
                        int from = change.getFrom();

                        GamecardControl c = new GamecardControl(change.getList().get(from));
                        c.getFight().setVisible(true);
                        c.getPlay().setVisible(false);
                        c.setId("card_css_chosen");
                        setCardOnMyField(c, from);
                        my_card_field[from] = c;
                    } else {
                        for (int i = change.getFrom(); i < change.getTo(); i++) {
                            GameCard from = change.getList().get(i);
                            // GamecardControl c = new GamecardControl("x/x", "y/y", from.getName(), from.getImage(), from.getEffects(), from.getEvoEffects(), true);
                            GamecardControl c = new GamecardControl(from);
                            setCardOnMyField(c, i);
                        }
                    }

                }
            }
        });
        myField.getObservableBattlegroundSpecials().addListener(new ListChangeListener<SpecialCard>() {
            @Override
            public void onChanged(Change<? extends SpecialCard> change) {
                while (change.next()) {
                    if (change.wasReplaced()) {
                        int from = change.getFrom();
                        SpecialCardControl c = new SpecialCardControl(change.getList().get(from));
                        setCardOnMyField(c, from);
                        my_scard_field[from] = c;
                    } else {
                        for (int i = change.getFrom(); i < change.getTo(); i++) {
                            SpecialCard from = change.getList().get(i);
                            SpecialCardControl c = new SpecialCardControl(from);
                            setCardOnMyField(c, i);
                        }
                    }

                }
            }
        });
        enemyField.getObservableBattlegroundMonster().addListener(new ListChangeListener<GameCard>() {
            @Override
            public void onChanged(Change<? extends GameCard> change) {
                while (change.next()) {
                    if (change.wasReplaced()) {
                        int from = change.getFrom();
                        GamecardControl c = new GamecardControl(change.getList().get(from));
                        setCardsOnEnemyField(c, from);
                        enemy_card_field[from] = c;
                    } else {
                        for (int i = change.getFrom(); i < change.getTo(); i++) {
                            GameCard from = change.getList().get(i);
                            GamecardControl c = new GamecardControl(from);
                            setCardsOnEnemyField(c, i);
                        }
                    }

                }
            }
        });
        enemyField.getObservableBattlegroundSpecials().addListener(new ListChangeListener<SpecialCard>() {
            @Override
            public void onChanged(Change<? extends SpecialCard> change) {
                while (change.next()) {
                    if (change.wasReplaced()) {
                        int from = change.getFrom();
                        SpecialCardControl c = new SpecialCardControl(change.getList().get(from));
                        setCardsOnEnemyField(c, from);
                        enemy_scard_field[from] = c;
                    } else {
                        for (int i = change.getFrom(); i < change.getTo(); i++) {
                            SpecialCard from = change.getList().get(i);
                            // GamecardControl c = new GamecardControl("x/x", "y/y", from.getName(), from.getImage(), from.getEffects(), from.getEvoEffects(), true);
                            SpecialCardControl c = new SpecialCardControl(from);
                            setCardsOnEnemyField(c, i);
                        }
                    }

                }
            }
        });
    }


    /**
     * Erzeugung der Abhängigkeiten/Bindings.
     */
    private void initBindings() {
        //Bindings
        //Player Namen
        player1.setText(g.getMyField(enemyID).getPlayer().getName());
        player2.setText(g.getEnemyField(enemyID).getPlayer().getName());
        //Kartenanzahl Binding
        countCards2.textProperty().bind(g.getEnemyField(enemyID).getDeck().getCountCards().asString());
        countCards1.textProperty().bind(g.getMyField(enemyID).getDeck().getCountCards().asString());

        BindSetting.bindPhases(main1, battle1, end1, g.getMyPhase(enemyID), g.getEnemyPhase(enemyID));
        BindSetting.bindPhases(main2, battle2, end2, g.getEnemyPhase(enemyID), g.getMyPhase(enemyID));


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
        StyleSetting.setPrefSizeMax(main1);
        StyleSetting.setPrefSizeMax(main2);
        StyleSetting.setPrefSizeMax(end1);
        StyleSetting.setPrefSizeMax(end2);
        StyleSetting.setPrefSizeMax(battle1);
        StyleSetting.setPrefSizeMax(battle2);

        StyleSetting.setPrefSizeMax(countCards1);
        StyleSetting.setPrefSizeMax(countCards2);

        StyleSetting.setPrefSizeMax(enemy_specialcard1);
        StyleSetting.setPrefSizeMax(enemy_specialcard2);
        StyleSetting.setPrefSizeMax(enemy_specialcard3);
        StyleSetting.setPrefSizeMax(enemy_specialcard4);

        StyleSetting.setPrefSizeMax(my_specialcard1);
        StyleSetting.setPrefSizeMax(my_specialcard2);
        StyleSetting.setPrefSizeMax(my_specialcard3);
        StyleSetting.setPrefSizeMax(my_specialcard4);

        StyleSetting.setPrefSizeMax(enemy_card1);
        StyleSetting.setPrefSizeMax(enemy_card2);
        StyleSetting.setPrefSizeMax(enemy_card3);
        StyleSetting.setPrefSizeMax(enemy_card4);

        StyleSetting.setPrefSizeMax(my_card1);
        StyleSetting.setPrefSizeMax(my_card2);
        StyleSetting.setPrefSizeMax(my_card3);
        StyleSetting.setPrefSizeMax(my_card4);

        cardPreviewPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

    }

    /**
     * Entfernung des vordefinierten Styles.
     */
    private void clearStyle() {
        enemy_shields.getStyleClass().clear();
        my_shields.getStyleClass().clear();
        my_cardsOnHand.getStyleClass().clear();
        enemy_cardsOnHand.getStyleClass().clear();

    }


    private void setCardOnMyHand(CardControl gc, int where) {
        if (gc == null) {
            throw new IllegalArgumentException("setPlayersField(GamecardControl gc) ist null");
        }
        if (gc instanceof GamecardControl) {
            //my_card_field[where] = (GamecardControl) gc;
            my_cardsOnHand.getItems().add(where, (GamecardControl)gc);

        } else {
            //my_scard_field[i] = (SpecialCardControl) gc;
            my_cardsOnHand.getItems().add(where, (SpecialCardControl)gc);
        }
    }


    private void setCardOnMyField(CardControl gc, int where) {
        if (gc == null) {
            throw new IllegalArgumentException("setPlayersField(GamecardControl gc) ist null");
        }
        if (gc instanceof GamecardControl) {
            //my_card_field[where] = (GamecardControl) gc;
            gridPlayGround.add(gc, 4 + (2 * where), 7, 2, 1);

        } else {
                //my_scard_field[i] = (SpecialCardControl) gc;
                gridPlayGround.add(gc, 4 + (2 * where), 9, 2, 2);
        }
    }

    private void setCardsOnEnemyField(CardControl gc, int where) {
        if (gc == null) {
            throw new IllegalArgumentException("setPlayersField(GamecardControl gc) ist null");
        }
        if (gc instanceof GamecardControl) {
            //my_card_field[where] = (GamecardControl) gc;
            gridPlayGround.add(gc, 4 + (2 * where), 5, 2, 1);

        } else {
                //my_scard_field[i] = (SpecialCardControl) gc;
                gridPlayGround.add(gc, 4 + (2 * where), 3, 2, 1);
        }


    }

}
