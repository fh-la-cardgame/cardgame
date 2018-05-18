/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

Log4J - Apache
 */
package cardgame.ui;

import cardgame.ai.KiPlayer;
import cardgame.ai.RandomPlayer;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javax.swing.text.Style;

/**
 * FXML Controller class
 *
 * @author BishaThan
 */
public class PlaygroundController implements Initializable {

    @FXML
    private GridPane gridPlayGround;
    @FXML
    private Button enemy_main;
    @FXML
    private Button enemy_battle;
    @FXML
    private Button enemy_end;
    @FXML
    private Button my_main;
    @FXML
    private Button my_battle;
    @FXML
    private Button my_end;
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

    GamecardControl myChosenGameCard;
    SpecialCardControl myChosenSpecialCard;

    EventHandler<MouseEvent> highlightMouseEvent;
    EventHandler<MouseEvent> highlightMouseEventSpecialCard;


    Thread task;

    int enemyID = 1;
    int myID = 2;

    private KiPlayer kiPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        DbCard db = new DbCard();
        p1 = new Player(enemyID, "Dennis");
        p2 = new Player(myID, "David");
        //g = new Game();
        d1 = new Deck(1, "Flora", db.getDeck("Flora"));
        d2 = new Deck(2, "David", db.getDeck("civitas diaboli"));
        g = new Game(p1, p2, d1, d2);
        kiPlayer = new RandomPlayer(g,enemyID);
        myField = g.getMyField(myID);
        enemyField = g.getEnemyField(myID);
        cardPreviewPane = new StackPane();




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
        task = new GameThread(g);
        g.changePlayer(myID);


        //task.start();

