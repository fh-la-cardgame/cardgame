package cardgame.classes;


import java.util.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


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
    /**
     * Angriffspunkte, GUI ATK
     **/
    private IntegerProperty pAtk;
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
        this.pAtk = new SimpleIntegerProperty(atk);
        this.atk = atk;
        this.evolutionShields = new Shield(evolutionShields);
        this.shields = new Shield(shields);
        this.evolution = evolution;
        this.effects = effects.clone();
        this.evoEffects = evoEffects;
        this.specialCards = new IdentityHashSet<>();
    }

    private GameCard(final int id, final String name, final String description, final Type type, final byte[] image, final int atk, final Shield evolutionShields, final Shield shields, final GameCard evolution, final Effect[] effects, final Effect[] evoEffects, final Set<SpecialCard> specialcard) {
        super(id, name, description, type, image);
        this.pAtk = new SimpleIntegerProperty(atk);
        this.atk = atk;
        this.evolutionShields = new Shield(evolutionShields);
        this.shields = new Shield(shields);
        this.evolution = evolution;
        this.effects = effects.clone();
        this.evoEffects = evoEffects;
        this.specialCards = new IdentityHashSet<>(specialcard);
    }

    /**
     * Copy-Konstruktor
     *
     * @param c Spielkarte
     */
    public GameCard(final GameCard c) {
        this(c.getId(), c.getName(), c.getDescription(), c.getType(), c.getImage(), c.getAtk(), c.getEvolutionShields(), c.getShields(), c.getEvolution(), c.getEffects(), c.getEvoEffects());
    }

    /**
     * Ändert die Atk Punkte um den Wert add.
     *
     * @param add Die Atk punkte die hinzugefügt oder abgezogen werden sollen.
     */
    public void changeAtk(int add) {
        this.atk += add;
        if (atk < 0) atk = 0;
        setpAtk(atk);
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

    public IntegerProperty getpAtk() {
        return pAtk;
    }

    public void setpAtk(int atk) {
        this.pAtk.setValue(atk);
    }

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

}
