package cardgame.classes;

import sun.awt.util.IdentityArrayList;

import java.util.*;

/**
 * Klasse zur Abbildung der Struktur einer Spezialkarte.
 * @author BishaThan
 */
public class SpecialCard extends Card{
	
        /** Liste der Effekte der Spezialkarte. **/
	private List<Effect> effects;

	/**Liste die alle Monsterkarten speichert auf denen der Effekt angewendet wurde.*/
	private final List<GameCard> gameCards;

	/**
         * Konstruktor
         * @param id Indentifikationsnummer
         * @param name Name der Spezialkarte
         * @param description Beschriebung
         * @param type Typ
         * @param effects Effekte der Spezialkarte
         */
	public SpecialCard(final int id, final String name, final String description, final Type type, final byte[] image, final List<Effect> effects) {
		super(id, name, description, type, image);
		this.effects = new LinkedList<>(effects);
		this.gameCards = new IdentityArrayList<>();
	}
	
	private SpecialCard(final int id, final String name, final String description, final Type type, final byte[] image, final List<Effect> effects, final List<GameCard> gameCards) {
		super(id, name, description, type, image);
		this.effects = new LinkedList<>(effects);
		this.gameCards = new IdentityArrayList<>(gameCards);
	}
        
        /**
         * Copy-Konstruktor
         * @param s Spezialkarte
         */
        public SpecialCard(final SpecialCard s){
            this(s.getId(), s.getName(), s.getDescription(), s.getType(), s.getImage(), s.getEffects(), s.getGameCard());
        }
        
        public List<GameCard> getGameCard(){
        	return gameCards;
        }

	/**
	 * @return the effects
	 */
	public List<Effect> getEffects() {
		return effects; // ?? Frage offen: ob Kopie noetig
	}

	public void addGameCard(List<GameCard> cards){
		cards.stream()
				.filter(card -> !gameCards.contains(card))
				.forEach(gameCards::add);

	}

	public void removeGameCard(GameCard g){
		Iterator<GameCard> iterator = gameCards.iterator();
		while(iterator.hasNext()){
			if(iterator.next() == g) {
				iterator.remove();
				return;
			}
		}
	}

	public boolean hasGameCards(){
		return !gameCards.isEmpty();
	}

	public boolean needGameCard(){
		for (Effect e:effects) {
			if(e.getEffectType().needsGameCard()) return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return super.toString()+" "+effects.toString()+" \n";
	}

	

}
