package cardgame.ui;

import cardgame.ai.KiPlayer;
import cardgame.classes.GameEndException;
import cardgame.logic.Game;
import cardgame.logic.LogicException;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Klasse zur Bindverwaltung.
 */
public class BindSetting {

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    /**
     * Binding von Phasen
     *
     * @param m      Main-Phase Button
     * @param b      Battle-Phase Button
     * @param e      End-Phase Button
     * @param phase Phase
     */
    static public void bindPhases(Game game, int id,int enemyId, Button m, Button b, Button e, IntegerProperty phase,KiPlayer kiPlayer) {
        m.disableProperty().bind(new BooleanBinding() {
            {
                bind(phase);
            }

            @Override
            protected boolean computeValue() {
                System.out.println("main: " + (phase.getValue() != 0));

                return phase.getValue() != 0;
            }
        });

        b.disableProperty().bind(new BooleanBinding() {
            {
                bind(phase);
            }

            @Override
            protected boolean computeValue() {

                return phase.getValue() == 2; // Hinweis: nicht in einzel Variable abspeichert
            }
        });
        e.disableProperty().bind(new BooleanBinding() {
            {
                bind(phase);
            }

            @Override
            protected boolean computeValue() {

                boolean endBool = (phase.getValue() == 2);
                if(endBool){
                    System.out.println(game.getPlayersTurn() + "-" + id);
                    if(game.getPlayersTurn() != id){
                        //try {
                            //game.getMyField(id).addCard(); addCard wird in change jetzt in change Player durchgefuehrt
                            //System.out.println(">"+(kiPlayer != null) + ":"+ kiPlayer.getId());
                            if(kiPlayer != null){
                                executorService.execute(()->{
                                    try {
                                        kiPlayer.yourTurn();
                                    }catch (LogicException ex){
                                        System.out.println(ex);
                                    }
                                    game.changePlayer(id); //TODO Spiel kann an dieser Stelle zu Ende sein

                                });
                            }
                       /* }catch(GameEndException ex){
                            game.setPlayerWon(enemyId);
                            //StyleSetting.printAlertWindow(ex);
                        } */
                    }
                }
                return endBool;
            }
        });

    }

    /**
     * Labelbinding
     * @param l Label
     * @param s Text
     */
    public static void bindLabel(Label l, StringBinding s){
        l.textProperty().bind(s);
    }

    /**
     * Labelbinding
     * @param l Label
     * @param s Text
     */
    public static void bindLabel(Label l, StringProperty s){
        l.textProperty().bind(s);
    }

    public static void unbindLabel(Label l){
        l.textProperty().unbind();
    }


    /**
     * Binding eines Buttons.
     *
     * @param b  Button
     * @param ip IntegerProperty
     * @param v  geaenderte Wert
     */
    public static void bindButtonPerValue(Button b, IntegerProperty ip, int v) {
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


}
