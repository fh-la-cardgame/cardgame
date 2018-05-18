package cardgame.db;

import cardgame.classes.Card;
import cardgame.classes.Effect;
import cardgame.classes.EffectType;
import cardgame.classes.GameCard;
import cardgame.classes.Shield;
import cardgame.classes.SpecialCard;
import cardgame.classes.Type;

import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                                     + ", g.evo_shield_max, g.evo, g.image from \"Gamecard\" g order by g.gid");
            
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
                byte[] imageGamecard = resStmt1.getBytes(11);
                
                if (resStmt1.getInt(10) != 0) {
                    evo = integerToGamecard(resStmt1.getInt(10));
                }

                /** Ausfuerung des Joins(ueber Card_Effekt) um die Effekte der LifeShields einer Karte auszulesen. **/
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
           evoEffects = getEvoEffects(evoShieldMax, resStmt3);
                                           
               
                cardList.add(new GameCard(gameCardID, gameCardName, gameCardDescription, monsterType, imageGamecard, attack,
                                   new Shield(lifeShieldCurrent, lifeShieldMax),
                                   new Shield(evoShieldCurrent, evoShieldMax), evo, effects, evoEffects));            
            }
            
            
             /** Ausfuehren des Selects um alle notwendigen Infos aus Specialcard zu beziehen. **/
             
            prepStmt1 = c.prepareStatement("select sc.sid, sc.name, sc.description, sc.type, sc.image from "
                                            + "\"Specialcard\" sc order by sc.sid ");
                                       
            resStmt1 = prepStmt1.executeQuery();
            
            while (resStmt1.next()) {       
                
                int specialCardID = resStmt1.getInt(1);
                String specialCardName = resStmt1.getString(2);
                String specialCardDescription = resStmt1.getString(3);
                Type monsterType = stringToType(resStmt1.getString(4));
                byte[] imageSpecialcard = resStmt1.getBytes(5);
            
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
                             stringToType(resStmt1.getString(4)), imageSpecialcard, effects_list));         
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
    
    /**Gibt eine GameCard zurueck.
     * 
     * @param name Name der Karte.
     * @return eine GameCard.
     */
    public GameCard getGameCard(String name){
    	GameCard result = null;
    	try{
    	 c = DbConnection.getPostgresConnection();
         
         /** Ausfuehren des Selects um alle notwendigen Infos aus Gamecard zu beziehen. **/
                      
          PreparedStatement prepStmt1 = c.prepareStatement("Select g.gid, g.name, g.description,"
                                   + "g.monster_type, g.atk, g.shield_curr,"
                                   + "g.shield_max, g.evo_shield_curr"
                                   + ", g.evo_shield_max, g.evo from \"Gamecard\" g "
                                   + "where LOWER(g.name) = LOWER(?)");
          prepStmt1.setString(1, name);
          ResultSet resStmt1 = prepStmt1.executeQuery();
          
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
              byte[] imageGamecard = null;
              
//              (final int id, final String name, final String description, final Type type, final byte[] image, final int atk,
//            	final Shield evolutionShields, final Shield shields, final GameCard evolution, 
//              final Effect[] effects, final Effect[] evoEffects) {

              /** Ausfuerung des Joins(ueber Card_Effekt) um die Effekte der LifeShields einer Karte auszulesen. **/
             PreparedStatement prepStmt2 = c.prepareStatement("select e.eid, e.description, e.effect_type, e.effect_number, c_e.shield"+""
         		+ " from \"Effecte\" e, \"Card_Effect\" c_e, \"Gamecard\" g "
         		+ "where g.gid = c_e.gid "
         		+ "and c_e.eid = e.eid "
         		+ "and c_e.shield is not null "
         		+ "and g.gid = "
         		+ "?");
         prepStmt2.setInt(1, gameCardID);
         ResultSet resStmt2 = prepStmt2.executeQuery();
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
            ResultSet resStmt3 = selectCard_EvoEffect.executeQuery();
           
             EffectType effectType;
              Effect[] effects = new Effect[lifeShieldMax];
              Effect[] evoEffects = new Effect[EVO_OBERGRENZE];
              
         effects = getEffects(lifeShieldMax, resStmt2);
         evoEffects = getEvoEffects(evoShieldMax,resStmt3);
             
              result = new GameCard(gameCardID, gameCardName, gameCardDescription, monsterType, imageGamecard, attack, 
            		  	new Shield(lifeShieldCurrent, lifeShieldMax), new Shield(evoShieldCurrent, evoShieldMax), evo,
            		  	effects, evoEffects);
          }  c.close();
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
        
        return Objects.requireNonNull(result);
          
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
    private Effect[] getEvoEffects(int shield_max, ResultSet resultEvoEffect) {
        EffectType effect = null;
        Effect[] effects = null;
        try {					//shield_max -1
            effects = new Effect[shield_max];
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
                    while (i < shield_max) {
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
                    + "g.\"shield_max\", g.\"evo_shield_curr\", g.\"evo_shield_max\", g.\"image\" from public.\"Gamecard\" g where g.gid = ?");
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
            byte[] imageGamecard = resultCard.getBytes(10);

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
            evoEffects = getEvoEffects(gEvo_Shield_max, resultEvoEffect);

            result = new GameCard(gId, gName, gDescription, type, imageGamecard, gAtk,
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
                    + "shield_max, evo_shield_curr, evo_shield_max, evo, g.image "
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
                byte[] imageGamecard = resultCard.getBytes(11);
                

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
                evoEffects = getEvoEffects(gEvo_shield_max, resultEvoEffect);

                GameCard evo = null;
                if (gEvo != 0) {
                    evo = integerToGamecard(gEvo);
                }

                deck.add(new GameCard(gId, gName, gDescription,
                        stringToType(gType), imageGamecard, gAtk, new Shield(gEvo_shield_curr,
                        gEvo_shield_max), new Shield(gShield_curr, gShield_max), evo, effects.clone(), evoEffects.clone()));
            }

            //Ab hier kommen die Specialcards:
            selectSpecialcard = c.prepareStatement("select sc.sid, sc.name, description, type, image "
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
                byte[] imageSpecialCard = resultCard.getBytes(5);

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
                deck.add(new SpecialCard(sId, sName, sDescription, stringToType(resultCard.getString(4)), imageSpecialCard, effects_list));
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
     * Liest alle Karten und Specialcards in eine Liste ein.
     *
     * @param deckName Name des Decks
     * @return Gibt eine List aller Karten zurueck
     */
    public List<Card> getDeckWithoutImage(String deckName) {
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
                evoEffects = getEvoEffects(gEvo_shield_max, resultEvoEffect);

                GameCard evo = null;
                if (gEvo != 0) {
                    evo = integerToGamecard(gEvo);
                }

                deck.add(new GameCard(gId, gName, gDescription,
                        stringToType(gType), null, gAtk, new Shield(gEvo_shield_curr,
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
                deck.add(new SpecialCard(sId, sName, sDescription, stringToType(resultCard.getString(4)), null, effects_list));
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
        PreparedStatement pst0, pst, pst2;
        int length_old = 0, length_new = 0;
        ResultSet result0, result;
        try {
            c = DbConnection.getPostgresConnection();
            pst = c.prepareStatement("select count(*) from \"Card_Effect\"");
            result = pst.executeQuery();
            result.next();
            length_old = result.getInt(1);
            pst0 = c.prepareStatement("select evo_shield_max from \"Gamecard\" where gid = ?");
            pst0.setInt(1, gid);
            result0 = pst0.executeQuery();
            result0.next();
            if (evoShield > EVO_OBERGRENZE || evoShield == 0 || evoShield > result0.getInt(1)) {
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
    /**Aktualisieren der Anzahl an Schildern.
     * Erlaubt eine konsistentes Aendern der Anzahl der Schilder, an denen auch die Effekte haengen.
     * Dabei gilt folgendes:
     * Auf dem 0-tem Schild(letztes verbleibendes Schild) GIBT ES EINEN Effekt. Auf dem max-Schild gibt es keinen Effekt.
     * @param gid Id der Gamecard.
     * @param max_shield Neue Anzahl an maximalen Schildern.
     */
    public void updateShields(int gid, int max_shield){
    	PreparedStatement pst, pst2, pst3, pst4;
    	ResultSet result, result2;
    	 try {
			c = DbConnection.getPostgresConnection();
			 pst = c.prepareStatement("select shield_max from \"Gamecard\" where gid = ?");
			 pst.setInt(1, gid);
	         result = pst.executeQuery();
	         result.next();
	         if(result.getInt(1) > max_shield){
	        	 pst2 = c.prepareStatement("select c_eid from \"Card_Effect\" "+
					        			 "where gid = ? "+
					        			 "and shield > ?;");
	        	 pst2.setInt(1, gid);
	        	 pst2.setInt(2, max_shield-2);
	        	 result2 = pst2.executeQuery();
	        	 while(result2.next()){
	        		 pst3 = c.prepareStatement("delete from \"Card_Effect\" "+
	        				 					"where c_eid = ?");
	        		 pst3.setInt(1, result2.getInt(1));
	        		 pst3.executeUpdate();
	        		 System.out.println(result2.getInt(1)+" wurde geloescht");
	        	 }
	         }
	         pst4 = c.prepareStatement("update \"Gamecard\" "+
		        	 "set shield_max = ?, shield_curr = ?"+ 
	        		 "where gid = ?");
		        	 pst4.setInt(1, max_shield);
		        	 pst4.setInt(2, max_shield);
		        	 pst4.setInt(3, gid);
		     pst4.executeUpdate();
		     System.out.println("Karte mit id: "+gid+" hat nun "+max_shield+" Schilder");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
        
    }
    /**Aktualisieren der Evolution-Schilder.
     * Erlaubt ein konsistentes Aendern der Anzahl an Evo-Schildern, an denen auch die Effekte haengen.
     * Dabei gilt folgendes:
     * Auf dem 0-ten Schild gibt es keinen Effekt. Auf dem letzten Schild gibt es einen Effekt.
     * Das 0-te Schild existiert gar nicht in der Liste, die Liste enthaelt nur die Schilde 1 - 10.
     * @param gid Id der Gamecard.
     * @param maxShield Neue Anzahl an maximalen Schilder.
     */
    public void updateEvoShield(int gid, int maxShield){
    	PreparedStatement pst, pst2, pst3, pst4;
    	ResultSet result, result2;
    	 try {
			c = DbConnection.getPostgresConnection();
			 pst = c.prepareStatement("select evo_shield_max from \"Gamecard\" where gid = ?");
			 pst.setInt(1, gid);
	         result = pst.executeQuery();
	         result.next();
	         if(result.getInt(1) > maxShield){
	        	 pst2 = c.prepareStatement("select c_eid from \"Card_Effect\" "+
	        			 "where gid = ? "+
	        			 "and evo_shield > ?;");
				 pst2.setInt(1, gid);
				 pst2.setInt(2, maxShield);
				 result2 = pst2.executeQuery();
				 while(result2.next()){
	        		 pst3 = c.prepareStatement("delete from \"Card_Effect\" "+
	        				 					"where c_eid = ?");
	        		 pst3.setInt(1, result2.getInt(1));
	        		 pst3.executeUpdate();
	        		 System.out.println(result2.getInt(1)+" wurde geloescht");
	        	 }
	         }
	         pst4 = c.prepareStatement("update \"Gamecard\" "+
		        	 "set evo_shield_max = ?, evo_shield_curr = ?"+ 
	        		 "where gid = ?");
		        	 pst4.setInt(1, maxShield);
		        	 pst4.setInt(2, maxShield);
		        	 pst4.setInt(3, gid);
		     pst4.executeUpdate();
		     System.out.println("Karte mit id: "+gid+" hat nun "+maxShield+" Evo-Schilder");
	    }catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
    }
    /**Prueft ob die Beziehung zwischen Evolution und Anzahl an Schildern stimmt.
     * Hilfsmethode die folgendes ueberprueft:
     * Wenn eine Karte weniger als 10 Schilder hat, muss sie eine Eovlution besitzten.
     */
    private void isEvoConsistent(){
    	PreparedStatement pst, pst2;
    	ResultSet result, result2;
    	boolean inconsistent = false;
    	 try {
			c = DbConnection.getPostgresConnection();
			pst = c.prepareStatement("select gid, evo, evo_shield_max from \"Gamecard\" "+
					"where evo_shield_max < 10 "+
					"and evo is null");
			result = pst.executeQuery();
			while(result.next()){
				inconsistent = true;
				System.out.println("Karte mit ID: "+result.getInt(1)+" hat keine Evolution obwohl es weniger als 10 Evo-Schilder besitzt");
			}
			if(inconsistent){
				throw new IllegalStateException("Inkonsistenzien in der Beziehung Evolution und Anzahl an Evo-Schildern");
			}else{
				System.out.println("Alles konsistent");
			}
    	 }catch (ClassNotFoundException | SQLException e) {
 			e.printStackTrace();
 		}
    }
    
//    public void add_change_Effects(int gid, int eid, Object shield, Object evoShield){
//    	PreparedStatement pst;
//    	ResultSet rs;
//    	
//    		try {
//    			c = DbConnection.getPostgresConnection();
//    			if(evoShield == null){
//					pst = c.prepareStatement("select c_eid from \"Card_Effect\" "+
//							"where gid = ? "+
//							"and (shield = ? "+
//							"or shield = -1)");
//					pst.setInt(1, gid);
//					pst.setInt(2, (int)shield);
//					rs = pst.executeQuery();
//					boolean found = false;
//					while(rs.next()){
//						if(found){
//							throw new IllegalStateException("Zu viele Ergebnisse im ResultSet");
//						}
//						found = true;
//						System.out.println("Update shield an "+rs.getInt(1));
//						updateEffect(rs.getInt(1), gid, eid, shield, evoShield);
//					}
//					if(!found){
//						System.out.println("Insert Effect bei shield");
//						System.out.println(insert_Card_Effect_Shields(gid, eid, (int)shield));
//					}
//					
//    			}
//    			else if(shield == null){
//    				pst = c.prepareStatement("select c_eid from \"Card_Effect\" "+
//    						"where gid = ? "+
//    						"and (evo_shield = ? "+
//    						"or evo_shield = -1)");
//    				pst.setInt(1, gid);
//					pst.setInt(2, (int)evoShield);
//					rs = pst.executeQuery();
//					boolean found = false;
//					while(rs.next()){
//						if(found){
//							throw new IllegalStateException("Zu viele Ergebnisse im ResultSet");
//						}
//						found = true;
//						System.out.println("Update Evoshield an "+rs.getInt(1));
//						updateEffect(rs.getInt(1), gid, eid, shield, evoShield);
//					}
//					if(!found){
//						System.out.println("Insert Effect bei Evoshield");
//						System.out.println(insert_Card_Effect_EvoShields(gid, eid, (int)evoShield));
//					}
//    			}
//    			
//    			
//    			
//			} catch (ClassNotFoundException | SQLException e) {
//				e.printStackTrace();
//			}
//    		return;
//    		
//    }
    
    /**Effekte einer GameCard aendern.
     * 
     * @param c_eid	
     * @param gid Id der GameCard.
     * @param eid ID des neuen Effekts.
     * @param shield Nr. des Shields.
     * @param evo_shield Nr. des EvoShields.
     * @return Anzahl der betroffenen Zeilen.
     */
    public int updateEffect(int c_eid, int gid, int eid, Object shield, Object evo_shield){

        /**holt die gewuenschte Karte. **/
        PreparedStatement prepStmtCard;
        /** holt die Max-Anzahl fuer Shield und EvoShield.  **/
        PreparedStatement prepStmtMax;
        /** holt die Anzahl der Shield-Effekte auf der Karte. **/
        PreparedStatement prepStmtCount;
        /** holt die Anzahl der Effekte auf dem Shield. **/
        PreparedStatement prepStmtDouble;
        /** holt die Anzahl der Evo-Shield-Effekte auf der Karte. **/
        PreparedStatement prepStmtCount2;
        /** holt die Anzahl der Effekte auf dem Evo-Shield. **/
        PreparedStatement prepStmtDouble2;
        
        ResultSet resultMax;
        ResultSet resultCount;
        ResultSet resultDouble;
        ResultSet resultCount2;
        ResultSet resultDouble2;
                
        int affectedRows = 0;
        
        try {
            c = DbConnection.getPostgresConnection();
           
             prepStmtMax = c.prepareStatement("select shield_max, evo_shield_max from \"Gamecard\" "
                                            + "where gid = ?");
             prepStmtMax.setInt(1, gid);   
             resultMax = prepStmtMax.executeQuery();
             resultMax.next();
             
             
             prepStmtCount =  c.prepareStatement("select count(*) from \"Card_Effect\" "
                                                 + "where gid = ? and shield is not null");
              prepStmtCount.setInt(1, gid);
             resultCount = prepStmtCount.executeQuery();
             resultCount.next();
             /** speichert die Anzahl der Shield-Effekte auf der Karte ein. **/
             int countShield = resultCount.getInt(1);
             
             
             prepStmtDouble = c.prepareStatement("select count(*) from \"Card_Effect\""
                                                  + " where gid = ? and shield = ?");              
             prepStmtDouble.setInt(1, gid);
             prepStmtDouble.setObject(2, shield);            
             resultDouble = prepStmtDouble.executeQuery();
             resultDouble.next();
             /** speichert die Anzahl der Shield-Effekte auf dem Shield ein. **/
             int doubleShield = resultDouble.getInt(1);
             
             
             prepStmtCount2 =  c.prepareStatement("select count(*) from \"Card_Effect\" "
                                                 + "where gid = ? and evo_shield is not null");
              prepStmtCount2.setInt(1, gid);
             resultCount2 = prepStmtCount2.executeQuery();
             resultCount2.next();
             /** speichert die Anzahl der Evo-Shield-Effekte auf der Karte ein. **/
             int countShield2 = resultCount2.getInt(1);
             
             
             prepStmtDouble2 = c.prepareStatement("select count(*) from \"Card_Effect\""
                                                  + " where gid = ? and evo_shield = ?");              
             prepStmtDouble2.setInt(1, gid);
             prepStmtDouble2.setObject(2, evo_shield);            
             resultDouble2 = prepStmtDouble2.executeQuery();
             resultDouble2.next();
             /** speichert die Anzahl der Evo-Shield-Effekte auf dem Shield ein. **/
             int doubleShield2 = resultDouble2.getInt(1);
             
             
        
              prepStmtCard = c.prepareStatement("update \"Card_Effect\" set gid = ?, "
                                                + "eid = ?, shield = ?,"
                                                + " evo_shield = ? where c_eid = ?");  
              /**Update der GameCard-ID**/
              prepStmtCard.setInt(1, gid);
              /**Update der Effekt-ID**/
              prepStmtCard.setInt(2, eid);
              /**Update des betroffenen Shields**/
              if(shield != null) {
                  int i = (int) shield;
                  if (i >= resultMax.getInt(1) - 1 || i == -1 && resultMax.getInt(1) == 1) {
                      throw new IllegalArgumentException("Die Karte hat nicht so viele Schilder!");
                  }
                  else if(i == -1 && countShield > 1){
                      throw new IllegalArgumentException("Ungueltiger Wertebereich fuer Shields.");
                  }
                  else if(doubleShield > 1){
                      throw new IllegalArgumentException("Auf diesem Schild gibt es bereits einen Effekt!");
                  }           
                  prepStmtCard.setObject(3, i);
                  }
              else{
                  prepStmtCard.setObject(3, shield);
                  }
              /**Update des betroffenen Evo-Shields**/
              if(evo_shield != null) {
                  int i = (int) evo_shield;
                  if (i > resultMax.getInt(2) || i == 0) {
                      throw new IllegalArgumentException("Ungueltiger Wertebereich fuer Evo-Schilder!");
                  }
                  else if(i == -1 && countShield2 >1) {
                      throw new IllegalArgumentException("Ungueltiger Wertebereich fuer Evo-Shield!");
                  }
                  else if(doubleShield2 > 1){
                      throw new IllegalArgumentException("Auf diesem Evo-Shied gibt es bereits einen Effekt!");
                  }
                  prepStmtCard.setObject(4, i);
              }
              else{
                  prepStmtCard.setObject(4, evo_shield);
              }
              /**Auswahl des eindeutigen Effekts**/
              prepStmtCard.setInt(5, c_eid);
     
     
          affectedRows = prepStmtCard.executeUpdate();
        
        } 
        catch (SQLException | ClassNotFoundException ex) {
          Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);          
      }
        return affectedRows;       
    }
    
public boolean insert_duplicate(int did, int gid) throws IllegalArgumentException {
        
        PreparedStatement prepStmtInsert;
        PreparedStatement prepStmtCount;
        
        ResultSet resultCount;
        
        try {
            c = DbConnection.getPostgresConnection();
            
            prepStmtCount = c.prepareStatement("select count(*) from \"Deck_Cards\" where did = ? and gid = ?");
            prepStmtCount.setInt(1, did);
            prepStmtCount.setInt(2, gid);
            resultCount = prepStmtCount.executeQuery();
            resultCount.next();
            /**Speichert die Anzahl der Eintraege dieser Karte im Deck ein**/
            int countCard = resultCount.getInt(1);
               
           if(countCard == 3){
               throw new IllegalArgumentException("Diese Karte ist bereits drei mal im Deck!");
           }
           
           prepStmtInsert = c.prepareStatement("insert into \"Deck_Cards\" (did, gid) select ?,?");
           prepStmtInsert.setInt(1, did);
           prepStmtInsert.setInt(2, gid);
           prepStmtInsert.executeUpdate();
           
            c.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
	/**Zeigt die Verteilung der Karten im Deck.
	 * Ebenso auch die Gesamtanzahl.
	 * @param deckName Name des Decks.
	 */
	public void showAmountOfCards(String deckName){
		PreparedStatement pst, pst2, pst3;
		ResultSet rs, rs2, rs3;
		
		try{
			c = DbConnection.getPostgresConnection();
			pst = c.prepareStatement("select g.name, count(dc.gid) from \"Gamecard\" g, \"Deck_Cards\" dc, \"Deck\" d "+
					"where g.gid = dc.gid "+
					"and dc.did = d.did "+
					"and LOWER(d.name) = LOWER(?) "+
					"group by g.name");
			
			pst.setString(1, deckName);
			rs = pst.executeQuery();
			
			pst3 = c.prepareStatement("select sp.name, count(dc.sid) from \"Specialcard\" sp, \"Deck\" d, \"Deck_Cards\" dc "+
					"where d.did = dc.did "+
					"and sp.sid = dc.sid "+
					"and LOWER(d.name) = LOWER(?) "+
					"group by sp.name");
			
			pst3.setString(1, deckName);
			rs3 = pst3.executeQuery();
			
			
			pst2 = c.prepareStatement("select count(gid)+count(sid) from \"Deck\" d, \"Deck_Cards\" dc "+
					"where d.did = dc.did "+
					"and LOWER(name) = LOWER(?)");
			pst2.setString(1, deckName);
			rs2 = pst2.executeQuery();
			while(rs.next()){
				System.out.println(rs.getString(1)+":\t "+rs.getInt(2));
			}
			System.out.println("-----------------------------------------------------");
			while(rs3.next()){
				System.out.println(rs3.getString(1)+":\t "+rs3.getInt(2));
			}
			rs2.next();
			System.out.println("-----------------------------------------------------");
			System.out.println("Gesamt: "+rs2.getInt(1));
			
			c.close();	
		} catch (SQLException | ClassNotFoundException ex) {
			Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
	    } finally {
	        try {
	            c.close();
	        } catch (SQLException ex) {
	            Logger.getLogger(DbCard.class.getName()).log(Level.SEVERE, null, ex);
	        }
    }
	}

}