        //TEST
    }

    /**
     * Stylesetzung auf Elemente.
     */
    private void setStyle() {
        StyleSetting.setDescriptionCss(description);
        StyleSetting.setButtonCss(enemy_main);
        StyleSetting.setButtonCss(my_main);
        StyleSetting.setButtonCss(my_battle);
        StyleSetting.setButtonCss(enemy_battle);
        StyleSetting.setButtonCss(my_end);
        StyleSetting.setButtonCss(enemy_end);


        cardPreviewPane.setMinHeight(200);
        cardPreviewPane.setMinWidth(100);

        my_cardsOnHand.maxHeight(130);
        my_cardsOnHand.minHeight(130);
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

        int size = enemyField.getPlayer().getShields().getCurrentShields();
        for (int i = 0; i < size; i++) {
            enemy_l_shields.add(new PlayerShieldControl());

        }

        enemy_observ_list_shields = FXCollections.observableArrayList(enemy_l_shields);
        enemy_shields.setItems(enemy_observ_list_shields);
        //System.out.println("enemy_observ_list size:" + enemy_observ_list_shields.size());
    }

    private void initGame() {
        g.setPlayerPhases();
        g.setPlayerWonProperity();
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

    /**
     * Karten auf der Hand initialisieren.
     */
    void initCardsOnHandStart() {
        for (Card c : myField.getObservableCardsOnHand()) {
            GamecardControl gc;
            SpecialCardControl sc;
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
                gc = new GamecardControl((GameCard) c);
                setActionOnPlay(gc);
                my_cardsOnHand.getItems().add(gc);

            } else {
                sc = new SpecialCardControl((SpecialCard) c);
                setActionOnPlay(sc);
                my_cardsOnHand.getItems().add(sc);
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

                        if(newv != null){
                            ObservableList<Label> ob = FXCollections.observableArrayList(newv.getgDescription());
                            description.setItems(ob);
                            if (newv.getBg() != null) {
                                //System.out.println("Neuer Hintergrund");
                                //cardPreviewPane.setBackground(new Background(new BackgroundImage(new Image(new ByteArrayInputStream(newv.getBg().get)), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
                                //// cardPreviewPane.setBackground(newv.getBg());
                            }
                        }



                    }
                });
    }

    private void setBindings() {
        initBindings();
        bindPlayground();
        initEvents();
    }

    private void initEvents() {
        highlightMouseEvent = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY){
                    resetHighlightOnEnemyGameCards();
                }else if(e.getButton() == MouseButton.PRIMARY){
                    try {
                        g.attack(myID, myChosenGameCard.getCard(), ((GamecardControl)e.getSource()).getCard());
                        disableFightButton(myChosenGameCard);
                        resetHighlightOnEnemyGameCards();
                        setDisabledCardsOnHand();
                    } catch (LogicException ex) {
                        enableFightButton(myChosenGameCard);
                        resetDisabledCardsOnHand();
                        StyleSetting.printAlertWindow(ex);
                    }
                }
            }
        };

        highlightMouseEventSpecialCard = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                    resetHighlightOnMyEnemyGameCards();
                } else if (e.getButton() == MouseButton.PRIMARY) {
                    //FRAGE dachte wirkt sich auf meine Karten aus
                    try {
                        g.playSpecialCard(myID, myChosenSpecialCard.getCard(), ((GamecardControl) e.getSource()).getCard());
                        resetHighlightOnMyEnemyGameCards();
                    } catch (LogicException ex) {

                        StyleSetting.printAlertWindow(ex);
                    }
                }
            }
        };
    }

    private void bindPlayground() {

        //Zurücksetzen aller aktiven Kartenwirkungen
        gridPlayGround.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY){
                    resetHighlightOnEnemyGameCards();
                    resetHighlightOnMyEnemyGameCards();
                }
            }
        });




        myField.getObservableBattlegroundMonster().addListener(new ListChangeListener<GameCard>() {
            @Override
            public void onChanged(Change<? extends GameCard> change) {
                while (change.next()) {
                    if (change.wasReplaced()) {
                        int from = change.getFrom();

                        GamecardControl c = new GamecardControl(change.getList().get(from));

                        setCardOnMyField(c, from);
                        if(my_card_field[from] != null) my_card_field[from].unbindAll();
                        my_card_field[from] = c;

                        c.getPlay().setVisible(false);
                        c.getFight().setVisible(true);

                        //Fehlerfahl/Ausnahmefall, wenn unsere Karte zerstört wird, wird diese derzeit
                        //durch eine leere Karte mit 0/0 0/0 ersetzt, in diesem Fall sollte kein Reset
                        //der Karten auf der Hand ausgeführt werden, auch kein Button zum Kampf bereit gestellt werden
                        if(!(c.getgName().getText().isEmpty())){//Leere Karte auf dem Feld
                            if(g.getRound() == 0){
                                //Ausnahmefall, wenn der Erste Zug, dann soll das Spielen mit der Karte nicht möglich sein
                                disableFightButton(c);
                            }else{
                                enableFightButton(c);
                            }
                            //Karten auf der Hand ausblenden, weil nur eine Monsterkarte pro Zug gespielt werden darf
                            setDisabledGamecardsOnHand();
                        }else{
                            disableFightButton(c);
                        }

                        setActionOnFight(c);
                        c.bindAll();
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
                        c.getPlay().setVisible(false);
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
                        if(enemy_card_field[from] != null) enemy_card_field[from].unbindAll();
                        enemy_card_field[from] = c;
                        c.bindAll();
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

        g.getPlayerWonProperity().addListener(new ChangeListener<Number>() {
                                                  @Override
                                                  public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                                      StyleSetting.printWinWindow("Spieler " + newValue + " hat gewonnen!");
                                                  }
                                              }

        );

        //Event zur Schildänderung des Gegners setzen
        p1.getShields().getgShield().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int curShield = p1.getShields().getCurrentShields();
                while((curShield >= 0) && (curShield < enemy_shields.getItems().size())){
                    enemy_shields.getItems().remove(0);
                }

                if(curShield == 0){
                    StyleSetting.printWinWindow("Sie haben gewonnen");
                }
            }
        });

        //Event zur Schildänderung des Spielers setzen
        p2.getShields().getgShield().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int curShield = p2.getShields().getCurrentShields();
                while((curShield >= 0) && (curShield < my_shields.getItems().size())){
                    my_shields.getItems().remove(0);
                }

                if(curShield == 0){
                    StyleSetting.printWinWindow("Sie haben verloren");
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
        BindSetting.bindLabel(countCards2, g.getEnemyField(enemyID).getDeck().getCountCards().asString());
        BindSetting.bindLabel(countCards1,g.getMyField(enemyID).getDeck().getCountCards().asString());

        BindSetting.bindPhases(g, enemyID,myID, enemy_main, enemy_battle, enemy_end, g.getMyPhase(enemyID), null);
        BindSetting.bindPhases(g, myID,enemyID, my_main, my_battle, my_end, g.getEnemyPhase(enemyID),kiPlayer);


    }

    @FXML
    private void enemyBattleAction(ActionEvent event) {
        //g.setpPlayer1Phase(1);
    }

    @FXML
    private void enemyEndAction(ActionEvent event) {
        g.changePlayer(myID);
    }

    @FXML
    private void myBattleAction(ActionEvent event) {
        //g.setpPlayer2Phase(1);
    }

    @FXML
    private void myEndAction(ActionEvent event) {
         g.changePlayer(enemyID);
         resetHighlightOnEnemyGameCards();
         resetHighlightOnMyEnemyGameCards();
        resetDisabledGameCards();
        resetDisabledCardsOnHand();

    }

    /**
     * Sizing an das Parentelement.
     */
    private void stretchElements() {
        StyleSetting.setPrefSizeMax(enemy_main);
        StyleSetting.setPrefSizeMax(my_main);
        StyleSetting.setPrefSizeMax(enemy_end);
        StyleSetting.setPrefSizeMax(my_end);
        StyleSetting.setPrefSizeMax(enemy_battle);
        StyleSetting.setPrefSizeMax(my_battle);

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


    private void setCardOnMyHand(CardControl c, int where) {
        if (c == null) {
            throw new IllegalArgumentException("setPlayersField(GamecardControl gc) ist null");
        }

        setActionOnPlay(c);

        if (c instanceof GamecardControl) {
            //my_card_field[where] = (GamecardControl) gc;
            my_cardsOnHand.getItems().add(where, (GamecardControl)c);

        } else {
            //my_scard_field[i] = (SpecialCardControl) gc;
            my_cardsOnHand.getItems().add(where, (SpecialCardControl)c);

        }
    }


    private void setActionOnPlay(CardControl c){
        //Action Event zum Spielen der Karte

        c.getPlay().setVisible(true);
        if(c instanceof GamecardControl){
        c.getPlay().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try{
                        GamecardControl gc = (GamecardControl) c;
                        c.getPlay().setVisible(false);
                        g.playCard(myID, gc.getCard());


                }catch(LogicException ex){
                    //POPUP Fenster
                    StyleSetting.printAlertWindow(ex);
                    c.getPlay().setVisible(true);
                }catch(RuntimeException ex){

                    StyleSetting.printAlertWindow(ex);
                    c.getPlay().setVisible(true);
                }
            }
        });
        }else{
            setOnActionOnPlaySpecialCard((SpecialCardControl) c);
        }

    }

    private void resetHighlightOnEnemyGameCards(){

        for(int i = 0; i < enemy_card_field.length; i++){
            if(enemy_card_field[i] != null && !(enemy_card_field[i].getgName().textProperty().isEmpty().get())) {
                StyleSetting.resetCardCss(enemy_card_field[i]);
                enemy_card_field[i].removeEventHandler(MouseEvent.MOUSE_CLICKED, highlightMouseEvent);
            }
        }
    }
    private void resetHighlightOnMyEnemyGameCards(){

        for(int i = 0; i < my_card_field.length; i++){
            if(my_card_field[i] != null && !(my_card_field[i].getgName().textProperty().isEmpty().get())) {
                StyleSetting.resetCardCss(my_card_field[i]);
                my_card_field[i].removeEventHandler(MouseEvent.MOUSE_CLICKED, highlightMouseEventSpecialCard);
            }
            if(enemy_card_field[i] != null && !(enemy_card_field[i].getgName().textProperty().isEmpty().get())) {
                StyleSetting.resetCardCss(enemy_card_field[i]);
                enemy_card_field[i].removeEventHandler(MouseEvent.MOUSE_CLICKED, highlightMouseEventSpecialCard);
            }
        }
    }

    private void setActionOnFight(GamecardControl c){

        //Action Event zum Spielen der Karte
        c.getFight().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                    myChosenGameCard = c;
                    if(g.getEnemyField(myID).getCountBattlegroundMonster() == 0){
                        try {
                            g.attack(myID, c.getCard(), null);
                            setDisabledCardsOnHand();
                            disableFightButton(c);
                        }catch (LogicException ex){
                            resetDisabledCardsOnHand();
                            enableFightButton(c);
                            StyleSetting.printAlertWindow(ex);
                        }
                        return;
                    }
                    for(int i = 0; i < enemy_card_field.length; i++){
                        if(enemy_card_field[i] != null && !(enemy_card_field[i].getgName().textProperty().isEmpty().get())){
                            StyleSetting.highlightCard(enemy_card_field[i]);



                            enemy_card_field[i].addEventHandler(MouseEvent.MOUSE_CLICKED, highlightMouseEvent);

                        }
                    }
                }

        });

    }

    private void setOnActionOnPlaySpecialCard(SpecialCardControl s){
        SpecialCard sc =  s.getCard();
        if(sc.needGameCard()){
            s.getPlay().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    myChosenSpecialCard = s;



                    for (int i = 0; i < my_card_field.length; i++) {
                        if (my_card_field[i] != null && !(my_card_field[i].getgName().textProperty().isEmpty().get())) {
                            StyleSetting.highlightCard(my_card_field[i]);

                            my_card_field[i].addEventHandler(MouseEvent.MOUSE_CLICKED, highlightMouseEventSpecialCard);

                        }
                        if(enemy_card_field[i] != null && !(enemy_card_field[i].getgName().textProperty().isEmpty().get())){

                            StyleSetting.highlightCard(enemy_card_field[i]);

                            enemy_card_field[i].addEventHandler(MouseEvent.MOUSE_CLICKED, highlightMouseEventSpecialCard);
                        }
                    }

                }});
            }else{
                try {
                    g.playCard(myID, sc);
                    s.getPlay().setVisible(false);
                } catch (LogicException ex) {
                    s.getPlay().setVisible(true);
                    StyleSetting.printAlertWindow(ex);
                }
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


    private void resetDisabledGameCards(){
        for(int i = 0 ; i < my_card_field.length; i++){
            if(my_card_field[i] != null){
                enableFightButton(my_card_field[i]);
            }
        }
    }

    private void resetDisabledCardsOnHand(){
        for(CardControl c : my_cardsOnHand.getItems()){
            if(c != null){
                c.setDisable(false);
            }
        }
    }


    private void setDisabledGamecardsOnHand(){
        for(CardControl c : my_cardsOnHand.getItems()){
            if(c != null && (c instanceof GamecardControl)){
                c.setDisable(true);
            }
        }
    }


    private void setDisabledCardsOnHand(){
        for(CardControl c : my_cardsOnHand.getItems()){
            if(c != null){
                c.setDisable(true);
            }
        }
    }


    private void disableFightButton(GamecardControl c){
        c.getFight().setDisable(true);
    }
    private void enableFightButton(GamecardControl c){
        c.getFight().setDisable(false);
    }

    private void disablePlayButton(CardControl c){
        c.getPlay().setDisable(true);
    }
    private void enablePlayButton(CardControl c){
        c.getPlay().setDisable(false);
    }
}
