package cardgame.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import cardgame.classes.Card;
import cardgame.classes.EffectType;
import cardgame.classes.GameCard;
import cardgame.classes.Playground;
import cardgame.classes.SpecialCard;
import cardgame.logic.Game;
import cardgame.logic.LogicException;

public class TestPlayerProtokoll implements KiPlayer {
	private static final boolean KAMIKAZE = false;
	private final int id;
    private final Game game;
    private final Playground myPlayground;
    
    public TestPlayerProtokoll(Game game,int id) throws LogicException{
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
				System.out.println("Waelbare SpecialCard: "+card);
				if(((SpecialCard)card).getEffects().stream().filter(e -> e.getEffectType() == EffectType.destroy).findFirst().isPresent()){
					hasDestroyEffect = true;
					specialCardPlaying = ((SpecialCard)card);
				}
				//Bei Fehler clonen
				if(!hasDestroyEffect && specialCardPlaying != null){
					specialCardPlaying = specialCardPlaying.getEffects().stream().map(e -> e.getEffectNumber()).max(Integer::max).get() < ((SpecialCard)card).getEffects().stream().map(e -> e.getEffectNumber()).max(Integer::max).get() ? (SpecialCard)card : specialCardPlaying;
				}else if(!hasDestroyEffect && specialCardPlaying == null){
					specialCardPlaying = (SpecialCard)card;
				}
				System.out.println("Gewaelte SpecialCard: "+specialCardPlaying);
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
			  System.out.println("NEUE KARTE: "+monsterCardPlaying.getName());
		  }
		  //--------------------------------------------------------------------------------------------------------
//		  GameCard targetCard = null;
//		  boolean targetCardSelected = false;
//		  if(myPlayground.canPlaySpecialCard() && specialCardPlaying != null && specialCardPlaying.needGameCard()){
//			  if(specialCardPlaying.getEffects().stream().filter(e -> e.getEffectType().toString().startsWith("substraction") || e.getEffectType().toString().startsWith("destroy")) !=null && game.getEnemyField(id).getBattlegroundMonster().length != 0){
//				  for(GameCard card : game.getEnemyField(id).getBattlegroundMonster()){
//					  if(targetCard != null){
//						  targetCard = targetCard.getAtk() > card.getAtk() ? targetCard : card;
//					  }else{
//						  targetCard = card;
//						  targetCardSelected = true;
//					  }
//				  }
//			  }
//			  if(specialCardPlaying.getEffects().stream().filter(e -> e.getEffectType().toString().startsWith("addition")) !=null && game.getMyField(id).getBattlegroundMonster().length != 0){
//				  for(GameCard card : game.getMyField(id).getBattlegroundMonster()){
//					  if(targetCard != null){
//						  targetCard = targetCard.getAtk() > card.getAtk() ? targetCard : card;
//					  }else{
//						  targetCard = card;
//						  targetCardSelected = true;
//					  }
//				  }
//			  }
//			  
//			  
//			  if(targetCard != null){
//			  game.playSpecialCard(id, specialCardPlaying, targetCard);
//			  System.out.println("NEUE SPECIALKARTE: "+specialCardPlaying.getName()+ " AUF "+ targetCard.getName());
//			  }
//		  }else if(myPlayground.canPlaySpecialCard() && specialCardPlaying != null){
//			  if(game.getEnemyField(id).getBattlegroundMonster().length > 0)
//			  game.playSpecialCard(id, specialCardPlaying, null);
//			  System.out.println("NEUE SPECIALKARTE: "+specialCardPlaying.getName());
//		  }
//		  
		 //------------------------------------------------------------------------------------------------------------------------- 
		  
	}
	
	private void attack() throws LogicException{
		System.out.println("ROUND: "+game.getRound());
		 if (game.getRound() != 0 && myPlayground.getCountBattlegroundMonster() != 0){
			List<GameCard> myField = new ArrayList<>(Arrays.asList(game.getMyField(id).getBattlegroundMonster()));
			System.out.println("MEIN FELD: "+myField+"\n--------------");
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
						System.out.println("ANGRIFF: "+aggressor.getName() +" vs " + victim.getName());
						
					}else if(game.getEnemyField(id).getCountBattlegroundMonster() == 0){
						System.out.println("KONTROLLE: "+Arrays.asList(game.getEnemyField(id).getBattlegroundMonster()));
						System.out.println("ANGRIFF: "+aggressor.getName() +" vs gegnerischer Spieler");
						game.attack(id, aggressor, null);
					}
					System.out.println(game.getEnemyField(id).getCountBattlegroundMonster()+" - "+ game.getEnemyField(id).getPlayer().getShields().getCurrentShields());
					System.out.println(game.getMyField(id).getCountBattlegroundMonster()+" - "+ game.getMyField(id).getPlayer().getShields().getCurrentShields());
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