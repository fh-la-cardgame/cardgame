package cardgame.logic;

import java.util.*;

import cardgame.classes.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Game {

    /**
     * Playground von Spieler 1.
     */
    private final Playground side1;

    /**
     * Playground von Spieler 2.
     */
    private final Playground side2;
    
    private final int side1PlayerId;
    
    private final int side2PlayerId;

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
    
    /** Phasen des 1. Spielers **/
    private IntegerProperty player1Phase;
    
    /** Phasen des 2. Spielers **/
    private IntegerProperty player2Phase;

    /**
     * Spieler der am Zug ist.
     */
    private int playersTurn;

    /**
     * True wenn der Spieler der dran ist eine Monstercard gespielt hat.
     */
    private boolean playedMonstercard;
    
    private boolean gameEnd = false;
    
    private int playerWon = -1;

    /**
     * Alle Karten mit denen in diesem Zug schon angegriffen wurde.
     * Muss am Ende des Spielzugs geleert werden.
     */
    private Set<GameCard> CardsHaveAttack = new IdentityHashSet<>();
    
    public Game(Player player1, Player player2, Deck deck1, Deck deck2){
    	this(player1, player2, deck1, deck2, false);
    }
    
    /**
     * Konstruktor noch nicht fertig.
     */
    public Game(Player player1, Player player2, Deck deck1, Deck deck2, boolean isTest) {
        side1 = new Playground(player1, deck1, isTest);
        side2 = new Playground(player2, deck2, isTest);
        side1PlayerId = player1.getId();
        side2PlayerId = player2.getId();
        playersTurn = player1.getId();
        player1Phase = new SimpleIntegerProperty(0);
        player2Phase = new SimpleIntegerProperty(2);
        //Unsauber!:
        round = -1;
        
    }

    /**
     * Fuehrt eine Angriff aus.
     *
     * @param id           Eigene Spieler Id.
     * @param myCardRow    Zeilennummer der Karte mit der Angegriffen werden soll.
     * @param enemyCardRow Zeilennummer der Karte die angegriffen werden soll (-1 Angriff auf Spieler).
     */
    public void attack(int id, int myCardRow, int enemyCardRow) throws LogicException{
        GameCard myCard = getMyField(id).getBattlegroundMonster()[myCardRow];
        if (myCard == null) {
            throw new LogicException("Bei Zeilennummer keine Karte");
        }
        if (enemyCardRow == -1) {
            attack(id, myCard, null);
        } else {
            GameCard enemyCard = getEnemyField(id).getBattlegroundMonster()[enemyCardRow];
            if (enemyCard == null) {
                throw new LogicException("Bei Zeilennummer keine Karte");
            }
            attack(id, myCard, enemyCard);
        }

    }

    /**
     * Fuehrt eine Angriff aus.
     *
     * @param id        Eigene Spieler Id.
     * @param myCard    Karte mit der Angegriffen werden soll.
     * @param enemyCard Karte die angegriffen werden soll (null Angriff auf Spieler).
     */
    // Noch keine Implementierung mit Effekten
    public void attack (int id, GameCard myCard, GameCard enemyCard) throws LogicException {
        turn(id);

        //Phase wird in Angriff geaendert
        if (phase == 0) {
            phase = 1;
        }
        //Illegale Phasen
        if (phase >= 2 || phase < 0) {
            throw new LogicException("Phase existiert nicht");
        }

        //Erster Zug kein Angriff moeglich
        if (round <= 0) {
            throw new LogicException("Erste Runde Kein Angriff moeglich");
        }

        //Abfrage ob mit Karte schon angegriffen wurde
        if(CardsHaveAttack.contains(myCard)) throw new LogicException("Mit dieser Karte wurde schon angegriffen !");
        /*for (GameCard g : CardsHaveAttack) {
            if (myCard == g) {
                throw new LogicException("Mit dieser Karte wurde schon angegriffen");
            }
        }*/
        GameCard[] enemyBattleground = getEnemyField(id).getBattlegroundMonster();
        GameCard[] myBattleground = getMyField(id).getBattlegroundMonster();


        //Abfrage, ob Karte auf dem Feld
        gameCardInField(myCard, myBattleground);


        //Angriff direkt auf den Gegner
        if (enemyCard == null) {
            //Pruefen ob Gegner noch Karten auf Spielfeld
            if(getEnemyField(id).getCountBattlegroundMonster() != 0)throw new LogicException("Kein Angriff direkt auf den Spieler moeglich");


            //Gegner verliert ein Schield
            Shield shield = getEnemyField(id).getPlayer().getShields();
            shield.dropShield();

            //Ueberpruefen ob gewonnen ???
            if (shield.getCurrentShields() == 0) {
//                System.out.println("Spieler hat gewonnen");
              //TODO
                gameEnd = true;
                if(id == side1PlayerId) {
                	playerWon = side2PlayerId;
                } else {
                	playerWon = side1PlayerId;
                }
            }

            //Eigene Evolutionschilder erhoehen
            addEvoSchieldAndEffect(id, myCard, null);
            //TODO destroy Effect kann auf keine Karte wirken???? wird nicht ausgefuehrt???

            CardsHaveAttack.add(myCard);
            return;
        }
        //Angriff auf eine Karte
        //Abfrage, ob Karte auf dem Feld
        gameCardInField(enemyCard, enemyBattleground);

        if (myCard.getAtk() > enemyCard.getAtk()) {

            //Gegner Schild entfernen
        	dropShieldAndEffect(id, enemyCard, myCard);

            //Eigene Evolutionschilder erhoehen
            addEvoSchieldAndEffect(id, myCard, enemyCard);

        } else if (myCard.getAtk() == enemyCard.getAtk()) {

            //Gegner Schield entfernen
        	dropShieldAndEffect(id, enemyCard, myCard);

            //Eigene Schield entfernen
        	dropShieldAndEffect(id, myCard, enemyCard);

            //Eigene Evolutionschilder erhoehen
        	addEvoSchieldAndEffect(id, myCard, enemyCard);
        	
            //Gegnerische Evolutionschilder erhoehen
            addEvoSchieldAndEffect(id ,enemyCard, myCard);
            

        } else if (myCard.getAtk() < enemyCard.getAtk()) {

            //Eigene Schield entfernen
        	dropShieldAndEffect(id, myCard, enemyCard);

        }

        CardsHaveAttack.add(myCard);

    }

    /**
     * Eine Karte aus der Hand wird aufs Feld gelegt.
     *
     * @param id   SpielerId.
     * @param card Karte die aufs Feld gelegt werden soll.
     */
    public void playCard(int id, Card card) throws LogicException {
        if (card instanceof GameCard) {
            turn(id);
            if (phase != 0) throw new LogicException("Kann nur am Anfang Karten legen !");
            if (playedMonstercard) throw new LogicException("Es darf nur 1 mal pro Zug eine Monsterkarte gelegt werden !");
            getMyField(id).addMonsterCard((GameCard) card);
            playedMonstercard = true;
        } else playSpecialCard(id, (SpecialCard) card, null);
    }


    public void playSpecialCard(int id, SpecialCard card, GameCard enemyCard) throws LogicException {
        turn(id);
        if (phase != 0) throw new LogicException("Kann nur am Anfang Karten legen !");
        //Effekt ausfuehren
        List<Effect> allEffects = card.getEffects();
        for (Effect e : allEffects) {
            List<GameCard> cardsEffect = getCardsForEffect(id,e,enemyCard,enemyCard);
            List<GameCard> cardsDeath =  EffectsAssignment.useEffect(e,cardsEffect);
            if(e.getEffectType() == EffectType.destroy){
                cardsDeath.forEach(this::removeGameCardFormField);
            }else{
                cardsEffect.forEach(c-> c.addSpecialCard(card));
                card.addGameCard(cardsEffect);
                getMyField(id).addSpecialCardToField(card);
            }
        }
    }
    
    /**
     * Gibt zu einer Effektkarte die Karten zurück auf denen dieser Effekt angewendet wird.
     * @param id Id des Spielers.
     * @param effect Effekt der angewendet werden soll.
     * @param effectTriggered Karte die den Effekt ausgeloest hat.
     * @param enemyCard Karte die angegriffen worden ist.
     * @return Liste der GameKarten. 
     */
    private List<GameCard> getCardsForEffect(int id,Effect effect,GameCard effectTriggered,GameCard enemyCard) throws LogicException{
        List<GameCard> allCards = new ArrayList<>();
        EffectType type = effect.getEffectType();
        if(effect.getEffectType() == EffectType.destroy){
            if(effect.getEffectNumber() > 0) allCards.add(Objects.requireNonNull(effectTriggered));
            else allCards.add(Objects.requireNonNull(enemyCard));
            return allCards;
        }
        String number  = type.toString().split("_")[1];
        switch(number){
            case "one":
                allCards.add(effectTriggered);
                break;
            case "all":
                GameCard[] enemyBattleground = getEnemyField(id).getBattlegroundMonster();
                GameCard[] myBattleground = getMyField(id).getBattlegroundMonster();
                int length = myBattleground.length;
                for(int i=0;i<length;i++){
                    if(myBattleground[i] != null) allCards.add(myBattleground[i]);
                    if(enemyBattleground[i] != null) allCards.add(enemyBattleground[i]);
                }
                break;
            case "deck":
                for(GameCard c:getMyField(id).getBattlegroundMonster())
                    if(c != null) allCards.add(c);
                break;
            case "deckenemy":
                for(GameCard c:getEnemyField(id).getBattlegroundMonster())
                    if(c != null) allCards.add(c);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return allCards;
    }



    private void removeGameCardFromSpecialCard(GameCard gameCard){
        Set<SpecialCard> specialCardGCards = gameCard.getSpecialCards();
        for(SpecialCard sCard:specialCardGCards){
            sCard.removeGameCard(gameCard);
            if(!sCard.hasGameCards()){
                removeSpecialCardFromField(sCard);
            }
        }
    }

    /**
     * Gibt die Karten auf der Hand des jeweiligen Spielers zurueck.
     *
     * @param id Id des Spielers
     * @return Die Karten des Spielers.
     */
    public List<Card> getCardsOnHand(int id) throws LogicException{
        return getMyField(id).getCardsOnHand();
    }

    /**
     * Gibt Playground vom Gegner zuruek.
     *
     * @param id Eigne Spieler Id.
     * @return Gegnerisches Spielfeld.
     */

    public Playground getEnemyField(int id) throws LogicException{
        if (side1PlayerId == id) {
            return side2;
        }
        if (side2PlayerId == id) {
            return side1;
        }
        throw new LogicException("PlayerId not exists");
    }

    /**
     * Gibt eigenen Playground zurueck.
     *
     * @param id Eigne Spieler Id.
     * @return Eigenes Spielfeld.
     */
    public Playground getMyField(int id) throws LogicException{
        if (side1PlayerId == id) {
            return side1;
        }
        if (side2PlayerId == id) {
            return side2;
        }
        throw new LogicException("PlayerId not exists");
    }

    /**
     * Prueft ob Sieler am Zug ist.
     *
     * @param PlayerId Spieler Id.
     */

    private void turn(int PlayerId) throws LogicException {
        if (playersTurn != PlayerId) {
            throw new LogicException("Spieler ist nicht am Zug");
        }
        if (gameEnd) {
        	throw new LogicException("Spiel zu Ende");
        }
    }

    /**
     * Prueft ob Karte auf Spielfeld vorhanden ist.
     *
     * @param gameCard Karte.
     * @param field    Spielfeld.
     */
    private void gameCardInField(GameCard gameCard, final GameCard[] field) throws LogicException {
        for (GameCard g : field) {
            if (gameCard == g) {
                return;
            }
        }
        throw new LogicException("Karte nicht auf dem Feld");
    }
    
    /**
     * Entfernt eine GameCard vom Spielfeld.
     * @param g GameCard zum entferen
     */
    private void removeGameCardFormField(GameCard g) {
    	removeGameCardFromSpecialCard(g);
    	side1.removeBattlegroundMonster(g);
    	side2.removeBattlegroundMonster(g);
    }

    /**
     * Entfernt SpecialCard vom Spielfeld.
     * @param special
     */
    private void removeSpecialCardFromField(SpecialCard special){
        if(!side1.removeBattlegroundSpecial(special));
            side2.removeBattlegroundSpecial(special);
    }
    
    /**
     * Entfernt ein Shield von der GameCard und falls diese auf 0 fallen wird diese entfernt.
     * Fuehrt dabei alle notwendigen Effecte aus.
     * @param id id des Spielers.
     * @param g GameCard bei der Shield entfernt wird.
     * @param otherForEffect GameCard fuer destroy Effect.
     */
    private void dropShieldAndEffect(int id, GameCard g, GameCard otherForEffect) throws LogicException{
    	Effect effect;
    	if (!g.dropShield()) {
    		effect = g.getNextEffect();
            removeGameCardFormField(g);
        } else {
        	effect = g.getNextEffect();
        }
    	if(effect != null) {
    		List<GameCard> list = getCardsForEffect(id, effect, g, otherForEffect);
    		EffectsAssignment.useEffect(effect, list).forEach( x->removeGameCardFormField(x) );
    	}
        
    }
    
//   /**
//    * Teilt die verschiedenen Effecttypen den GameCard g oder otherForEffect zu. 
//    * @param effect Effect der ausgefuehrt werden soll.
//    * @param g GameCard von der der Effect stammt.
//    * @param otherForEffect andere fuer destroy Effect.
//    */
//    private void useEffect(Effect effect, GameCard g, GameCard otherForEffect) {
//    	if (effect.getEffectType() == EffectType.destroy) {
//        	//destroy Effect wirkt auf die andere Karte
//    		//bei null kann Effect nicht ausgefuehrt werden und wird ignoriert
//        	if(otherForEffect != null) {
//        		List<GameCard> list = EffectsAssignment.useEffect(effect, otherForEffect);
//        		for(GameCard gamecard: list) {
//        			removeGameCardFormField(gamecard); 
//        		}
//        	}
//        } else {
//        	//alle anderen wirken auf die eigene Karte
//        	EffectsAssignment.useEffect(effect, g);
//        }
//    }
    
    /**
     * Erhoeht die EvoShields von der GameCard g und fuehrt falls notwendig den jeweiligen Effect aus.
     * @param g GameCard zum erhoehen der EvoShields
     * @param otherForEffect andere fuer destroy Effect
     */
    private void addEvoSchieldAndEffect(int id, GameCard g, GameCard otherForEffect) throws LogicException {
    	GameCard evolution = g.addEvoShield();
        Effect effect = g.getNextEffect();
        if (effect != null) {
        	//falls der Spieler angegriffen wurde wird destroy Effect ignoriert, da keine entsprechende Karte vorhanden ist
        	if(otherForEffect != null || effect.getEffectType() != EffectType.destroy) {
        		List<GameCard> list = getCardsForEffect(id, effect, g, otherForEffect);
        		EffectsAssignment.useEffect(effect, list).forEach( x->removeGameCardFormField(x) );
        	}
        }
        //Karte kann schon nicht mehr vorhanden sein !.
        if (evolution != null && g.isAlive()) {
            makeEvolution(g, evolution);
        }
    }

    /**
     * Fuehrt eine Evolution auf die GameCard old durch und ersetzt dabei diese durch GameCard evolution.
     * @param old alte GameCard
     * @param evolution GameCard zum ersetzen
     */
    private void makeEvolution(GameCard old, GameCard evolution) {
        /*GameCard[] arraySide1 = side1.getBattlegroundMonster();
        GameCard[] arraySide2 = side2.getBattlegroundMonster();
        for (int i = 0; i < arraySide1.length; i++) {
            if (arraySide1[i] == old) {
                //Reicht removeSpecialCardFromGameCard ?
                removeGameCardFromSpecialCard(old);
            	//removeGameCardFormField(old);
                arraySide1[i] = evolution;
                return;
            }
            if (arraySide2[i] == old) {

                removeGameCardFromSpecialCard(old);
            	//removeGameCardFormField(old);
                arraySide2[i] = evolution;
                return;
            }
        }*/
        if(side1.updateBattlegroundMonster(old,evolution) || side2.updateBattlegroundMonster(old,evolution)) removeGameCardFromSpecialCard(old);
        else throw new IllegalArgumentException("GameCard wurde im Playground nicht gefunden");

    }

    public int getPhase() {
        return phase;
    }

    public IntegerProperty getMyPhase(int id) {
    	if(side1PlayerId == id)  {
    		return player1Phase;
    	}
    	if(side2PlayerId == id) {
    		return player2Phase;
    	}
        throw new IllegalArgumentException("Id existiert nicht");
    }
    
    public IntegerProperty getEnemyPhase(int id) {
    	if(side1PlayerId == id)  {
    		return player2Phase;
    	}
    	if(side2PlayerId == id) {
    		return player1Phase;
    	}
        throw new IllegalArgumentException("Id existiert nicht");
    }

    public void setMyPhase(int id, int phase1) {
    	if(side1PlayerId == id)  {
    		this.player1Phase.setValue(phase1);
    	}else if(side2PlayerId == id) {
    		this.player2Phase.setValue(phase1);
    	}else throw new IllegalArgumentException("Id existiert nicht");
        
    }

    public boolean isGameRunning() {
    	return !gameEnd;
    }

    public void setGameEnd(boolean gameEnd) {
        this.gameEnd = gameEnd;
    }

    public int getPlayerWon() {
        return playerWon;
    }

    public void setPlayerWon(int playerWon) {
        this.playerWon = playerWon;
    }

    public int getRound() {
        return round;
    }

    public Set<GameCard> getCardsHaveAttack() {
        return CardsHaveAttack;
    }

    public void changePlayer(int id) {
        round++;
        phase = 0;
        playersTurn = id;
        playedMonstercard = false;
        CardsHaveAttack.clear();
    }
}
