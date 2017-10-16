package cardgame.classes;
/**
 * Bildet die Sturktur zur Abbildung des gesammten Spielfeldes.
 * @author BishaThan
 */
public class Battlefield {
         
        /** Feste Zeilendefinierung der Matrizen.**/
	private final static int ROW = 2;
    
        /** Spielseite des 1. Spielers. **/
	private final Playground side1;
        /** Spielseite des 2. Spielers. **/
	private final Playground side2;
        /** Matrix fuer  Monsterkarten auf beiden Seiten.**/
	private final GameCard[][] battlegroundMonster;        
        /** Matrix fuer die Zauber- bzw. Fallenkarten auf beiden Seiten.**/
	private final SpecialCard[][] battlegroundSpecials;   
	
    /**
     * Konstruktor
     * @param side1 Spielseite des 1. Spielers
     * @param side2 Spielseite des 2. Spielers
     */
    public Battlefield(final Playground side1, final Playground side2) {
		this.side1 = side1;
		this.side2 = side2;
		this.battlegroundMonster = new GameCard[ROW][ROW * 2];
		this.battlegroundSpecials = new SpecialCard[ROW][ROW * 2];
	}

    /**
     * Entfernen einer Karte auf dem Spielfeld.
     * @param row Zeile
     * @param col Spalte
     */
    public void removeCard(final int row, final int col){
           //Logik
    }
		
    /**
     * Hinzufuegen einer Karte auf dem Spielfeld.
     * @param row Zeile
     * @param col Spalte 
    */
    public void addCard(final int row, final int col){
            //Logik
    }

}
