package cardgame.db;

import cardgame.classes.Card;
import cardgame.classes.Effect;
import cardgame.classes.EffectType;
import cardgame.classes.GameCard;
import cardgame.classes.Shield;
import cardgame.classes.SpecialCard;
import cardgame.classes.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Zugriff auf die DB-Tabelle der Karten. (Lesen, Schreiben, Aktualisieren)
 *
 * @author BishaThan, Dennis, David
 */
public class DbCard {

    /**
     * Die Connection zur DB *
     */
    private static Connection c;
    /**
     * Die maximale Anzahl an Effekten die eine Karte haben kann*
     */
    public static final int MAX_EFFEKTS = 5;
    /**
     * Die Obergrenze fuer die Anzahl an EvoEffekten pro Card*
     */
    public static final int EVO_OBERGRENZE = 10;

    /** Gibt eine Liste aller Karten zurueck
     * @return List<Card> Eine Liste aller Karten, ausgelesen aus der DB
     */
      public List<Card> getAllCards() {
        
        List<Card> cardList = new LinkedList<>();
        PreparedStatement prepStmt1;
        PreparedStatement prepStmt2;
        //PreparedStatement prepStmt3;
        ResultSet resStmt1;
        ResultSet resStmt2;
        ResultSet resStmt3;
        
        try {
            c = DbConnection.getPostgresConnection();
            
           /** Ausfuehren des Selects um alle notwendigen Infos aus Gamecard zu beziehen. **/
                        
            prepStmt1 = c.prepareStatement("Select g.gid, g.name, g.description,"
                                     + "g.monster_type, g.atk, g.shield_curr,"
                                     + "g.shield_max, g.evo_shield_curr"
                                     + ", g.evo_shield_max, g.evo from \"Gamecard\" g order by g.gid");
            
            resStmt1 = prepStmt1.executeQuery();
               
            while (resStmt1.next()) {       
                
                int gameCardID = resStmt1.getInt(1);
                String gameCardName = resStmt1.getString(2);
                String gameCardDescription = resStmt1.getString(3);
                Type monsterType = stringToType(resStmt1.getString(4));
                int attack = resStmt1.getInt(5);
                short lifeShieldCurrent = resStmt1.getShort(6);
                short lifeShieldMax = resStmt1.getShort(7);
                short evoShieldCurrent = resStmt1.getShort(8);
                short evoShieldMax = resStmt1.getShort(9);
                GameCard evo = null;
                
                if (resStmt1.getInt(10) != 0) {
                    evo = integerToGamecard(resStmt1.getInt(10));
                }

                /** Ausfuerung des Joins(ueber Card_Effekt) um die Effekte der LifeShields einer Karte auszulesen. **/
               //auslagern, dass ich ihn auch verwenden kann
                prepStmt2 = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield"+""
           		+ " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
           		+ "where g.gid = c_e.gid "
           		+ "and c_e.eid = e.eid "
           		+ "and c_e.shield is not null "
           		+ "and g.gid = "
           		+ "?");
           prepStmt2.setInt(1, gameCardID);
           resStmt2 = prepStmt2.executeQuery();
            /** Ausfuerung des Joins(ueber Card_Effekt) um die EvoShield- Effekte einer Karte auszulesen. **/
            //auslagern
           PreparedStatement selectCard_EvoEffect = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.evo_shield"+""
              		+ " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
              		+ "where g.gid = c_e.gid "
              		+ "and c_e.eid = e.eid "
              		+ "and c_e.evo_shield is not null "
              		+ "and g.gid = "
              		+ "?");
           selectCard_EvoEffect.setInt(1, gameCardID);
              resStmt3 = selectCard_EvoEffect.executeQuery();
             
               EffectType effectType;
                Effect[] effects = new Effect[lifeShieldMax];
                Effect[] evoEffects = new Effect[EVO_OBERGRENZE];
                
           effects = getEffects(lifeShieldMax, resStmt2);
           evoEffects = getEvoEffects(resStmt3);
                                           
               
                cardList.add(new GameCard(gameCardID, gameCardName, gameCardDescription, monsterType, attack,
                                   new Shield(lifeShieldCurrent, lifeShieldMax),
                                   new Shield(evoShieldCurrent, evoShieldMax), evo, effects, evoEffects));            
            }
            
            
             /** Ausfuehren des Selects um alle notwendigen Infos aus Specialcard zu beziehen. **/
             
            prepStmt1 = c.prepareStatement("select sc.sid, sc.name, sc.description, sc.type from "
                                            + "\"Specialcard\" sc order by sc.sid ");
                                       
