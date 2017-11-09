package cardgame.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Klasse zur Ausfuehrung von Effekten anhand ihres identifizierten Typs.
 * @author BishaThan
 */
public class EffectsAssignment {
    /**
     * Führt einen Effekt auf GameCards aus.
     * Gibt Karten die keine Schilde mehr haben zurück
     * @param effect Effekt der ausgeführt werden soll.
     * @param cards Karten auf denen der Effekt ausgeführt werden soll.
     * @return Karten die keine Schilder mehr haben.
     */
    public static List<GameCard> useEffect (Effect effect, GameCard... cards){
        List<GameCard> deathCards = null;
        BiConsumer<GameCard,Integer> function = effect.getEffectType().getFunction();
        int wert = effect.getEffectNumber();
        if(effect.getEffectType().isChangeShields()){
            deathCards = new ArrayList<>();
            for(GameCard card:cards) {
                if(card != null) {
                    function.accept(card, wert);
                    if (!card.isAlive()) deathCards.add(card);
                }
            }
        }else{
            for(GameCard card:cards) {
                if(card != null) function.accept(card,wert);
            }
        }
        return deathCards;
    }
}
