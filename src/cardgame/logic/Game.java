package cardgame.logic;

import java.util.*;

import cardgame.classes.*;

import static cardgame.classes.EffectType.destroy;

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
     * True wenn der Spieler der dran ist eine Monstercard gespielt hat.
     */
    private boolean playedMonstercard;

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
     * @param id           Eigene Spieler Id.
     * @param myCardRow    Zeilennummer der Karte mit der Angegriffen werden soll.
     * @param enemyCardRow Zeilennummer der Karte die angegriffen werden soll (-1 Angriff auf Spieler).
     */
    public void attack(int id, int myCardRow, int enemyCardRow) {
        GameCard myCard = getMyField(id).getBattlegroundMonster()[myCardRow];
        if (myCard == null) {
            throw new RuntimeException("Bei Zeilennummer keine Karte");
        }
        if (enemyCardRow == -1) {
            attack(id, myCard, null);
        } else {
            GameCard enemyCard = getEnemyField(id).getBattlegroundMonster()[enemyCardRow];
            if (enemyCard == null) {
                throw new RuntimeException("Bei Zeilennummer keine Karte");
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
    public void attack(int id, GameCard myCard, GameCard enemyCard) {
        turn(id);

        //Phase wird in Angriff geaendert
        if (phase == 0) {
            phase = 1;
        }
        //Illegale Phasen
        if (phase >= 2 || phase < 0) {
            throw new RuntimeException("Phase existiert nicht");
        }

        //Erster Zug kein Angriff moeglich
        if (round <= 0) {
            throw new RuntimeException("Erste Runde Kein Angriff möglich");
        }

        //Abfrage ob mit Karte schon angegriffen wurde
        for (GameCard g : CardsHaveAttack) {
            if (myCard == g) {
                throw new RuntimeException("Mit dieser Karte wurde schon angegriffen");
            }
        }


        GameCard[] enemyBattleground = getEnemyField(id).getBattlegroundMonster();
        GameCard[] myBattleground = getMyField(id).getBattlegroundMonster();


        //Abfrage, ob Karte auf dem Feld
        GameCardInField(myCard, myBattleground);


        //Angriff direkt auf den Gegner
        if (enemyCard == null) {
            //Prüfen ob Gegner noch Karten auf Spielfeld
            for (GameCard g : enemyBattleground) {
                if (g != null) {
                    throw new RuntimeException("Kein Angriff direkt auf den Spieler möglich");
                }
            }

            //Gegner verliert ein Schield
            Shield shield = getEnemyField(id).getPlayer().getShields();
            shield.dropShield();

            //Überpruefen ob gewonnen ???
            if (shield.getCurrentShields() == 0) {
                System.out.println("Spieler hat gewonnen");
            }

            myCard.getEvolutionShields().addShield();

            CardsHaveAttack.add(myCard);
            return;
        }


        //Angriff auf eine Karte

        //Abfrage, ob Karte auf dem Feld
        GameCardInField(enemyCard, enemyBattleground);

        if (myCard.getAtk() > enemyCard.getAtk()) {

            //Gegner Schild entfernen
            if (!enemyCard.dropShield()) {
                getEnemyField(id).removeBattlegroundMonster(enemyCard);
            } else {
                Effect effect = enemyCard.getNextEffect();
                if (effect != null) {
                    //Effect ausfuehren
                }
            }

            //Eigene Evolutionschilder erhoehen
            GameCard evolution = myCard.AddEvoShield();
            Effect effect = myCard.getNextEffect();
            if (effect != null) {
                //Effect ausfuehren
            }
            if (evolution != null) {
                MakeEvolution(myCard, evolution);
            }


        } else if (myCard.getAtk() == enemyCard.getAtk()) {

            //Gegner Schield entfernen
            if (!enemyCard.dropShield()) {
                getEnemyField(id).removeBattlegroundMonster(enemyCard);
            } else {
                Effect effect = enemyCard.getNextEffect();
                if (effect != null) {
                    //Effect ausfuehren
                }
            }

            //Eigene Schield entfernen
            if (!myCard.dropShield()) {
                getEnemyField(id).removeBattlegroundMonster(myCard);
            } else {
                Effect effect = myCard.getNextEffect();
                if (effect != null) {
                    //Effect ausfuehren
                }
            }

            //Eigene Evolutionschilder erhoehen
            GameCard evolution = myCard.AddEvoShield();
            Effect effect = myCard.getNextEffect();
            if (effect != null) {
                //Effect ausfuehren
            }
            if (evolution != null) {
                MakeEvolution(myCard, evolution);
            }

            //Gegnerische Evolutionschilder erhoehen
            evolution = enemyCard.AddEvoShield();
            effect = enemyCard.getNextEffect();
            if (effect != null) {
                //Effect ausfuehren
            }
            if (evolution != null) {
                MakeEvolution(enemyCard, evolution);
            }


        } else if (myCard.getAtk() < enemyCard.getAtk()) {

            //Eigene Schield entfernen
            if (!myCard.dropShield()) {
                getEnemyField(id).removeBattlegroundMonster(myCard);
            } else {
                Effect effect = myCard.getNextEffect();
                if (effect != null) {
                    //Effect ausfuehren
                }
            }

        }

        CardsHaveAttack.add(myCard);

    }

    /**
     * Eine Karte aus der Hand wird aufs Feld gelegt.
     *
     * @param id   SpielerId.
     * @param card Karte die aufs Feld gelegt werden soll.
     */

    public void playCard(int id, Card card) {
        turn(id);
        if (phase != 0) throw new RuntimeException("Kann nur am Anfang Karten legen !");
        if (card instanceof GameCard) {
            if (playedMonstercard)
                throw new IllegalArgumentException("Es darf nur 1 mal pro Zug eine Monsterkarte gelegt werden !");
            getMyField(id).addMonsterCard((GameCard) card);
            playedMonstercard = true;
        } else playSpecialCard(id, (SpecialCard) card, null);
    }

    public void playSpecialCard(int id, SpecialCard card, GameCard enemyCard) {
        turn(id);
        if (phase != 0) throw new RuntimeException("Kann nur am Anfang Karten legen !");
        //Effekt ausführen
        List<Effect> allEffects = card.getEffects();
        for (Effect e : allEffects) {
            String[] c = e.getEffectType().toString().split("_");
            if (c[0].equals("destroy")) {
                Objects.requireNonNull(enemyCard);
                EffectsAssignment.useEffect(e, enemyCard);
            }
            else if (c[1].equals("one")) {
                Objects.requireNonNull(enemyCard);
                card.addGameCard(enemyCard);
                enemyCard.addSpecialCard(card);
                EffectsAssignment.useEffect(e,enemyCard);
                getMyField(id).addSpecialCard(card);
            } else
                switch (c[1]) {
                    case "all":
                        GameCard[] enemyBattleground = getEnemyField(id).getBattlegroundMonster();
                        GameCard[] myBattleground = getMyField(id).getBattlegroundMonster();
                        EffectsAssignment.useEffect(e, enemyBattleground);
                        EffectsAssignment.useEffect(e, myBattleground);
                        for(int i=0;i<myBattleground.length;i++){
                            if(myBattleground[i] != null) myBattleground[i].addSpecialCard(card);
                            if(enemyBattleground[i] != null) enemyBattleground[i].addSpecialCard(card);
                        }
                        card.addGameCard(enemyBattleground);
                        card.addGameCard(myBattleground);
                        break;
                    default:
                        throw new IllegalArgumentException("Nicht vorhanden !");
                }
            getMyField(id).addSpecialCard(card);

        }
    }

    private void removeGameCardFromSpecialCard(GameCard gameCard){
        List<SpecialCard> specialCardGCards = gameCard.getSpecialCards();
        for(SpecialCard sCard:specialCardGCards){
            sCard.removeGameCard(gameCard);
            if(!sCard.hasGameCards()){
                int length = side1.getBattlegroundSpecials().length;
                for(int i=0;i<length;i++){
                    if(side1.getBattlegroundSpecials()[i] == sCard){
                        side1.getBattlegroundMonster()[i] = null;
                        break;
                    }
                    if(side2.getBattlegroundSpecials()[i] == sCard){
                        side2.getBattlegroundMonster()[i] = null;
                        break;
                    }
                }
            }
        }
    }

    private boolean checkForReference(List<Card> cards, Card checkCard) {
        for (Card c : cards) {
            if (c == checkCard) return true;
        }
        return false;
    }


    /**
     * Gibt die Karten auf der Hand des jeweiligen Spielers zurück.
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
        if (side1.getPlayer().getId() == id) {
            return side2;
        }
        if (side2.getPlayer().getId() == id) {
            return side1;
        }
        throw new RuntimeException("PlayerId not exists");
    }

    /**
     * Gibt eigenes Playground zuruek.
     *
     * @param id Eigne Spieler Id.
     * @return Eigenes Spielfeld.
     */
    public Playground getMyField(int id) {
        if (side1.getPlayer().getId() == id) {
            return side1;
        }
        if (side2.getPlayer().getId() == id) {
            return side2;
        }
        throw new RuntimeException("PlayerId not exists");
    }

    /**
     * Prueft ob Sieler am Zug ist.
     *
     * @param PlayerId Spieler Id.
     */
    private void turn(int PlayerId) {
        if (playersTurn.getId() != PlayerId) {
            throw new RuntimeException("Spieler ist nicht am Zug");
        }
    }

    /**
     * Prueft ob Karte auf Spielfeld vorhanden ist.
     *
     * @param gameCard Karte.
     * @param field    Spielfeld.
     */
    private void GameCardInField(GameCard gameCard, final GameCard[] field) {
        for (GameCard g : field) {
            if (gameCard == g) {
                return;
            }
        }
        throw new RuntimeException("Karte nicht auf dem Feld");
    }

    private void MakeEvolution(GameCard old, GameCard evolution) {
        GameCard[] arraySide1 = side1.getBattlegroundMonster();
        GameCard[] arraySide2 = side2.getBattlegroundMonster();
        GameCard[] array = null;
        Playground playground = null;
        int i;
        for (i = 0; i < arraySide1.length; i++) {
            if (arraySide1[i] == old) {
                array = arraySide1;
                playground = side1;
                break;
            }
            if (arraySide2[i] == old) {
                array = arraySide2;
                playground = side2;
                break;
            }
        }
        playground.removeCard(old);
        array[i] = evolution;

    }

}
