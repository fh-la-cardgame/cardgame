package cardgame.ui;

import cardgame.classes.Effect;

import cardgame.classes.GameCard;
import cardgame.classes.Type;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Abbildung der Spielkarte als Controlelement der GUI
 *
 * @author BishaThan
 */
public class GamecardControl extends CardControl {


    /* Angriffspunkte **/
    private Label atk;
    /* Schwarze Schilder **/
    private Label card_black_shield;
    /* Weiße Schilder **/
    private Label card_white_shield;
    /* Button zum Kämpfen der Karte auf das Feld **/
    private Button fight;
    /* Card Referenz */
    private GameCard card;
  

    private GamecardControl(){
        this("","","","","",Type.human, new byte[1],null,null);
        
    }
    public GamecardControl(GameCard card){
        this(card.getShields().toString(), card.getEvolutionShields().toString(), card.getName(), card.getDescription(), Integer.toString(card.getAtk()), card.getType(), card.getImage(),card.getEffects(),card.getEvoEffects());
        this.card = card;
    }

    private GamecardControl(String blackshield, String whiteshield, String name, String description, String atk, Type type, byte[] raw, Effect[] effects, Effect[] evoeffects) {
        super(name, description, type, raw);
        this.fight = new Button("Kämpfen");
        this.fight.setVisible(false);
        this.card_black_shield = new Label(blackshield);
        this.card_white_shield = new Label(whiteshield);
        this.atk = new Label(atk);
      
        
        /*Anzeige der LEBENS-SCHILDEFFEKTE*/
        if (effects != null && effects.length > 0) {
            Label l = new Label("LEBENS-SCHILDEFFEKTE:");
            l.setId("boldtext_css");
            this.getgDescription().add(l);
            
            for (int i = 0; i < effects.length; i++) {
                if (effects[i] != null) {
                  
                    if(effects[i].getAffectedShield()>=0){
                    Label s = new Label((effects[i].getAffectedShield()+1) + "/" + (effects.length+1));
                    s.setId("boldtext_css");
                    this.getgDescription().add(s);
                    this.getgDescription().add(new Label(effects[i].getDescription()));
                    }
                    else{
                        Label s = new Label("Effekt tritt bei jedem verlorenen Lebensschild ein:");
                        s.setId("boldtext_css");
                        this.getgDescription().add(s);
                        this.getgDescription().add(new Label(effects[i].getDescription()));
                        i = i + 1;
                    }              
                }
            }

            this.getgDescription().add(new Label(""));
        }
       
        /*Anzeige der EVOLUTIONS-SCHILDEFFEKTE*/
        if (evoeffects != null && evoeffects.length > 0) {
            Label l = new Label("EVOLUTIONS-SCHILDEFFEKTE:");
            l.setId("boldtext_css");
            this.getgDescription().add(l);
            
            for (int i = 0; i < evoeffects.length; i++) {
                if (evoeffects[i] != null) {
                    
                    if(evoeffects[i].getAffectedShield() >= 0){
                    Label s = new Label((evoeffects[i].getAffectedShield()) + "/" + evoeffects.length); 
                    s.setId("boldtext_css");
                    this.getgDescription().add(s);
                    this.getgDescription().add(new Label(evoeffects[i].getDescription())); 
                    }
                    else{
                        Label s = new Label("Effekt tritt bei jedem gewonnen Evolutions-Schild ein:");
                        s.setId("boldtext_css");
                        this.getgDescription().add(s);
                        this.getgDescription().add(new Label(evoeffects[i].getDescription()));
                        break;
                    }
                    
                }

            }
        }



        positionAdditionalElements();
        setAdditionalSizesAndPosition();
        setAdditioanalCSS();

    }



    /**
     * Positionierung der Elemente im Grid.
     */
    @Override
    protected void positionAdditionalElements() {
        this.add(card_black_shield, 0, 3);
        this.add(card_white_shield, 1, 3);
        this.add(atk, 0, 2, 2, 1);
        this.add(fight, 0, 4, 2, 1);

    }

    public void bindAll(){
        card.setpAtk();
        card.getShields().setgShield();
        card.getEvolutionShields().setgShield();


        BindSetting.bindLabel(this.atk, card.getpAtk().asString());
        BindSetting.bindLabel(this.card_black_shield, card.getShields().getgShield());
        BindSetting.bindLabel(this.card_white_shield, card.getEvolutionShields().getgShield());


    }

    public void unbindAll(){
        BindSetting.unbindLabel(this.atk);
        BindSetting.unbindLabel(this.card_black_shield);
        BindSetting.unbindLabel(this.card_white_shield);
    }

    /**
     * Anpassung der Groesse und Ausrichtung.
     */
    @Override
    protected void setAdditionalSizesAndPosition() {
        //Padding
        Insets i = new Insets(2, 2, 2, 2);
        this.card_black_shield.setPadding(i);
        this.card_white_shield.setPadding(i);
    }

    private void setAdditioanalCSS(){
        StyleSetting.setButtonCss(this.fight);
    }

    /**
     * Getter
     * @return schwarze Schilder
     */
    public Label getCard_black_shield() {
        return card_black_shield;
    }

    /**
     * Setter
     * @param card_black_shield schwarze Schilder
     */
    public void setCard_black_shield(Label card_black_shield) {
        this.card_black_shield = card_black_shield;
    }

    /**
     * Getter
     * @return weiße Schilder 
     */
    public Label getCard_white_shield() {
        return card_white_shield;
    }

    /**
     * Setter
     * @param card_white_shield weiße Schilder
     */
    public void setCard_white_shield(Label card_white_shield) {
        this.card_white_shield = card_white_shield;
    }

    public GameCard getCard() {
        return card;
    }

    public void setCard(GameCard card) {
        this.card = card;
    }

    public Button getFight() {
        return fight;
    }
}
