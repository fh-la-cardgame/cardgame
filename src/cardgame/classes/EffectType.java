package cardgame.classes;

/**
 * Aufzaehlungstyp fuer Effekte.
 * @author BishaThan
 */
public enum EffectType {
        /** Verminderungseffekt. Gilt fuer alle Karten**/
	subtraction_all,
        /** Erhoehungseffekt. Gilt fuer alle Karten**/
  	addition_all,
  		/** Verminderungseffekt. Gilt nur fuer eine Karte**/
  	substraction_one,
  		/** Erhoehungseffekt. Gilt nur fuer eine Karte**/
  	addition_one,
  		/** Zerstoerungseffekt. Gilt nur fuer eine Karte**/
  	destroy;
        

}
