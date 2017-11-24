package cardgame.classes;

import java.util.*;

/**
 * Klasse zur Abbildung der Struktur einer Spezialkarte.
 * @author BishaThan
 */
public class SpecialCard extends Card{
	
        /** Liste der Effekte der Spezialkarte. **/
	private List<Effect> effects;

	/**Liste die alle Monsterkarten speichert auf denen der Effekt angewendet wurde.*/
	private final ArrayList<GameCard> gameCards;

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
		this.gameCards = new ArrayList<>();
	}
	
	private SpecialCard(final int id, final String name, final String description, final Type type, final byte[] image, final List<Effect> effects, final ArrayList<GameCard> gameCards) {
		super(id, name, description, type, image);
		this.effects = new LinkedList<>(effects);
		this.gameCards = (ArrayList)gameCards.clone();
	}
        
        /**
         * Copy-Konstruktor
         * @param s Spezialkarte
         */
        public SpecialCard(final SpecialCard s){
            this(s.getId(), s.getName(), s.getDescription(), s.getType(), s.getImage(), s.getEffects(), s.getGameCard());
        }
        
        public ArrayList<GameCard> getGameCard(){
        	return gameCards;
        }

	/**
	 * @return the effects
	 */
	public List<Effect> getEffects() {
		return effects; // ?? Frage offen: ob Kopie noetig
	}

	public void addGameCard(GameCard... cards){
		for(GameCard card:cards){
			boolean has = false;
			if(card != null) {
				for (GameCard c : gameCards) {
					if (card == c) {
						has = true;
						break;
					}
				}
				if (!has) gameCards.add(card);
			}
		}
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
	
	@Override
	public String toString(){
		return super.toString()+" "+effects.toString()+" \n";
	}

	

}
