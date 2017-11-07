package cardgame.logic;

import java.util.ArrayList;
import java.util.List;

import cardgame.classes.GameCard;
import cardgame.classes.Player;
import cardgame.classes.Playground;
import cardgame.classes.Shield;

public class Game {
	
	/**
	 * Playground von Spieler 1.
	 */
	private final Playground side1;
	
	/**
	 * Playground von Spieler 2.
	 */
	private final Playground side2;
	
	/**
	 * Runden des Spiels.
	 */
	private int round;
	
	/**
	 * Phase in der Spiels.
	 * Z.B. Angriffsphase.
	 */
	// Voschlag Verwendung eines Enums
	private int phase;
	
	/**
	 * Spieler der am Zug ist.
	 */
	private Player playersTurn;
	
	/**
	 * Alle Karten mit denen in diesem Zug schon angegriffen wurde.
	 * Muss am Ende des Spielzugs geleert werden.
	 */
	private List<GameCard> CardsHaveAttack = new ArrayList<>();
	
	/**
	 * Konstruktor noch nicht fertig.
	 */
	public Game() {
		side1 = new Playground(null, null);
		side2 = new Playground(null, null);
	}

	/**
	 * Fuehrt eine Angriff aus.
	 * 
	 * @param id Eigene Spieler Id.
	 * @param myCard Karte mit der Angegriffen werden soll.
	 * @param enemyCard Karte die angegriffen werden soll (null Angriff auf Spieler).
	 */
	// Noch keine Implementierung mit Effekten
	public void attack(int id, GameCard myCard, GameCard enemyCard) {
		turn(id);
		
		if(phase == 0) {
			phase = 1;
		}
		if(phase >= 2) {
			throw new RuntimeException("Angriffsphase schon beendet");
		}
		
		if(round <= 0) {
			throw new RuntimeException("Erste Runde Kein Angriff möglich");
		}
		
		
		
		if(CardsHaveAttack.contains(myCard)) {
			throw new RuntimeException("Mit dieser Karte wurde schon angegriffen");
		}
		
		
		GameCard[] enemyBattleground = getEnemyField(id).getBattlegroundMonster();
		GameCard[] myBattleground = getMyField(id).getBattlegroundMonster();
		
		GameCardInField(myCard, myBattleground);
		
		
		//Angriff direkt auf den Gegner
		if(enemyCard == null) {
			//Prüfen ob Gegner noch Karten auf Spielfeld
			for(GameCard g: enemyBattleground) {
				if(g != null) {
					throw new RuntimeException("Kein Angriff direkt auf den Spieler möglich");
				}
 			}
			
			//Gegner verliert ein Schield
			Shield shield = getEnemyField(id).getPlayer().getShields();
			shield.dropShield();
			
			//Überpruefen ob gewonnen ???
			if(shield.getCurrentShields() == 0) {
				System.out.println("Spieler hat gewonnen");
			}
			
			myCard.getEvolutionShields().addShield();
			
			CardsHaveAttack.add(myCard);
			return;
		}
		
		
		//Angriff auf eine Karte
		
		GameCardInField(enemyCard, enemyBattleground);
		
		CardsHaveAttack.add(myCard);
		
		
		if(myCard.getAtk() > enemyCard.getAtk()) {
			Shield shield = enemyCard.getShields();
			shield.dropShield();
			//Vorschlag neue Funktion dropShield in GameCard 
			//Effekte ausführen
			
			//Entferne Karte
			if(shield.getCurrentShields() == 0) {
				getEnemyField(id).removeBattlegroundMonster(enemyCard);
			}
			
			//Evolutionsschield hinzufügen
			myCard.getEvolutionShields().addShield();
			//Vorschlag neue Funktion addEvoShield in GameCard
			//Effekte
			
			return;
		}
		
		if(myCard.getAtk() == enemyCard.getAtk()) {
			//Gegner Schield entfernen
			Shield enemyShield = enemyCard.getShields();
			enemyShield.dropShield();
			
			//Entferne Karte
			if(enemyShield.getCurrentShields() == 0) {
				getEnemyField(id).removeBattlegroundMonster(enemyCard);
			}
			
			
			//Eigens Schield entfernen
			Shield myShield = myCard.getShields();
			myShield.dropShield();
			
			//Entferne Karte
			if(myShield.getCurrentShields() == 0) {
				getMyField(id).removeBattlegroundMonster(myCard);
			}
			
			//Evolutionsschield hinzufügen
			myCard.getEvolutionShields().addShield();
			enemyCard.getEvolutionShields().addShield();
			
			return;
		}
		
		if(myCard.getAtk() < enemyCard.getAtk()) {
			
			//Eigens Schield entfernen
			Shield myShield = myCard.getShields();
			myShield.dropShield();
			
			//Entferne Karte
			if(myShield.getCurrentShields() == 0) {
				getMyField(id).removeBattlegroundMonster(myCard);
			}
			
			return;
		}
		
	}
	
	/**
	 * Gibt Playground vom Gegner zuruek.
	 * @param id Eigne Spieler Id.
	 * @return Gegnerisches Spielfeld.
	 */
	public Playground getEnemyField(int id) {
		if(side1.getPlayer().getId() == id) {
			return side2;
		}
		if(side2.getPlayer().getId() == id) {
			return side1;
		}
		throw new RuntimeException("PlayerId not exists");
	}
	
	/**
	 * Gibt eigenes Playground zuruek.
	 * @param id Eigne Spieler Id.
	 * @return Eigenes Spielfeld.
	 */
	public Playground getMyField(int id) {
		if(side1.getPlayer().getId() == id) {
			return side1;
		}
		if(side2.getPlayer().getId() == id) {
			return side2;
		}
		throw new RuntimeException("PlayerId not exists");
	}
	
	/**
	 * Prueft ob Sieler am Zug ist.
	 * @param PlayerId Spieler Id.
	 */
	private void turn(int PlayerId) {
		if(playersTurn.getId() != PlayerId) {
			throw new RuntimeException("Spieler ist nicht am Zug");
		}
	}
	
	/**
	 * Prueft ob Karte auf Spielfeld vorhanden ist.
	 * @param gameCard Karte.
	 * @param field Spielfeld.
	 */
	private void GameCardInField(GameCard gameCard, final GameCard[] field) {
		for(GameCard g: field) {
			if(gameCard == g) {
				return;
			}
		}
		throw new RuntimeException("Karte nicht auf dem Feld");
	}
	
}
