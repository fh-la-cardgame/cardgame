package cardgame.classes;

import java.util.*;

/**
 * Klasse zur Abbildung der Struktur einer Spezialkarte.
 *
 * @author BishaThan
 */
public class SpecialCard extends Card {

    /**
     * Liste der Effekte der Spezialkarte. *
     */
    private List<Effect> effects;


    /**
     * Speichert die Anzahl der Monsterkarten auf die diese SpecialCard wirkt.
     */
    private int countGameCards;

    /**
     * Konstruktor
     *
     * @param id          Indentifikationsnummer
     * @param name        Name der Spezialkarte
     * @param description BeschriebungGame
     * @param type        Typ
     * @param effects     Effekte der Spezialkarte
     */
    public SpecialCard(final int id, final String name, final String description, final Type type, final byte[] image, final List<Effect> effects) {
        super(id, name, description, type, image);
        this.effects = new LinkedList<>(effects);
        this.countGameCards = 0;
    }

    /**
     * Copy-Konstruktor
     *
     * @param s Spezialkarte
     */
    public SpecialCard(final SpecialCard s) {
        this(s.getCid(), s.getName(), s.getDescription(), s.getType(), s.getImage(), s.getEffects());
        countGameCards = s.countGameCards;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void removeGameCard() {
        countGameCards--;
    }

    public void addGameCard() {
        countGameCards++;
    }

    public boolean hasGameCards() {
        return countGameCards > 0;
    }

    public boolean needGameCard() {
        for (Effect e : effects) {
            if (e.getEffectType().needsGameCard()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " " + effects.toString() + " \n";
    }

}
