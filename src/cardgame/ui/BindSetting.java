package cardgame.ui;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/** Klasse zur Bindverwaltung.
 */
public class BindSetting {


    /**
     * Binding von Phasen
     *
     * @param m      Main-Phase Button
     * @param b      Battle-Phase Button
     * @param e      End-Phase Button
     * @param phase1 Phase des 1. Spielers
     * @param phase2 Phase des 2. Spielers
     */
    static public void bindPhases(Button m, Button b, Button e, IntegerProperty phase1, IntegerProperty phase2) {
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

    /**
     * Labelbinding
     * @param l Label
     * @param s Text
     */
    public static void bindLabel(Label l, StringBinding s){
        l.textProperty().bind(s);
    }

}
