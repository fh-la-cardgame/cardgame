package cardgame.ui;

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
}
