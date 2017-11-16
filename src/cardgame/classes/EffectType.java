package cardgame.classes;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Aufzaehlungstyp fuer Effekte.
 * Jeder Enum bekommt im Konstruktor einen Boolean der angibt ob der Effekt Karten zerstören kann und
 * einen Lambda ausdruck der den Effekt dann auf einer Karte ausführt.
 * @author BishaThan
 */
public enum EffectType {

        /** Verminderungseffekt. Gilt fuer alle Karten**/
	subtraction_all(false,GameCard::changeAtk),
        /** Erhoehungseffekt. Gilt fuer alle Karten**/
  	addition_all(false,GameCard::changeAtk),
  		/** Verminderungseffekt. Gilt nur fuer eine Karte**/
  	substraction_one(false,GameCard::changeAtk),
  		/** Erhoehungseffekt. Gilt nur fuer eine Karte**/
  	addition_one(false,GameCard::changeAtk),
  		/** Zerstoerungseffekt. Gilt nur fuer eine Karte**/
  	destroy(true,(card,anz)->card.getShields().dropShield()),
  		/**Erhoehungseffekt. Gilt fuer das eigenes Deck**/
	addition_deck(false, GameCard::changeAtk),
		/**Verminderungseffekt. Gilt fur eigenes Deck**/
	substraction_deck(false, GameCard::changeAtk),
		/**Erhoehungseffekt. Gilt fur geg. Deck**/
	addition_deckenemy(false, GameCard::changeAtk),
		/**Verminderungseffekt. Gilt fur geg. Deck**/
	substraction_deckenemy(false, GameCard::changeAtk);

	private boolean changeShields;

	private BiConsumer<GameCard,Integer> function;

  	EffectType(boolean changeShields,BiConsumer<GameCard,Integer> function){
  		this.changeShields = changeShields;
  		this.function = function;
	}
	public boolean isChangeShields(){
  		return changeShields;
	}

	public BiConsumer<GameCard, Integer> getFunction() {
		return function;
	}
	/*
	/**
	 * Gibt den Lambda-Ausdruck anhand des Effekts zurück;
	 * @param effectType
	 * @param wert
	 * @return
	 */
	/*
  	public static Consumer<GameCard> getFunction(EffectType effectType,int wert){
  		switch(effectType){
			case substraction_one:
			case addition_all:
			case addition_one:
			case subtraction_all:
				return c -> c.changeAtk(wert);
			case destroy:
				return c -> c.getShields().dropShield();
		}
		throw new IllegalArgumentException("Funktion für Karte nicht gefunden !");
	}*/
        

}
