package cardgame.ui;

import javafx.scene.control.Control;
import javafx.scene.control.ListView;

/**
 * Klasse zum Stylen der Controllelemente
 */
public class StyleSetting {

    /**
     * Hebt eine Karte vom Aussehen hervor.
     * @param c Karte
     */
    public static void highlightCard(CardControl c){
        c.setId("card_css_chosen");
        c.getgName().setId("card_name");
    }


    /**
     * Rücksetzung des Kartenaussehen auf Anfangszustand.
     * @param c Karte
     */
    public static void resetCardCss(CardControl c){
        c.setId("card_css");
        c.getgName().setId("card_name");
    }


    /**
     * Beschreibungsaussehen setzen.
     * @param l
     *
     */
    public static void setDescriptionCss(ListView l){
        l.setId("description_css");
    }


    /**
     * Hilfsmethode zur Maximierung der Groesse im Parentcontainer.
     *
     * @param c Control
     */
    public static void setPrefSizeMax(Control c) {
        c.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

    }

}
