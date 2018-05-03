package cardgame.classes;


import java.util.*;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;


/**
 * Klasse zur Abbildung der Struktur einer Spielkarte.
 *
 * @author BishaThan
 */
public class GameCard extends Card {

    public static GameCard DUMMY = new GameCard();

    /**
     * Angriffspunkte.
     **/
    private int atk;
    /**
     * Notwendige und erreichte Schilder fuer Evolution der Karte.
     **/
    private final Shield evolutionShields;
    /**
     * Schutzschilder der Karte.
     **/
    private final Shield shields;
    /**
     * Kartenevolution.
     **/
    private final GameCard evolution;
    /**
     * Karteneffekte.
     **/
    private final Effect[] effects;
    /**
     * EvoEffekte
     **/
    private final Effect[] evoEffects;
    /**
     * Effect der noch ausgefuert werden muss
     **/
    private Effect nextEffect = null;

    /*Angriffspunkte zum GUI Binden*/
    private SimpleIntegerProperty pAtk;


    /**
     * Konstruktor
     *
     * @param id               Identifikationsnummer
     * @param name             Kartenname
     * @param description      Kartenbeschreibung
     * @param type             Kartentyp
     * @param atk              Angriffspunkte
     * @param evolutionShields Evolutionsschilder
     * @param shields          Schutzschilder der Spielkarte
     * @param evolution        Evolutionskarte
     * @param effects          Karteneffekte
     */
    public GameCard(final int id, final String name, final String description, final Type type, final byte[] image, final int atk, final Shield evolutionShields, final Shield shields, final GameCard evolution, final Effect[] effects, final Effect[] evoEffects) {
        super(id, name, description, type, image);
        this.pAtk = null;
        this.atk = atk;
        this.evolutionShields = new Shield(evolutionShields);
        this.shields = new Shield(shields);
        if(evolution != null){
            this.evolution = new GameCard(evolution);
        }else this.evolution = null;
        this.effects = effects;
        this.evoEffects = evoEffects;
    }

    /**
     * Copy-Konstruktor
     *
     * @param c Spielkarte
     */
    public GameCard(final GameCard c) {
        this(c.getCid(), c.getName(), c.getDescription(), c.getType(), c.getImage(), c.getAtk(), c.getEvolutionShields(), c.getShields(), c.getEvolution(), c.getEffects(), c.getEvoEffects());
    }

    /*
        /**
         * Basiskonstruktor
         *
         */
    public GameCard() {
        this(-1, "", "", Type.human, new byte[1], 0, new Shield(0), new Shield(0), null, new Effect[0], new Effect[0]);
    }

    /**
     * Ändert die Atk Punkte um den Wert add.
     *
     * @param add Die Atk punkte die hinzugefügt oder abgezogen werden sollen.
     */
    public void changeAtk(int add) {
        this.atk += add;
        if (atk < 0) atk = 0;
        if(pAtk != null) changepAtk();
    }

    public boolean isAlive() {
        return shields.getCurrentShields() > 0;
    }

    public int getAtk() {
        return atk;
    }

    public Shield getEvolutionShields() {
        return evolutionShields;
    }

    public Shield getShields() {
        return shields;
    }

    public GameCard getEvolution() {
        return evolution;
    }

    public Effect[] getEffects() {
        return effects;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public Effect[] getEvoEffects() {
        return evoEffects;
    }


    public IntegerProperty getpAtk() {
        return pAtk;
    }


    private void changepAtk() {
            Platform.runLater(()->this.pAtk.setValue(this.atk));
    }

    public void setpAtk(){
        this.pAtk = new SimpleIntegerProperty(atk);
    }


    public SimpleIntegerProperty pAtkProperty() {
        return pAtk;
    }

    /**
     * Erniedrigt die Schilder um ein Schild.
     * Prueft, ob dadurch ein Effect ausgeloest wird.
     * Dieser muss mit der Methode getNextEffect geholt und ausgefuehrt werden.
     *
     * @return true(wenn Karte noch am Leben) false(falls Karte keine Schilder mehr besitzt)
     */
    public boolean dropShield() {
        if (shields.dropShield()) {
            int shield = shields.getCurrentShields();
            if (nextEffect != null) {
                throw new RuntimeException("Alter Effect wurde noch nicht ausgefuert");
            }
			/*if(effects.length <= shield) {
				System.out.println(this);
				System.out.println("EffectArray: " + effects.length + " CurrentShields: " + shield + " MaxShield: " + shields.getMaxShields());
			}*/
            if (shield == 0) {
                return false;
            } else {
                //Es gibt keine Effekt wenn die Karte tot ist.
                nextEffect = effects[shield - 1];
                return true;
            }
        }
        return false;
    }

    /**
     * Erhoeht die EvolutionSchilder um eins.
     * Prueft, ob dadurch ein Effect ausgeloest wird.
     * Dieser muss mit der Methode getNextEffect geholt und ausgefuehrt werden.
     *
     * @return GameCard fuer Evolution(wenn evolutionShields maximal und Evolution moeglich) sonst null
     */
    public GameCard addEvoShield() {
        int shield = evolutionShields.getCurrentShields();
        if (evolutionShields.addShield()) {
            //indexoutofboundsexception ??
            //shield = evolutionShields.getCurrentShields();
            if (nextEffect != null) {
                throw new RuntimeException("Alter Effect wurde noch nicht ausgefuert");
            }
            nextEffect = evoEffects[shield];
        }
        if (evolutionShields.getCurrentShields() == evolutionShields.getMaxShields()) {
            return evolution;
        }
        return null;
    }

    /**
     * Gibt Effect zuruek, der noch ausgefuehrt werden muss.
     * Setzt dabei nextEffect wieder auf null.
     *
     * @return
     */
    public Effect getNextEffect() {
        Effect e = nextEffect;
        nextEffect = null;
        return e;
    }

    //
//    /**
//     * Getter 
//     * @return Angrisspunkte 
//     */
//    public Label getgAtk() {
//        return gAtk;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameCard gameCard = (GameCard) o;

        if (atk != gameCard.atk) return false;
        if (!Objects.equals(evolutionShields, gameCard.evolutionShields)) return false;
        if (!Objects.equals(shields, gameCard.shields)) return false;
        return true;
    }

    @Override
    public int compareTo(Card card){
        int wert = super.compareTo(card);
        if(wert != 0) return wert;
        //Geht davon aus das GameCard und Specialcards verschieden cIds haben
        if(card instanceof SpecialCard) return -1;
        GameCard gameCard = (GameCard) card;
        if(atk != gameCard.atk) return atk - gameCard.atk;
        if(shields.getCurrentShields() != gameCard.shields.getCurrentShields())
            return  shields.getCurrentShields() - gameCard.shields.getCurrentShields();
        if(evolutionShields.getCurrentShields() != gameCard.evolutionShields.getCurrentShields())
            return evolutionShields.getCurrentShields() - gameCard.evolutionShields.getCurrentShields();
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), atk, evolutionShields, shields);
    }

    @Override
    public String toString() {
        return super.toString() + " " + getAtk() + " " + getEvolutionShields() + " " + getShields() + " \nEvo: " + getEvolution() + "\nEffects: " + Arrays.toString(getEffects()) + "\nEvoEffects: " + Arrays.toString(evoEffects) + "\n\n";
    }


}
