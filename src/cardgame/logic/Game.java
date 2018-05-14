package cardgame.logic;

import java.util.*;
import java.util.stream.Collectors;

import cardgame.classes.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import sun.rmi.runtime.Log;

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

    /**
     * Phasen des 1. Spielers
     **/
    private IntegerProperty player1Phase;

    /**
     * Phasen des 2. Spielers
     **/
    private IntegerProperty player2Phase;

    private IntegerProperty playerWonProperity;

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
     * Speichert die Links zwischen Karte und Specialcard als Indexe
     * Index groeser als Row (aus Playground) ist side2
     */
    private final Map<Integer, Set<Integer>> cardLinks;
    /**
     * Alle Indizes von Karten mit denen in diesem Zug schon angegriffen wurde.
     * Muss am Ende des Spielzugs geleert werden.
     */
    private boolean[] cardsHaveAttack;

    public Game(Player player1, Player player2, Deck deck1, Deck deck2) {
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
        cardLinks = new HashMap<>();
        cardsHaveAttack = new boolean[Playground.getRow()];
        //Unsauber!:
        round = -1;
    }

    public Game(Game g) {
        side1 = new Playground(g.side1);
        side2 = new Playground(g.side2);
        side1PlayerId = g.side1PlayerId;
        side2PlayerId = g.side2PlayerId;
        playersTurn = g.playersTurn;
        playedMonstercard = g.playedMonstercard;
        gameEnd = g.gameEnd;
        playerWon = g.playerWon;
        phase = g.phase;
        round = g.round;
        cardLinks = g.cardLinks.entrySet()
                .stream()
                .collect(Collectors.toMap(k -> k.getKey(), v -> new HashSet<Integer>(v.getValue())));
        cardsHaveAttack = g.cardsHaveAttack.clone();
    }

    /**
     * Fuehrt eine Angriff aus.
     *
     * @param id           Eigene Spieler Id.
     * @param myCardRow    Zeilennummer der Karte mit der Angegriffen werden soll.
     * @param enemyCardRow Zeilennummer der Karte die angegriffen werden soll (-1 Angriff auf Spieler).
     */
    public void attack(int id, int myCardRow, int enemyCardRow) throws LogicException {
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
    public void attack(int id, GameCard myCard, GameCard enemyCard) throws LogicException {
        turn(id);

        //Phase wird in Angriff geaendert
        if (phase == 0) {
            if(player1Phase != null){
                if(playersTurn == side1PlayerId){
                    player1Phase.set(1);
                }else{
                    player2Phase.set(1);
                }
            }
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

        GameCard[] enemyBattleground = getEnemyField(id).getBattlegroundMonster();
        GameCard[] myBattleground = getMyField(id).getBattlegroundMonster();

        //Abfrage, ob Karte auf dem Feld
        gameCardInField(myCard, myBattleground);
        //Abfrage ob mit Karte schon angegriffen wurde
        int cardIndex = getMyField(id).indexOfBattlegroundMonster(myCard);
        if (cardsHaveAttack[cardIndex]) throw new LogicException("Mit dieser Karte wurde schon angegriffen !");

        //Angriff direkt auf den Gegner
        if (enemyCard == null) {
            //Pruefen ob Gegner noch Karten auf Spielfeld
            if (getEnemyField(id).getCountBattlegroundMonster() != 0)
                throw new LogicException("Kein Angriff direkt auf den Spieler moeglich");


            //Gegner verliert ein Schield
            Shield shield = getEnemyField(id).getPlayer().getShields();
            shield.dropShield();

            //Ueberpruefen ob gewonnen ???
            if (shield.getCurrentShields() == 0) {
//                System.out.println("Spieler hat gewonnen");
                //TODO
                gameEnd = true;
                if (id == side1PlayerId) {
                    playerWon = side1PlayerId;
                } else {
                    playerWon = side2PlayerId;
                }
            }

            //Eigene Evolutionschilder erhoehen
            addEvoSchieldAndEffect(id, myCard, null);
            //TODO destroy Effect kann auf keine Karte wirken???? wird nicht ausgefuehrt???

            cardsHaveAttack[cardIndex] = true;
            return;
        }
        //Angriff auf eine Karte
        //Abfrage, ob Karte auf dem Feld
        gameCardInField(enemyCard, enemyBattleground);

        if (myCard.getAtk() > enemyCard.getAtk()) {

            //Gegner Schild entfernen
            dropShieldAndEffect(getEnemyId(id), enemyCard, myCard);

            //Eigene Evolutionschilder erhoehen
            addEvoSchieldAndEffect(id, myCard, enemyCard);

        } else if (myCard.getAtk() == enemyCard.getAtk()) {

            //Gegner Schield entfernen
            dropShieldAndEffect(getEnemyId(id), enemyCard, myCard);

            //Eigene Schield entfernen
            dropShieldAndEffect(id, myCard, enemyCard);

            //Eigene Evolutionschilder erhoehen
            addEvoSchieldAndEffect(id, myCard, enemyCard);

            //Gegnerische Evolutionschilder erhoehen
            addEvoSchieldAndEffect(getEnemyId(id), enemyCard, myCard);


        } else if (myCard.getAtk() < enemyCard.getAtk()) {

            //Eigene Schield entfernen
            dropShieldAndEffect(id, myCard, enemyCard);

            //Gegnerische Evolutionschilder erhoehen
            addEvoSchieldAndEffect(getEnemyId(id), enemyCard, myCard);

        }

        cardsHaveAttack[cardIndex] = true;
    }

    /**
     * Eine Karte aus der Hand wird aufs Feld gelegt.
     *
     * @param id   SpielerId.
     * @param card Karte die aufs Feld gelegt werden soll.
     */
    public void playCard(int id, Card card) throws LogicException {
       /* if (card instanceof GameCard) {
            turn(id);
            if (phase != 0) throw new LogicException("Kann nur am Anfang Karten legen !");
            if (playedMonstercard)
                throw new LogicException("Es darf nur 1 mal pro Zug eine Monsterkarte gelegt werden !");
            getMyField(id).addMonsterCard((GameCard) card);
            playedMonstercard = true;
        } else playSpecialCard(id, (SpecialCard) card, null);*/
        playCard(id,getMyField(id).indexOfCardsOnHand(card));
    }

    public void playCard(int id,int cardIndex)throws LogicException{
        Card card = getMyField(id).getCardsOnHand().get(cardIndex);
        if(card instanceof GameCard){
            turn(id);
            if(phase != 0) throw new LogicException("Kann nur am Anfang Karte legen !");
            if(playedMonstercard) throw new LogicException("Es darf nur 1 mal pro Zug eine Monsterkarte gelegt werden");
            getMyField(id).addMonsterCard(cardIndex);
            playedMonstercard = true;
        }else playSpecialCard(id,cardIndex,-1);
    }


    public void playSpecialCard(int id, SpecialCard card, GameCard enemyCard) throws LogicException {
        /*turn(id);
        if (phase != 0) throw new LogicException("Kann nur am Anfang Karten legen !");
        List<Effect> allEffects = card.getEffects();
        for (Effect e : allEffects) {
            List<GameCard> cardsEffect = getCardsForEffect(id, e, enemyCard, enemyCard);
            List<GameCard> cardsDeath = EffectsAssignment.useEffect(e, cardsEffect);
            if (e.getEffectType() == EffectType.destroy) {
                cardsDeath.forEach(this::removeGameCardFormField);
            } else {
                cardsEffect.forEach(c -> c.addSpecialCard(card));
                card.addGameCard(cardsEffect);
            }
        }
        if (card.hasGameCards()) getMyField(id).addSpecialCardToField(card);
        else {
            if (!getMyField(id).getCardsOnHand().contains(card))
                throw new IllegalArgumentException("Karte nicht auf der Hand");
            getMyField(id).removeCardFromHand(card);
        }*/
        //int indexSpecial = getMyField(id).getCardsOnHand().indexOf(card);
        int indexSpecial = getMyField(id).indexOfCardsOnHand(card);
        int indexEnemy = -1;
        if (enemyCard != null) {
            indexEnemy = getMyField(id).indexOfBattlegroundMonster(enemyCard);
            if (indexEnemy == -1) {
                indexEnemy = getEnemyField(id).indexOfBattlegroundMonster(enemyCard) + Playground.getRow();
            }
        }
        playSpecialCard(id, indexSpecial, indexEnemy);
    }

    public void playSpecialCard(int id, int specialCard, int enemyCard) throws LogicException {
        turn(id);
        if (phase != 0) throw new LogicException("Kann nur am Anfang Karten legen !");
        if (specialCard < 0) throw new IllegalArgumentException("Keine Special Karte zum Spielen angegeben !");
        SpecialCard card = (SpecialCard) getMyField(id).getCardsOnHand().get(specialCard);
        int index = getMyField(id).addSpecialCardToField(specialCard);
        for (Effect e : card.getEffects()) {
            int offset = idToIndex(id);
            if (offset != 0 && enemyCard >= 0) {
                if (enemyCard < Playground.getRow()) enemyCard += offset;
                else enemyCard -= offset;
            }
            GameCard[] cardIndex = getIndexOfCardsForEffect(id, e, enemyCard, enemyCard);
            List<GameCard> cardsDeath = EffectsAssignment.useEffect(e, Arrays.asList(cardIndex));
            if (e.getEffectType() == EffectType.destroy) {
                cardsDeath.forEach(this::removeGameCardFormField);
            } else {
                int off = offset + index;
                for (int i = 0; i < cardIndex.length; i++) {
                    if (cardIndex[i] != null) {
                        Set<Integer> get = cardLinks.get(i);
                        if (get != null) {
                            get.add(off);
                            card.addGameCard();
                        } else {
                            get = new HashSet<>();
                            get.add(off);
                            card.addGameCard();
                            cardLinks.put(i, get);
                        }
                    }
                }
            }
        }
        if (!card.hasGameCards()) getMyField(id).removeBattlegroundSpecial(index);
    }


    /**
     * Gibt zu einer Effektkarte die Karten zurück auf denen dieser Effekt angewendet wird.
     *
     * @param id              Id des Spielers von GameCard effectTriggered.
     * @param effect          Effekt der angewendet werden soll.
     * @param effectTriggered Karte die den Effekt ausgeloest hat.
     * @param enemyCard       Karte die angegriffen worden ist.
     * @return Liste der GameKarten.
     */
    private List<GameCard> getCardsForEffect(int id, Effect effect, GameCard effectTriggered, GameCard enemyCard) throws LogicException {
        List<GameCard> allCards = new ArrayList<>();
        EffectType type = effect.getEffectType();
        if (effect.getEffectType() == EffectType.destroy) {
            if (effect.getEffectNumber() > 0) allCards.add(Objects.requireNonNull(effectTriggered));
            else allCards.add(Objects.requireNonNull(enemyCard));
            return allCards;
        }
        String number = type.toString().split("_")[1];
        switch (number) {
            case "one":
                allCards.add(effectTriggered);
                break;
            case "all":
                GameCard[] enemyBattleground = getEnemyField(id).getBattlegroundMonster();
                GameCard[] myBattleground = getMyField(id).getBattlegroundMonster();
                int length = myBattleground.length;
                for (int i = 0; i < length; i++) {
                    if (myBattleground[i] != null) allCards.add(myBattleground[i]);
                    if (enemyBattleground[i] != null) allCards.add(enemyBattleground[i]);
                }
                break;
            case "deck":
                for (GameCard c : getMyField(id).getBattlegroundMonster())
                    if (c != null) allCards.add(c);
                break;
            case "deckenemy":
                for (GameCard c : getEnemyField(id).getBattlegroundMonster())
                    if (c != null) allCards.add(c);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return allCards;
    }

    /**
     * Gibt zu einer Effektkarte die Indexe der Karten zurück auf denen dieser Effekt angewendet wird.
     *
     * @param id              Id des Spielers von GameCard effectTriggered.
     * @param effect          Effekt der angewendet werden soll.
     * @param effectTriggered Karte die den Effekt ausgeloest hat.
     * @param enemyCard       Karte die angegriffen worden ist.
     * @return Liste der GameKarten.
     */
    private GameCard[] getIndexOfCardsForEffect(int id, Effect effect, int effectTriggered, int enemyCard) throws LogicException {
        GameCard[] allCards = new GameCard[10];
        EffectType type = effect.getEffectType();
        if (effect.getEffectType() == EffectType.destroy) {
            if (effectTriggered == -1) throw new IllegalArgumentException("Effekt benötigt zusätzlich Karte");
            if (effect.getEffectNumber() > 0)
                allCards[effectTriggered] = getCard(effectTriggered);
            else {
                if (enemyCard == -1)
                    throw new IllegalArgumentException("Es wird eine Karte benötigt auf die der Effekt ausgeführt wird !");
                allCards[enemyCard] = getCard(enemyCard);
            }
            return allCards;
        }
        String number = type.toString().split("_")[1];
        switch (number) {
            case "one":
                allCards[effectTriggered] = getCard(effectTriggered);
                break;
            case "all":
                GameCard[] battelgroundSide1 = side1.getBattlegroundMonster();
                GameCard[] battlegroundSide2 = side2.getBattlegroundMonster();
                int length = battelgroundSide1.length;
                for (int i = 0; i < length; i++) {
                    allCards[i] = battelgroundSide1[i];
                    allCards[i + length] = battlegroundSide2[i];
                }
                break;
            case "deck":
                int offset = idToIndex(id);
                GameCard[] battleground = getMyField(id).getBattlegroundMonster();
                for (int i = 0; i < battleground.length; i++) {
                    allCards[offset] = battleground[i];
                    offset++;
                }
                break;
            case "deckenemy":
                int off = idToIndex(id);
                GameCard[] battlegroundEnemy = getMyField(id).getBattlegroundMonster();
                for (int i = 0; i < battlegroundEnemy.length; i++) {
                    allCards[off] = battlegroundEnemy[i];
                    off++;
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        return allCards;
    }

    /**
     * Gibt die GameCard zu einem Index zurück
     *
     * @param index zwischen 0 und 2*Row -1
     * @return
     */
    private GameCard getCard(int index) {
        if (index < Playground.getRow()) {
            return side1.getBattlegroundMonster()[index];
        } else {
            return side2.getBattlegroundMonster()[index - Playground.getRow()];
        }
    }

    /**
     * Gibt den Offset an der dazu addiert werden muss wenn die Karte in side2 liegt
     *
     * @param id Id des Spielers
     * @return
     */
    private int idToIndex(int id) {
        if (id == side1PlayerId) {
            return 0;
        } else if (id == side2PlayerId) {
            return Playground.getRow();
        } else throw new IllegalArgumentException("Id nicht vorhanden");
    }

    private void removeGameCardFromSpecialCard(GameCard gameCard) {
        int index = side1.indexOfBattlegroundMonster(gameCard);
        if (index == -1) {
            index = side2.indexOfBattlegroundMonster(gameCard) + Playground.getRow();
        }
        removeGameCardLink(index);
    }

    /**
     * Entfernt einen Link von einer GameCard auf die Specialcard.
     * Wenn die Specialcard keinen Link mehr hat, also es keine GameCard
     * auf den Feld mehr gibt auf den diese Karte wirkt wird sie entfernt
     *
     * @param index Index der GameCard
     */
    private void removeGameCardLink(int index) {
        Set<Integer> set = cardLinks.get(index);
        if (set != null) {
            for (int sIndex : set) {
                if (sIndex < Playground.getRow()) {
                    SpecialCard s = side1.getBattlegroundSpecials()[sIndex];
                    s.removeGameCard();
                    if (!s.hasGameCards()) {
                        side1.removeBattlegroundSpecial(sIndex);
                    }
                } else {
                    sIndex = sIndex - Playground.getRow();
                    SpecialCard s = side2.getBattlegroundSpecials()[sIndex];
                    s.removeGameCard();
                    if (!s.hasGameCards()) {
                        side2.removeBattlegroundSpecial(sIndex);
                    }
                }
            }
            cardLinks.remove(index);
        }
    }

    /**
     * Gibt die Karten auf der Hand des jeweiligen Spielers zurueck.
     *
     * @param id Id des Spielers
     * @return Die Karten des Spielers.
     */
    public List<Card> getCardsOnHand(int id) {
        return getMyField(id).getCardsOnHand();
    }

    /**
     * Gibt Playground vom Gegner zuruek.
     *
     * @param id Eigne Spieler Id.
     * @return Gegnerisches Spielfeld.
     */

    public Playground getEnemyField(int id) {
        if (side1PlayerId == id) {
            return side2;
        }
        if (side2PlayerId == id) {
            return side1;
        }
        throw new IllegalArgumentException("Id existiert nicht");
    }

    /**
     * Gibt eigenen Playground zurueck.
     *
     * @param id Eigne Spieler Id.
     * @return Eigenes Spielfeld.
     */
    public Playground getMyField(int id){
        if (side1PlayerId == id) {
            return side1;
        }
        if (side2PlayerId == id) {
            return side2;
        }
        throw new IllegalArgumentException("Id existiert nicht");
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
            // Beim Distribution of Odds auskommentieren:
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
     *
     * @param g GameCard zum entferen
     */
    private void removeGameCardFormField(GameCard g) {
        removeGameCardFromSpecialCard(g);
        side1.removeBattlegroundMonster(g);
        side2.removeBattlegroundMonster(g);
    }

    /**
     * Entfernt ein Shield von der GameCard und falls diese auf 0 fallen wird diese entfernt.
     * Fuehrt dabei alle notwendigen Effecte aus.
     *
     * @param id             id des Spielers von GameCard g.
     * @param g              GameCard bei der Shield entfernt wird.
     * @param otherForEffect GameCard fuer destroy Effect.
     */
    private void dropShieldAndEffect(int id, GameCard g, GameCard otherForEffect) throws LogicException {
        Effect effect;
        if (g.dropShield()) {
            effect = g.getNextEffect();
        } else {
            effect = g.getNextEffect();
            removeGameCardFormField(g);
        }
        if (effect != null) {
            List<GameCard> list = getCardsForEffect(id, effect, g, otherForEffect);
            EffectsAssignment.useEffect(effect, list).forEach(x -> removeGameCardFormField(x));
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
     *
     * @param id             Id des Spielers von GameCard g.
     * @param g              GameCard zum erhoehen der EvoShields.
     * @param otherForEffect andere fuer destroy Effect.
     */
    private void addEvoSchieldAndEffect(int id, GameCard g, GameCard otherForEffect) throws LogicException {
        GameCard evolution = g.addEvoShield();
        Effect effect = g.getNextEffect();
        if (effect != null) {
            //falls der Spieler angegriffen wurde wird destroy Effect ignoriert, da keine entsprechende Karte vorhanden ist
            if (otherForEffect != null || effect.getEffectType() != EffectType.destroy) {
                List<GameCard> list = getCardsForEffect(id, effect, g, otherForEffect);
                EffectsAssignment.useEffect(effect, list).forEach(x -> removeGameCardFormField(x));
            }
        }
        //Karte kann schon nicht mehr vorhanden sein !.
        if (evolution != null && g.isAlive()) {
            makeEvolution(g, evolution);
        }
    }

    /**
     * Gibt die gegnerische Id zuruek.
     *
     * @param id eigene Id.
     * @throws IllegalArgumentException wenn Player id nicht existiert.
     */
    private int getEnemyId(int id) {
        if (id == side1PlayerId) {
            return side2PlayerId;
        }
        if (id == side2PlayerId) {
            return side1PlayerId;
        }
        throw new IllegalArgumentException("Player Id existiert nicht");
    }

    /**
     * Fuehrt eine Evolution auf die GameCard old durch und ersetzt dabei diese durch GameCard evolution.
     *
     * @param old       alte GameCard
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
        if (side1.updateBattlegroundMonster(old, evolution)) {
            int index = side1.indexOfBattlegroundMonster(evolution);
            if (playersTurn == side1PlayerId) cardsHaveAttack[index] = false;
            removeGameCardLink(index);

        } else if (side2.updateBattlegroundMonster(old, evolution)) {
            int index = side2.indexOfBattlegroundMonster(evolution);
            if (playersTurn == side2PlayerId) cardsHaveAttack[index] = false;
            removeGameCardLink(Playground.getRow() + index);
        } else throw new IllegalArgumentException("GameCard wurde im Playground nicht gefunden");

    }

    public int getPhase() {
        return phase;
    }

    public IntegerProperty getMyPhase(int id) {
        if (side1PlayerId == id) {
            return player1Phase;
        }
        if (side2PlayerId == id) {
            return player2Phase;
        }
        throw new IllegalArgumentException("Id existiert nicht");
    }

    public IntegerProperty getEnemyPhase(int id) {
        if (side1PlayerId == id) {
            return player2Phase;
        }
        if (side2PlayerId == id) {
            return player1Phase;
        }
        throw new IllegalArgumentException("Id existiert nicht");
    }

    public void setMyPhase(int id, int phase1) {
        if (side1PlayerId == id) {
            this.player1Phase.setValue(phase1);
        } else if (side2PlayerId == id) {
            this.player2Phase.setValue(phase1);
        } else throw new IllegalArgumentException("Id existiert nicht");

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
        if(playerWonProperity != null) playerWonProperity.set(playerWon);
        this.playerWon = playerWon;
    }

    public int getRound() {
        return round;
    }

    public IntegerProperty getPlayerWonProperity() {
        return playerWonProperity;
    }

    public boolean hasAttacked(int id, GameCard c) throws LogicException{
        int index = getMyField(id).indexOfBattlegroundMonster(c);
        return cardsHaveAttack[index];
    }

    public void changePlayer(int id) {
        round++;
        phase = 0;
        playersTurn = id;
        playedMonstercard = false;
        for (int i = 0; i < cardsHaveAttack.length; i++)
            cardsHaveAttack[i] = false;
        if(player1Phase != null){
            if(id == side1PlayerId){
                player1Phase.set(0);
                player2Phase.set(2);
            }else{
                player1Phase.set(2);
                player2Phase.set(0);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;

        if (round != game.round || phase != game.phase) return false;
        if (playersTurn != game.playersTurn) return false;
        if (playedMonstercard != game.playedMonstercard) return false;
        if (gameEnd != game.gameEnd || playerWon != game.playerWon) return false;
        if (!side1.equals(game.side1) || !side2.equals(game.side2)) return false;
        if (!cardLinks.equals(game.cardLinks)) return false;
        GameCard[] g1;
        GameCard[] g2;
        //try {
            g1 = getMyField(playersTurn).getBattlegroundMonster();
            g2 = game.getMyField(game.playersTurn).getBattlegroundMonster();
        //}catch (LogicException e){
        //    throw new IllegalArgumentException(e);
        //}
        List<GameCard> g1Cards = new ArrayList<>(5);
        List<GameCard> g2Cards = new ArrayList<>(5);
        for (int i = 0; i < g1.length; i++) {
            if (cardsHaveAttack[i] && g1[i] != null) g1Cards.add(g1[i]);
            if (game.cardsHaveAttack[i] && g2[i] != null) g2Cards.add(g2[i]);
        }
        g1Cards.sort(null);
        g2Cards.sort(null);
        return g1Cards.equals(g2Cards);
    }

    public void setPlayerPhases(){
        player1Phase = new SimpleIntegerProperty();
        player2Phase = new SimpleIntegerProperty();
    }

    public void setPlayerWonProperity(){
        playerWonProperity = new SimpleIntegerProperty(-1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(side1, side2);
    }
    
    public int getPlayersTurn(){
    	return playersTurn;
    }
}