            resStmt1 = prepStmt1.executeQuery();
            
            while (resStmt1.next()) {       
                
                int specialCardID = resStmt1.getInt(1);
                String specialCardName = resStmt1.getString(2);
                String specialCardDescription = resStmt1.getString(3);
                Type monsterType = stringToType(resStmt1.getString(4));                     
            
            /** Ausfuerung des Joins(ueber Specialcard_Effekt) um die Effekte einer Specialkarte auszulesen. **/
                              
                prepStmt2 = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number "
                                        + "from \"Effecte\" e, \"Specialcard_Effect\" ce, \"Specialcard\" s "
                                        + "where s.sid = " + specialCardID + "and e.eid = ce.eid "
                                        + "and ce.scid = s.sid");
                
                resStmt2 = prepStmt2.executeQuery();
                
                List<Effect> effects_list = new ArrayList<>();                
                EffectType effectType;
             
                while (resStmt2.next()) {
                    
                    int effectID = resStmt2.getInt(1);
                    String effectDescription = resStmt2.getString(2);
                    effectType = stringToEffectType(resStmt2.getString(3));
                    int effectNumber = resStmt2.getInt(4);
                    
                    effects_list.add(new Effect(effectID, effectDescription, effectType, effectNumber));
                }
                cardList.add(new SpecialCard(specialCardID, specialCardName, specialCardDescription, 
                             stringToType(resStmt1.getString(4)), effects_list));         
            }
            
            
            c.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
          } 
            finally {
                try {
                c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
                  }
            }
        
        return cardList;
    }

    
    /**
     * Ordnet einem String einen Enum zu.
     *
     * @param string Der String der als Enum zurueckgegeben werden soll.
     * @return Type Gibt den zum String gehoerenden Enum zurueck.
     * @throws new IllegalArgumentException Wenn der String keinem Enum
     * zugeordnet werden kann.
     */
    private Type stringToType(String string) {
        Type type = Type.valueOf(string.toLowerCase());
         if(type == null) throw new IllegalArgumentException("Kein existenter Type(Enum)");
        
        return type;
    }

    /**
     * Ordnet einem String einen Enum zu.
     *
     * @param string Der String der als Enum zurueckgegeben werden soll.
     * @return EffectType Gibt den zum String gehoerenden Enum zurueck .
     * @throws new IllegalArgumentException Wenn der String keinem Enum
     * zugeordnet werden kann.
     */
    private EffectType stringToEffectType(String string) {
        EffectType effect = EffectType.valueOf(string);
        if(effect == null) throw new IllegalArgumentException("Zu dem String gibt es kein Effecttype");
        return effect;
    }

    /**
     * Liefert ein Array mit Effekten zurueck. Dieses Array laesst sich dann
     * einer Gamecard zuordnen.
     *
     * @param shield_max die maximale Anzahl an Schildern.
     * @param resultEffect Resultset das Infos ueber die Effekte liefert.
     * @return ein Array mit Effekten.
     */
    private Effect[] getEffects(int shield_max, ResultSet resultEffect) {
        EffectType effect = null;
        Effect[] effects = null;
        try {					//shield_max -1
            effects = new Effect[shield_max - 1];
            while (resultEffect.next()) {	//e.eid, e.description, e.effect_type, e.effect_number, c_e.shield
                int eId = resultEffect.getInt(1);
                String eDescription = resultEffect.getString(2);
                String eEffectType = resultEffect.getString(3);
                int eEffect_number = resultEffect.getInt(4);
                short eShield = resultEffect.getShort(5);
                //Oberstes Schild hat keinen Effekte:
                if (shield_max == 1) {
                    throw new IllegalArgumentException("Zu wenig Schilder fuer Effekte");
                }

                effect = stringToEffectType(eEffectType);
                if (eShield != -1) {
                    effects[eShield] = new Effect(eId, eDescription, effect, eEffect_number);
                } else {
                    int i = 0;
                    while (i < shield_max - 1) {
                        effects[i] = new Effect(eId, eDescription, effect, eEffect_number);
                        i++;
                    }

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return effects;
    }

    /**
     * Liefert eine Array auf EvoEffekten zurueck. Dieses Array laesst sich dann
     * einer Gamecard zuordnen.
     *
     * @param resultEvoEffect Resultset mit ausgelesen Infos auf DB hinsichlich
     * der Evo_Effekte.
     * @return ein Array aus Effekten.
     */
    private Effect[] getEvoEffects(ResultSet resultEvoEffect) {
        EffectType effect = null;
        Effect[] effects = null;
        try {					//shield_max -1
            effects = new Effect[EVO_OBERGRENZE];
            while (resultEvoEffect.next()) {			//effectType  e.description, e.effect_type, e.effect_number, c_e.evo_shield
                int eId = resultEvoEffect.getInt(1);
                String eDescription = resultEvoEffect.getString(2);
                String eType = resultEvoEffect.getString(3);
                int eEffect_number = resultEvoEffect.getInt(4);
                short eEvo_Shield = resultEvoEffect.getShort(5);

                effect = stringToEffectType(eType);
                //evo_shield
                if (eEvo_Shield != -1) {
                    effects[eEvo_Shield - 1] = new Effect(eId, eDescription, effect, eEffect_number);
                } else {
                    int i = 0;
                    while (i < EVO_OBERGRENZE) {
                        effects[i] = new Effect(eId, eDescription, effect, eEffect_number);
                        i++;
                    }

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return effects;
    }

    /**
     * Weist der id der Gamecard(integer) eine Gamecard zu.
     *
     * @param id Id der Gamecard die Evolution.
     * @return Gamecard.
     */
    private GameCard integerToGamecard(int id) {
        GameCard result = null;
        ResultSet resultCard, resultEffect, resultEvoEffect;
        PreparedStatement selectGamecard;
        Effect[] effects = null;
        Effect[] evoEffects = null;

        try {
            c = DbConnection.getPostgresConnection();
            //Ausfuehren des Selects um alle notwendigen Infos aus Gamecard zu beziehen.
            selectGamecard = c.prepareStatement("Select g.\"gid\", g.\"name\", g.\"description\", g.\"monster_type\", g.\"atk\", g.\"shield_curr\"," + " "
                    + "g.\"shield_max\", g.\"evo_shield_curr\", g.\"evo_shield_max\" from public.\"Gamecard\" g where g.gid = ?");
            selectGamecard.setInt(1, id);
            resultCard = selectGamecard.executeQuery();

            resultCard.next();
            int gId = resultCard.getInt(1);
            String gName = resultCard.getString(2);
            String gDescription = resultCard.getString(3);
            String gType = resultCard.getString(4);
            int gAtk = resultCard.getInt(5);
            short gShield_curr = resultCard.getShort(6);
            short gShield_max = resultCard.getShort(7);
            short gEvo_Shield_curr = resultCard.getShort(8);
            short gEvo_Shield_max = resultCard.getShort(9);

            // Da das ResultSet nur Strings statt Enums liefert, werden die Strings mittels switch - Anweisung
            // in der Hilfsmethode StringToType dem zugehoerigem Enum zugeordnet.
            Type type = null;
            type = stringToType(gType);

            PreparedStatement selectCard_Effect = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield" + ""
                    + " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
                    + "where g.gid = c_e.gid "
                    + "and c_e.eid = e.eid "
                    + "and c_e.shield is not null "
                    + "and g.gid = "
                    + "?");
            selectCard_Effect.setInt(1, gId);
            resultEffect = selectCard_Effect.executeQuery();

            PreparedStatement selectCard_EvoEffect = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.evo_shield" + ""
                    + " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
                    + "where g.gid = c_e.gid "
                    + "and c_e.eid = e.eid "
                    + "and c_e.evo_shield is not null "
                    + "and g.gid = "
                    + "?");
            selectCard_EvoEffect.setInt(1, gId);
            resultEvoEffect = selectCard_EvoEffect.executeQuery();

            effects = getEffects(gShield_max, resultEffect);
            evoEffects = getEvoEffects(resultEvoEffect);

            result = new GameCard(gId, gName, gDescription, type, gAtk,
                    new Shield(gEvo_Shield_curr, gEvo_Shield_max), new Shield(gShield_curr, gShield_max),
                    null, effects.clone(), evoEffects.clone());

        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;

    }

    /**
     * Liest alle Karten und Specialcards in eine Liste ein.
     *
     * @param deckName Name des Decks
     * @return Gibt eine List aller Karten zurueck
     */
    public List<Card> getDeck(String deckName) {
        PreparedStatement selectGamecard, selectCard_Effect, selectCard_EvoEffect, selectSpecialcard, selectSpecialcardEffect;
        ResultSet resultCard, resultEffect, resultEvoEffect;
        Effect[] effects = null;
        Effect[] evoEffects = null;
        //deckName = deckName.toLowerCase();
        List<Card> deck = new ArrayList<>();
        try {
            c = DbConnection.getPostgresConnection();

            selectGamecard = c.prepareStatement("select g.gid, g.name, g.description, monster_type, atk, shield_curr, "
                    + "shield_max, evo_shield_curr, evo_shield_max, evo "
                    + "from \"Gamecard\" g , \"Deck_Cards\" dc, \"Deck\" d "
                    + "where g.gid = dc.gid "
                    + "and d.did = dc.did "
                    + "and LOWER(d.name) = LOWER(?)" 
                    + "and g.gid not in (select evo from \"Gamecard\" where evo is not null)");
            selectGamecard.setString(1, deckName);
            resultCard = selectGamecard.executeQuery();

            while (resultCard.next()) {
                int gId = resultCard.getInt(1);
                String gName = resultCard.getString(2);
                String gDescription = resultCard.getString(3);
                String gType = resultCard.getString(4);
                int gAtk = resultCard.getInt(5);
                short gShield_curr = resultCard.getShort(6);
                short gShield_max = resultCard.getShort(7);
                short gEvo_shield_curr = resultCard.getShort(8);
                short gEvo_shield_max = resultCard.getShort(9);
                int gEvo = resultCard.getInt(10);

                selectCard_Effect = c.prepareStatement("select e.eid, e.description, effect_type, effect_number, shield "
                        + "from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
                        + "where e.eid = c_e.eid "
                        + "and c_e.gid = g.gid "
                        + "and shield is not null "
                        + "and g.gid = ?");
                selectCard_Effect.setInt(1, gId);
                resultEffect = selectCard_Effect.executeQuery();

                selectCard_EvoEffect = c.prepareStatement("select e.eid, e.description, effect_type, effect_number, c_e.evo_shield "
                        + "from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
                        + "where e.eid = c_e.eid "
                        + "and c_e.gid = g.gid "
                        + "and evo_shield is not null "
                        + "and g.gid = ?");
                selectCard_EvoEffect.setInt(1, gId);
                resultEvoEffect = selectCard_EvoEffect.executeQuery();

                effects = getEffects(gShield_max, resultEffect);
                evoEffects = getEvoEffects(resultEvoEffect);

                GameCard evo = null;
                if (gEvo != 0) {
                    evo = integerToGamecard(gEvo);
                }

                deck.add(new GameCard(gId, gName, gDescription,
                        stringToType(gType), gAtk, new Shield(gEvo_shield_curr,
                        gEvo_shield_max), new Shield(gShield_curr, gShield_max), evo, effects.clone(), evoEffects.clone()));
            }

            //Ab hier kommen die Specialcards:
            selectSpecialcard = c.prepareStatement("select sc.sid, sc.name, description, type "
                    + "from \"Specialcard\" sc, \"Deck_Cards\" dc, \"Deck\" d "
                    + " where LOWER(d.name) = LOWER(?)"
                    + " and sc.sid = dc.sid"
                    + " and dc.did = d.did"
                    + " order by sc.sid");
            selectSpecialcard.setString(1, deckName);
            resultCard = selectSpecialcard.executeQuery();

            while (resultCard.next()) {
                int sId = resultCard.getInt(1);
                String sName = resultCard.getString(2);
                String sDescription = resultCard.getString(3);

                List<Effect> effects_list = new ArrayList<>();
                selectSpecialcardEffect = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number "
                        + "from \"Effecte\" e, \"Specialcard_Effect\" ce, \"Specialcard\" "
                        + "where sid = ?"
                        + "and e.eid = ce.eid "
                        + "and scid = sid");
                selectSpecialcardEffect.setInt(1, sId);
                resultEffect = selectSpecialcardEffect.executeQuery();
                while (resultEffect.next()) {
                    int eId = resultEffect.getInt(1);
                    String eDescription = resultEffect.getString(2);
                    String eEffectType = resultEffect.getString(3);
                    short eEffect_number = resultEffect.getShort(4);

                    effects_list.add(new Effect(eId, eDescription, stringToEffectType(eEffectType), eEffect_number));
                }
                deck.add(new SpecialCard(sId, sName, sDescription, stringToType(resultCard.getString(4)), effects_list));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return deck;
    }

    /**
     * Fuegt neue Beziehungen zwischen Effekte und Cards in die Tabelle ein. Um
     * Redundanz zu vermeiden, verhindert diese Funktion doppelte als auch
     * unlogische Eintraege.
     *
     * @param gid Die Id der GameCard.
     * @param eid Die Id des Effects.
     * @param evoShield Der EvoShield bei dem der Effekt ausgeloest wird.
     * @throws IllegalArgumentException Wenn die Anzahl der Schilder in einem
     * ungueltigen Wertebereich liegen.
     * @return boolean true, falls der Eintrag noch nicht vorhanden war, false,
     * falls der Eintrag schon existiert.
     */
    public boolean insert_Card_Effect_EvoShields(int gid, int eid, int evoShield) {
        PreparedStatement pst, pst2;
        int length_old = 0, length_new = 0;
        ResultSet result;
        try {
            c = DbConnection.getPostgresConnection();
            pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
            result = pst.executeQuery();
            result.next();
            length_old = result.getInt(1);
            if (evoShield > EVO_OBERGRENZE || evoShield == 0) {
                throw new IllegalArgumentException("Ungueltiger Wertebereich");
            }

            if (evoShield == -1) {
                pst2 = c.prepareStatement("insert into \"Card_Effect\" (gid, eid, shield, evo_shield) select "
                        + "?,?, NULL, -1 where not exists (select gid, eid, evo_shield from \"Card_Effect\" where gid = "
                        + "? and (evo_shield > -1 or evo_shield = -1))");
                pst2.setInt(1, gid);
                pst2.setInt(2, eid);
                pst2.setInt(3, gid);
            } else if (evoShield > -1) {
                pst2 = c.prepareStatement("insert into \"Card_Effect\" (gid, eid, shield, evo_shield) select "
                        + "?,?, NULL, ? where not exists (select gid, eid, evo_shield from \"Card_Effect\" where gid = "
                        + "? and (evo_shield = ? or evo_shield = -1))");
                pst2.setInt(1, gid);
                pst2.setInt(2, eid);
                pst2.setInt(3, evoShield);
                pst2.setInt(4, gid);
                pst2.setInt(5, evoShield);
            } else {
                throw new IllegalArgumentException("Wertebereich fuer EvoShield: [-1," + MAX_EFFEKTS + "]");
            }
            pst2.executeUpdate();
            pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
            result = pst.executeQuery();
            result.next();
            length_new = result.getInt(1);
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return length_old < length_new;
    }

    /**
     * Fuegt neue Beziehungen zwischen Effekte und Cards in die Tabelle ein. Um
     * Redundanz zu vermeiden verhindert diese Funktion doppelte Eintraege.
     *
     * @param gid Die Id der GameCard.
     * @param eid Die Id des Effects.
     * @param shield Der Shield bei dem der Effekt ausgeloest wird.
     * @throws IllegalArgumentException Wenn die Anzahl der Schilder nicht im
     * Wertebereich liegt.
     * @return boolean true, falls der Eintrag noch nicht vorhanden war, false,
     * falls der Eintrag schon existiert.
     */
    public boolean insert_Card_Effect_Shields(int gid, int eid, int shield) {

        PreparedStatement pst, pst2, pst3;
        int length_old = 0, length_new = 0;
        ResultSet result, result2;
        try {
            c = DbConnection.getPostgresConnection();
            pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
            result = pst.executeQuery();
            result.next();
            length_old = result.getInt(1);
            pst3 = c.prepareStatement("select shield_max from \"Gamecard\" "
                    + "where gid = ?");
            pst3.setInt(1, gid);
            result2 = pst3.executeQuery();
            result2.next();
            if (shield >= result2.getInt(1) - 1 || shield == -1 && result2.getInt(1) == 1) {
                throw new IllegalArgumentException("Die Karte hat nicht so viele Schilder");
            }

            if (shield == -1) {
                pst2 = c.prepareStatement("insert into \"Card_Effect\" (gid, eid, shield, evo_shield) select "
                        + "?,?, -1, NULL where not exists (select gid, eid, shield from \"Card_Effect\" where gid = "
                        + "? and (shield > -1 or shield = -1))");
                pst2.setInt(1, gid);
                pst2.setInt(2, eid);
                pst2.setInt(3, gid);
            } else if (shield > -1) {
                pst2 = c.prepareStatement("insert into \"Card_Effect\" (gid, eid, shield, evo_shield) select "
                        + "?, ?, ? ,NULL where not exists (select gid, eid, shield from \"Card_Effect\" where gid = "
                        + "? and (shield = ? or shield = -1))");
                pst2.setInt(1, gid);
                pst2.setInt(2, eid);
                pst2.setInt(3, shield);
                pst2.setInt(4, gid);
                pst2.setInt(5, shield);
            } else {
                throw new IllegalArgumentException("Ungueltiger Wertebereich");
            }
            pst2.executeUpdate();
            pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
            result = pst.executeQuery();
            result.next();
            length_new = result.getInt(1);
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return length_old < length_new;
    }

}
