package cardgame.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Stream;

import cardgame.classes.Card;
import cardgame.classes.Effect;
import cardgame.classes.EffectType;
import cardgame.classes.GameCard;
import cardgame.classes.Playground;
import cardgame.classes.SpecialCard;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

public class TestPlayer implements KiPlayer {
	
	
	private static final boolean KAMIKAZE = false;
	private final int id;
    private final Game game;
    private final Playground myPlayground;
    
    public TestPlayer(Game game,int id) throws LogicException{
        this.game = game;
        this.id = id;
        myPlayground = game.getMyField(id);
    }

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void yourTurn() throws LogicException {
		playCards();
        attack();

	}
	
	
	
	private void playCards() throws LogicException{
		List<Card> monsterCards = game.getCardsOnHand(id);
		List<Card> specialCards = new ArrayList<>();
		GameCard monsterCardPlaying = null;
		SpecialCard specialCardPlaying = null;
		boolean hasDestroyEffect = false;
		List<SpecialCard> removes = new ArrayList<>();
		for(Card card: monsterCards){
			if(card instanceof SpecialCard){
				if(((SpecialCard)card).getEffects().stream().filter(e -> e.getEffectType() == EffectType.destroy).findFirst().isPresent()){
					hasDestroyEffect = true;
					specialCardPlaying = ((SpecialCard)card);
				}
				if(!hasDestroyEffect && specialCardPlaying != null){
					try{
					specialCardPlaying = specialCardPlaying.getEffects().stream().map(e -> e.getEffectNumber()).max(Integer::max).get() < ((SpecialCard)card).getEffects().stream().map(e -> e.getEffectNumber()).max(Integer::max).get() ? (SpecialCard)card : specialCardPlaying;
					}catch(NoSuchElementException ex){
							System.out.println(specialCardPlaying+"\n"+card);
							throw new NoSuchElementException();
						}
					}else if(!hasDestroyEffect && specialCardPlaying == null){
					specialCardPlaying = (SpecialCard)card;
				}
				specialCards.add(card);
//				monsterCards.remove(card);
//				removes.add((SpecialCard)card);
				
			}else{
				if(monsterCardPlaying == null){
					monsterCardPlaying = (GameCard)card;
				}else{
					monsterCardPlaying = monsterCardPlaying.getAtk() < ((GameCard)card).getAtk() ? (GameCard)card : monsterCardPlaying;
				}
			}
		}
		
		  if(myPlayground.canPlayMonsterCard() && monsterCardPlaying != null){
				 game.playCard(id, monsterCardPlaying);
				 if(DELAY)
			  		delay();
			  	
		  }
		  //--------------------------------------------------------------------------------------------------------
		  GameCard targetCard = null;
		  boolean targetCardSelected = false;
		  boolean playableAddition = false, playableSubtraction = false;
		  if(specialCardPlaying != null && myPlayground.canPlaySpecialCard() && specialCardPlaying.needGameCard()){
			  for(Effect effect: specialCardPlaying.getEffects()){
				  if(effect.getEffectType() == EffectType.addition_one && myPlayground.getCountBattlegroundMonster() > 0){
					  for(GameCard card: myPlayground.getBattlegroundMonster()){
						  if(targetCard != null && card != null){
							  playableAddition = true;
							  targetCard = targetCard.getAtk() < card.getAtk() ? card : targetCard;
						  }else if(targetCard == null && card != null){
							  playableAddition = true;
							  targetCard = card;
						  }
					  }
					  
				  }else if((effect.getEffectType() == EffectType.substraction_one || effect.getEffectType() == EffectType.destroy) && game.getEnemyField(id).getCountBattlegroundMonster() > 0){
					  for(GameCard card: game.getEnemyField(id).getBattlegroundMonster()){
						  if(targetCard != null && card != null){
							  playableSubtraction = true;
							  targetCard = targetCard.getAtk() < card.getAtk() ? card : targetCard;
						  }else if(targetCard == null && card != null){
							  playableSubtraction = true;
							  targetCard = card;
						  }
					  }
				  }
			  }
			  if(playableAddition || playableSubtraction){
			  game.playSpecialCard(id, specialCardPlaying, targetCard);
			  if(DELAY)
			  		delay();
			  }
		  }else if(specialCardPlaying != null && myPlayground.canPlaySpecialCard()){
			  game.playSpecialCard(id, specialCardPlaying, null);
			  if(DELAY)
			  		delay();
		  }
		 //------------------------------------------------------------------------------------------------------------------------- 
		  
	}
	
	private void attack() throws LogicException{
		 if (game.getRound() != 0 && myPlayground.getCountBattlegroundMonster() != 0){
			List<GameCard> myField = new ArrayList<>(Arrays.asList(game.getMyField(id).getBattlegroundMonster()));
			int i = 0;
			int times = myField.size();
			while(i < times){
				List<GameCard> enemyField = new ArrayList<>(Arrays.asList(game.getEnemyField(id).getBattlegroundMonster()));
				GameCard aggressor = null;
				GameCard victim = null;
				for(GameCard card: myField){
					if(card != null){
						if(aggressor != null){
							aggressor = aggressor.getAtk() < card.getAtk() ? aggressor : card; 
						}else{
							aggressor = card;
						}
					}
				}
				if(aggressor != null){
					for(GameCard card: enemyField){
						if(card != null){
							if(victim != null){
								victim = (card.getAtk() < aggressor.getAtk()) && (card.getAtk() > victim.getAtk()) ? card : victim; 
							}else{
								victim = card.getAtk() < aggressor.getAtk() ? card : null;
							}
						}
					}
					
					if(victim != null && game.getEnemyField(id).getCountBattlegroundMonster() != 0){
						game.attack(id, aggressor, victim);
						if(DELAY)
					  		delay();
						
					}else if(game.getEnemyField(id).getCountBattlegroundMonster() == 0){
						game.attack(id, aggressor, null);
						if(DELAY)
					  		delay();
					}
				}
				myField.remove(aggressor);
				i++;
			} 
			if(!myField.isEmpty() && KAMIKAZE){
				kamikaze(myField);
			}
		 }
	}
	
	private void kamikaze(List<GameCard> myField) throws LogicException{
		List<GameCard> enemyField = new ArrayList<>(Arrays.asList(game.getEnemyField(id).getBattlegroundMonster()));
		if(!enemyField.isEmpty()){
			for(GameCard myCard: myField){
				enemyField = new ArrayList<>(Arrays.asList(game.getEnemyField(id).getBattlegroundMonster()));
				for(GameCard card: enemyField){
					if(card.getEvolution() != null || Stream.of(card.getEvoEffects()).filter(e -> (e.getEffectType() == EffectType.addition_one) ||
																									e.getEffectType() == EffectType.addition_deck).findFirst().get() !=null){
						enemyField.remove(card);
					}
				}
				if(myCard.getShields().getCurrentShields() >= 2 && Stream.of(myCard.getEffects()).filter(e -> e.getEffectType() == EffectType.addition_deck || e.getEffectType() == EffectType.addition_one).findFirst().get() != null){
					game.attack(id, myCard, enemyField.get(0));
					if(DELAY){
						delay();
					}
				}
			}
		}
		
	}

	@Override
	public void endGame(boolean won) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getId() {
		return id;
	}

}