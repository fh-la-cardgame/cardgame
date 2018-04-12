package cardgame.classes;

import java.util.*;
import javafx.scene.control.Label;

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
     * Liste die alle Monsterkarten speichert auf denen der Effekt angewendet wurde.
     */
    private Set<GameCard> gameCards;

    /**
     * Konstruktor
     *
     * @param id Indentifikationsnummer
     * @param name Name der Spezialkarte
     * @param description Beschriebung
     * @param type Typ
     * @param effects Effekte der Spezialkarte
     */
    public SpecialCard(final int id, final String name, final String description, final Type type, final byte[] image, final List<Effect> effects) {
        super(id, name, description, type, image);
        this.effects = new LinkedList<>(effects);
        this.gameCards = new IdentityHashSet<>();

        if (effects != null && effects.size() > 0) {
            getgDescription().add(new Label("ALLGEMEINE EFFEKTE:"));
            for (Effect e : effects) {
                if (e != null) {

                    getgDescription().add(new Label(e.getDescription()));
                }

            }

            getgDescription().add(new Label("***************************************"));
        }

    }

    private SpecialCard(final int id, final String name, final String description, final Type type, final byte[] image, final List<Effect> effects, final Set<GameCard> gameCards) {
        this(id, name, description, type, image, effects);
        this.gameCards = new IdentityHashSet<>(gameCards);
    }

    /**
     * Platzhalterklasse - Definiert eine leere Karte als Schablone
     */
    public SpecialCard(){
        super();        
        this.effects = new LinkedList<>();
        this.gameCards = new IdentityHashSet<>();
    }
    /**
     * Copy-Konstruktor
     *
     * @param s Spezialkarte
     */
    public SpecialCard(final SpecialCard s) {
        this(s.getCid(), s.getName(), s.getDescription(), s.getType(), s.getImage(), s.getEffects(), s.getGameCard());
    }

    public Set<GameCard> getGameCard() {
        return gameCards;
    }

    /**
     * @return the effects
     */
    public List<Effect> getEffects() {
        return effects; // ?? Frage offen: ob Kopie noetig
    }

    public void addGameCard(List<GameCard> cards) {
        gameCards.addAll(cards);
        /*cards.stream()
				.filter(card -> !gameCards.contains(card))
				.forEach(gameCards::add);*/

    }

    public void removeGameCard(GameCard g) {
        gameCards.remove(g);
        /*Iterator<GameCard> iterator = gameCards.iterator();
		while(iterator.hasNext()){
			if(iterator.next() == g) {
				iterator.remove();
				return;
			}
		}*/
    }

    public boolean hasGameCards() {
        return !gameCards.isEmpty();
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

    @Override
    protected void positionAdditionalElements() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setAdditionalSizesAndPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
    

}
