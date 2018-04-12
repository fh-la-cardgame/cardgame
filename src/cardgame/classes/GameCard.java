package cardgame.classes;


import java.util.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


/**
 * Klasse zur Abbildung der Struktur einer Spielkarte.
 *
 * @author BishaThan
 */
public class GameCard extends Card {

    /**
     * Angriffspunkte.
     **/
    private int atk;
//    /**
//     * Angriffspunkte, GUI ATK
//     **/
//    private IntegerProperty pAtk;
    
    /* Angriffstaerke **/
    private Label gAtk;
    /* Button zur Kampfsteuerung **/
    private Button fight;
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
    /**
     * Liste aller SpecialCards die auf diese Karte wirken.
     */
    private Set<SpecialCard> specialCards;


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
        //this.pAtk = new SimpleIntegerProperty(atk);
        this.atk = atk;
        this.evolutionShields = new Shield(evolutionShields);
        this.shields = new Shield(shields);
        this.evolution = evolution;
        this.effects = effects.clone();
        this.evoEffects = evoEffects;
        this.specialCards = new IdentityHashSet<>();
        
        
        //GUI 
        this.gAtk = new Label(Integer.toString(atk));
        this.fight = new Button("Kaempfen");
        this.fight.setVisible(false);

        if (effects != null && effects.length > 0) {
            getgDescription().add(new Label("SCHWARZE SCHILDEFFEKTE:"));
            for (int i = 0; i < effects.length; i++) {
                if (effects[i] != null) {

                   getgDescription().add(new Label(effects[i].getDescription()));
                }

            }

            getgDescription().add(new Label("***************************************"));
        }

        if (evoEffects != null && evoEffects.length > 0) {
            getgDescription().add(new Label("EVO SCHILDEFFEKTE:"));
            for (int i = 0; i < evoEffects.length; i++) {
                if (evoEffects[i] != null) {

                    getgDescription().add(new Label(evoEffects[i].getDescription()));
                }

            }

            getgDescription().add(new Label("***************************************"));
        }
        
        positionAdditionalElements();
        setAdditionalSizesAndPosition();

    }

    private GameCard(final int id, final String name, final String description, final Type type, final byte[] image, final int atk, final Shield evolutionShields, final Shield shields, final GameCard evolution, final Effect[] effects, final Effect[] evoEffects, final Set<SpecialCard> specialcard) {
       this(id, name, description, type, image, atk, evolutionShields, shields, evolution, effects, evoEffects);
        //this.pAtk = new SimpleIntegerProperty(atk);
//        this.atk = atk;
//        this.evolutionShields = new Shield(evolutionShields);
//        this.shields = new Shield(shields);
//        this.evolution = evolution;
//        this.effects = effects.clone();
//        this.evoEffects = evoEffects;
        this.specialCards = new IdentityHashSet<>(specialcard);
    }

    /**
     * Copy-Konstruktor
     *
     * @param c Spielkarte
     */
    public GameCard(final GameCard c) {
        this(c.getCid(), c.getName(), c.getDescription(), c.getType(), c.getImage(), c.getAtk(), c.getEvolutionShields(), c.getShields(), c.getEvolution(), c.getEffects(), c.getEvoEffects());
    }

    /**
     * Platzhalterklasse - Definiert eine leere Karte als Schablone
     */
    public GameCard(){
        super();
        this.atk = 0;
        this.evolutionShields = new Shield(0);
        this.shields = new Shield(0);
        this.evolution = null;
        this.effects = null;
        this.evoEffects = null;
        this.specialCards = null;
        
        
        //GUI 
        this.gAtk = new Label("");
        this.fight = new Button("");
    }
    /**
     * Ändert die Atk Punkte um den Wert add.
     *
     * @param add Die Atk punkte die hinzugefügt oder abgezogen werden sollen.
     */
    public void changeAtk(int add) {
        this.atk += add;
        if (atk < 0) atk = 0;
        //setpAtk(atk);
        getgAtk().setText(Integer.toString(this.atk));
    }

    public void addSpecialCard(SpecialCard s) {
        if(!specialCards.contains(s)) specialCards.add(s);
    }

    public void removeSpecialCard(SpecialCard s) {
        Iterator<SpecialCard> iterator = specialCards.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == s) {
                iterator.remove();
                return;
            }
        }
    }

    public Set<SpecialCard> getSpecialCards() {
        return specialCards;
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
        return effects; // Offene Frage: Clonen ?
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public Effect[] getEvoEffects() {
        return evoEffects;    //Offene Frage: Clonen?
    }

//    public IntegerProperty getpAtk() {
//        return pAtk;
//    }
//
//    public void setpAtk(int atk) {
//        this.pAtk.setValue(atk);
//    }

    @Override
    public String toString() {
        return super.toString() + " " + getAtk() + " " + getEvolutionShields() + " " + getShields() + " \nEvo: " + getEvolution() + "\nEffects: " + Arrays.toString(getEffects()) + "\nEvoEffects: " + Arrays.toString(evoEffects) + "\n\n";
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
    
    /**
     * Getter 
     * @return Angrisspunkte 
     */
    public Label getgAtk() {
        return gAtk;
    }

    /**
     * Getter
     * @return Kampfbutton 
     */
    public Button getFight() {
        return fight;
    }

    /**
     * Positionierung der Elemente im Grid.
     */
 @Override
    protected final void positionAdditionalElements() {
        this.add(this.shields.getgShield(), 0, 3);
        this.add(this.evolutionShields.getgShield(), 1, 3);
        this.add(gAtk, 0,2,2,1);        
        this.add(fight, 1, 4);        
        
    }

    
    /**
     * Anpassung der Groesse und Ausrichtung.
     */
    @Override
    protected final void setAdditionalSizesAndPosition() {
        //Padding
        Insets i = new Insets(2, 2, 2, 2);
       this.shields.getgShield().setPadding(i);
        this.evolutionShields.getgShield().setPadding(i);
        this.gAtk.setPadding(i);
        this.fight.setPadding(i);

        //   this.card_black_shield.
//        this.card_black_shield.setAlignment(Pos.CENTER);
//        
//        this.card_white_shield.setAlignment(Pos.CENTER);
//        this.cardname.setAlignment(Pos.CENTER);
//        this.imageholder.setAlignment(Pos.CENTER);
//        this.cardname.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
//        this.imageholder.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
//        this.card_white_shield.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
//        this.card_black_shield.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );
    }


}
