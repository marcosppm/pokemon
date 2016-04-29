package pokemon_battle;

import java.util.Random;

import utils.Controller;
import utils.Event;

public class Battle extends Controller {
	private Player player1, player2;
	private boolean wild;
	
	private static final int DELAY_OF_EVENT = 1500;
	private static final int INITIAL_HP = 500;
	
	public Battle(Player player1, Player player2, boolean wild) {
		this.player1 = player1;
		this.player2 = player2;
		this.wild = wild;
	}

	private class AttackWithCurrent extends Event {
		private Attack attackerChosenAttack;
		private Player attacker, attacked;
		private boolean dead, willUsePokeball;
		private double multiplier;
		
		public AttackWithCurrent(long eventTime, Attack att, Player attr, Player attd) {
			super(eventTime);
			attackerChosenAttack = att;
			attacker = attr;
			attacked = attd;
		}
		
		public void action() {
			Item attdItemCurr = attacked.getItemCurrent();
			Pokemon attrPokCurr = attacker.getPokCurrent();
			Pokemon attdPokCurr = attacked.getPokCurrent();
			Attack attdAttCurr = attdPokCurr.getAttCurrent();
			
			int attackedCurrHp = attacked.getPokCurrent().getHp();
			multiplier = TypeChart.getMultiplier(attackerChosenAttack.getType(), 
					attdPokCurr.getType()); 
			int dan = (int) (attackerChosenAttack.getDan() * multiplier);
			attacked.getPokCurrent().setHp(attackedCurrHp - dan);
			if (attacked.getPokCurrent().getHp() == 0)
				dead = true;
			
			int proxOrderAtt = (attrPokCurr.getAttOrder() + 1) % attrPokCurr.getAttacks().length;
			attrPokCurr.setAttOrder(proxOrderAtt);
			attrPokCurr.setAttCurrent(attrPokCurr.getAttacks()[proxOrderAtt]);
			
			if (attdPokCurr.isAlive()) {
				if (!wild) {
					addEvent(new Battle.AttackWithCurrent(
							System.currentTimeMillis() + DELAY_OF_EVENT,
							attdAttCurr, attacked, attacker));
				} else {
					if (attacker.isWildPokemon()) {
						addEvent(new Battle.AttackWithCurrent(
								System.currentTimeMillis() + DELAY_OF_EVENT,
								attdAttCurr, attacked, attacker));
					} else { // it's possible to use pokeballs!
						Random random = new Random();
						int randomNumber = random.nextInt(INITIAL_HP) + 1;
						int wildPokHp = attacked.getPokCurrent().getHp();
						int chanceOfUsingPokeball = INITIAL_HP - wildPokHp;
						if (randomNumber <= chanceOfUsingPokeball) {
							// use pokeball
							willUsePokeball = true;
							addEvent(new Battle.UsePokeball(
									System.currentTimeMillis() + DELAY_OF_EVENT,
									attacker.getPokeballFree(),
									attacker, attacked));
						} else {
							// attack
							addEvent(new Battle.AttackWithCurrent(
									System.currentTimeMillis() + DELAY_OF_EVENT,
									attdAttCurr, attacked, attacker));
						}
					}
				}
				
			} else {
				if (attacked.hasItens()) {
					// use item
					addEvent(new Battle.UseItem(
							System.currentTimeMillis() + DELAY_OF_EVENT,
							attdItemCurr, attacked, attacker));
				} else {
					// try to change pokemon
					attacked.setPokOrder(attacked.getPokOrder() + 1);
					boolean pokemonsAreGone = (attacked.getPokOrder() == attacked.getPokemons().length);
					if (pokemonsAreGone) {
						addEvent(new Battle.RunAway(
								System.currentTimeMillis() + DELAY_OF_EVENT,
								pokemonsAreGone, false, attacked, attacker));
					} else {
						Pokemon newCurrent = attacked.getPokemons()[attacked.getPokOrder()];
						addEvent(new Battle.ChangeCurrentPokemon(
								System.currentTimeMillis() + DELAY_OF_EVENT,
								newCurrent, attacked, attacker));
					}
				}
			}
		}
		
		public String description() {
			String descr = null;
			if (!willUsePokeball) {
				descr = (attacker.getName() + "'s turn: " + "Pokemon " + attacker.getPokCurrent().getName() 
						+ " attacks (" + attackerChosenAttack.getName() + " with multiplier " + multiplier
						+ ")! " + attacked.getName() + "'s Pokemon " + attacked.getPokCurrent().getName()
						+ " actual HP: " + attacked.getPokCurrent().getHp() + "."
						+ (dead ? " Pokemon " + attacked.getPokCurrent().getName() + " is dead." : ""));
			} else {
				descr = (attacker.getName() + " will use pokeball!");
			}
			return descr;
		}
	}
	
	private class UsePokeball extends Event {
		private Pokeball chosenPokeball;
		private Player user, wildPokemon;
		private boolean success;
		
		public UsePokeball(long eventTime, Pokeball chosenPokeball, Player user, Player wildPokemon) {
			super(eventTime);
			this.chosenPokeball = chosenPokeball;
			this.user = user;
			this.wildPokemon = wildPokemon;
		}

		public void action() {
			Random random = new Random();
			int wildPokCatchRate = wildPokemon.getPokCurrent().getCatchRate();
			int maxRealCatchRate = (int) (wildPokCatchRate * chosenPokeball.getRate());
			int minRealCatchRate = wildPokCatchRate / 3;
			int randomNumber = random.nextInt(maxRealCatchRate - minRealCatchRate) + minRealCatchRate;
			int realCatchRate = getRealCatchRate();

			if (randomNumber < realCatchRate) {
				chosenPokeball.setWildPokemon(wildPokemon.getPokCurrent());
				user.setNextPokeballFree();
				success = true;
				addEvent(new Battle.RunAway(
						System.currentTimeMillis() + DELAY_OF_EVENT,
						false, true, wildPokemon, user));
			} else {
				success = false;
				Attack wildPokAttCurr = wildPokemon.getPokCurrent().getAttCurrent();
				addEvent(new Battle.AttackWithCurrent(
						System.currentTimeMillis() + DELAY_OF_EVENT,
						wildPokAttCurr, wildPokemon, user));
			}
		}
		
		private int getRealCatchRate() {
			int wildPokHp = wildPokemon.getPokCurrent().getHp();
			int wildPokCatchRate = wildPokemon.getPokCurrent().getCatchRate();
			return (int) (((3 * INITIAL_HP - 2 * wildPokHp) * wildPokCatchRate * 
					chosenPokeball.getRate()) / (3 * INITIAL_HP));
		}

		public String description() {
			String wildPokemonName = wildPokemon.getPokCurrent().getName();
			return (user.getName() + "'s turn: He throws a Pokeball " + chosenPokeball.getName()
					+ " against " + wildPokemonName + ". ")
					+ (success ? "He's got " + wildPokemonName + "!" : "He couldn't get " 
					+ wildPokemonName + "...");
		}
	}
	
	private class ChangeCurrentPokemon extends Event {
		private Pokemon newCurrent, previous;
		private Player changer, next;
		
		public ChangeCurrentPokemon(long eventTime, Pokemon nc, Player changer, Player next) {
			super(eventTime);
			this.changer = changer;
			this.previous = changer.getPokCurrent();
			this.newCurrent = nc;
			this.next = next;
		}
		
		public void action() {
			changer.setPokCurrent(newCurrent);
			
			addEvent(new Battle.AttackWithCurrent(
					System.currentTimeMillis() + DELAY_OF_EVENT,
					next.getPokCurrent().getAttCurrent(),
					next, changer));
		}
		
		public String description() {
			return (changer.getName() + "'s turn: Pokemon " + previous.getName() + " changed to " + 
					newCurrent.getName());
		}
	}
	
	private class UseItem extends Event {
		private Item item;
		private Player user, next;
		
		public UseItem(long eventTime, Item item, Player user, Player next) {
			super(eventTime);
			this.item = item;
			this.user = user;
			this.next = next;
		}
		
		public void action() {
			int actualPokHP = user.getPokCurrent().getHp();
			user.getPokCurrent().setHp(actualPokHP + item.getHpCure());
			item.takeOff();

			if (item.getQuantity() == 0) {
				user.setItemOrder(user.getItemOrder() + 1);
				if (user.getItemOrder() != user.getItems().length) {
					user.setItemCurrent(user.getItems()[user.getItemOrder()]);
				}
			}
			
			addEvent(new Battle.AttackWithCurrent(
					System.currentTimeMillis() + DELAY_OF_EVENT,
					next.getPokCurrent().getAttCurrent(),
					next, user));
		}
		
		public String description() {
			return (user.getName() + "'s turn: Pokemon " + user.getPokCurrent().getName() + " earned " + 
					item.getHpCure() + " HP points (" + user.getPokCurrent().getHp() + " total HP " +
					"points), using " + item.getName() + " item.");
		}
	}
	
	private class RunAway extends Event {
		private boolean pokemonsAreGone, hasBeenCaught;
		private Player loser, winner;
		
		public RunAway(long eventTime, boolean pokemonsAreGone, boolean hasBeenCaught,
				Player loser, Player winner) {
			super(eventTime);
			this.loser = loser;
			this.winner = winner;
			this.pokemonsAreGone = pokemonsAreGone;
			this.hasBeenCaught = hasBeenCaught;
		}
		
		public void action() {
			// no events added anymore, the battle is finished!
		}
		
		public String description() {
			if (!wild) {
				return (loser.getName() + "'s turn: He has fled of the battle... "
						+ (pokemonsAreGone ? "His/her pokemons are gone. " : "")
						+ "Player " + winner.getName() + " has won!!! :-)");
			} else {
				if (winner.isWildPokemon()) {
					return (loser.getName() + "'s turn: "
							+ "Player " + loser.getName() + " has lost to the wild pokemon " 
							+ winner.getPokCurrent().getName() + "...");
				} else {
					return (loser.getName() + "'s turn: "
							+ (hasBeenCaught ? "The wild pokemon has been caught! " : "He's dead... ")
							+ "Player " + winner.getName() + " has won!!! :-)");
				}
			}
		}
	}
	
	public class StartBattle extends Event {
		private boolean everybodyHasPokemons;
		
		public StartBattle(long eventTime) {
			super(eventTime);
		}
		
		public void action() {
			prepareBattle();
			everybodyHasPokemons = (player1.getPokCurrent() != null && 
					player2.getPokCurrent() != null);
			if (everybodyHasPokemons) {
				Pokemon p1PokCurr = player1.getPokCurrent();
				Pokemon p2PokCurr = player2.getPokCurrent();
				Attack p1AttCurr = p1PokCurr.getAttCurrent();
				Attack p2AttCurr = p2PokCurr.getAttCurrent();
				
				// if the priorities are equals, the player1 is the first to attack
				if (p1AttCurr.getPriority() <= p2AttCurr.getPriority()) {
					addEvent(new Battle.AttackWithCurrent(
							System.currentTimeMillis() + DELAY_OF_EVENT,
							p1AttCurr, player1, player2));
				} else {
					addEvent(new Battle.AttackWithCurrent(
							System.currentTimeMillis() + DELAY_OF_EVENT,
							p2AttCurr, player2, player1));
				}
			}
		}
		
		public String description() {
			if (everybodyHasPokemons) {
				return "The battle has started!";
			} else {
				return "There are players without pokemons. The battle couldn't be started... :-(";
			}
		}
	}
	
	private void prepareBattle() {
		if (!wild) {
			// player 1
			Pokemon[] player1Pokemons = new Pokemon[6];
			
			Attack[] attacksBulbasaur = new Attack[4];
			attacksBulbasaur[0] = new Attack("Solar Beam", AttackType.GRASS, 120, 3);
			attacksBulbasaur[1] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
			attacksBulbasaur[2] = new Attack("Petal Dance", AttackType.GRASS, 70, 2);
			attacksBulbasaur[3] = new Attack("Razor Leaf", AttackType.GRASS, 55, 1);
			player1Pokemons[0] = new Pokemon("Bulbasaur", INITIAL_HP, attacksBulbasaur,
					AttackType.GRASS, 45);
			
			Attack[] attacksCharmander = new Attack[4];
			attacksCharmander[0] = new Attack("Ember", AttackType.FIRE, 40, 2);
			attacksCharmander[1] = new Attack("Flame Thrower", AttackType.FIRE, 95, 2);
			attacksCharmander[2] = new Attack("Metal Claw", AttackType.STEEL, 50, 1);
			attacksCharmander[3] = new Attack("Rage", AttackType.NORMAL, 20, 0);
			player1Pokemons[1] = new Pokemon("Charmander", INITIAL_HP, attacksCharmander, 
					AttackType.FIRE, 45);
			
			Attack[] attacksSandslash = new Attack[3];
			attacksSandslash[0] = new Attack("Swift", AttackType.NORMAL, 60, 1);
			attacksSandslash[1] = new Attack("Fury Swipes", AttackType.NORMAL, 35, 0);
			attacksSandslash[2] = new Attack("Slash", AttackType.NORMAL, 70, 1);
			player1Pokemons[2] = new Pokemon("Sandslash", INITIAL_HP, attacksSandslash, 
					AttackType.GROUND, 90);
			
			Attack[] attacksPidgey = new Attack[1];
			attacksPidgey[0] = new Attack("Gust", AttackType.FLYING, 40, 0);
			player1Pokemons[3] = new Pokemon("Pidgey", INITIAL_HP, attacksPidgey, 
					AttackType.FLYING, 255);
			
			Attack[] attacksPikachu = new Attack[4];
			attacksPikachu[0] = new Attack("Thunderbolt", AttackType.ELECTRIC, 95, 1);
			attacksPikachu[1] = new Attack("Quick Attack", AttackType.NORMAL, 40, 0);
			attacksPikachu[2] = new Attack("Iron Tail", AttackType.STEEL, 100, 2);
			attacksPikachu[3] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
			player1Pokemons[4] = new Pokemon("Pikachu", INITIAL_HP, attacksPikachu, 
					AttackType.ELECTRIC, 190);
			
			Attack[] attacksSquirtle = new Attack[4];
			attacksSquirtle[0] = new Attack("Bubble", AttackType.WATER, 20, 0);
			attacksSquirtle[1] = new Attack("Hydro Pump", AttackType.WATER, 120, 2);
			attacksSquirtle[2] = new Attack("Ice Beam", AttackType.ICE, 95, 1);
			attacksSquirtle[3] = new Attack("Skull Bash", AttackType.NORMAL, 100, 1);
			player1Pokemons[5] = new Pokemon("Squirtle", INITIAL_HP, attacksSquirtle, 
					AttackType.WATER, 45);
			
			Item[] player1Items = new Item[2];
			player1Items[0] = new Item("HP Up", 100, 3);
			player1Items[1] = new Item("Health Wing", 150, 1);
			
			Pokeball[] player1Pokeballs = {};
			
			player1 = new Player("Marcos Paulo", player1Pokemons, player1Items, 
					player1Pokeballs, false);
			
			// player 2
			Pokemon[] player2Pokemons = new Pokemon[6];
			
			Attack[] attacksVenusaur = new Attack[4];
			attacksVenusaur[0] = new Attack("Vine Whip", AttackType.GRASS, 35, 1);
			attacksVenusaur[1] = new Attack("Razor Leaf", AttackType.GRASS, 55, 1);
			attacksVenusaur[2] = new Attack("Sweet Scent", AttackType.NORMAL, 0, 0);
			attacksVenusaur[3] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
			player2Pokemons[0] = new Pokemon("Venusaur", INITIAL_HP, attacksVenusaur, 
					AttackType.GRASS, 45);
			
			Attack[] attacksCharizard = new Attack[4];
			attacksCharizard[0] = new Attack("Dragon Breath", AttackType.DRAGON, 60, 0);
			attacksCharizard[1] = new Attack("Flame Thrower", AttackType.FIRE, 95, 2);
			attacksCharizard[2] = new Attack("Steal Wing", AttackType.STEEL, 70, 1);
			attacksCharizard[3] = new Attack("Iron Tail", AttackType.STEEL, 1020, 3);
			player2Pokemons[1] = new Pokemon("Charizard", INITIAL_HP, attacksCharizard, 
					AttackType.FIRE, 45);
			
			Attack[] attacksButterfree = new Attack[3];
			attacksButterfree[0] = new Attack("Confusion", AttackType.PSYCHIC, 50, 1);
			attacksButterfree[1] = new Attack("Gust", AttackType.FLYING, 40, 0);
			attacksButterfree[2] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
			player2Pokemons[2] = new Pokemon("Butterfree", INITIAL_HP, attacksButterfree, 
					AttackType.BUG, 45);
			
			Attack[] attacksPidgeot = new Attack[3];
			attacksPidgeot[0] = new Attack("Fly", AttackType.FLYING, 70, 2);
			attacksPidgeot[1] = new Attack("Peck", AttackType.FLYING, 35, 1);
			attacksPidgeot[2] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
			player2Pokemons[3] = new Pokemon("Pidgeot", INITIAL_HP, attacksPidgeot, 
					AttackType.FLYING, 45);
			
			Attack[] attacksArbok = new Attack[4];
			attacksArbok[0] = new Attack("Wrap", AttackType.NORMAL, 15, 0);
			attacksArbok[1] = new Attack("Acid", AttackType.POISON, 40, 1);
			attacksArbok[2] = new Attack("Headbutt", AttackType.NORMAL, 70, 2);
			attacksArbok[3] = new Attack("Poison Sting", AttackType.POISON, 15, 0);
			player2Pokemons[4] = new Pokemon("Arbok", INITIAL_HP, attacksArbok, 
					AttackType.POISON, 90);
			
			Attack[] attacksJigglypuff = new Attack[4];
			attacksJigglypuff[0] = new Attack("Body Slum", AttackType.NORMAL, 85, 2);
			attacksJigglypuff[1] = new Attack("Rollout", AttackType.ROCK, 30, 1);
			attacksJigglypuff[2] = new Attack("Flamethrower", AttackType.FIRE, 95, 2);
			attacksJigglypuff[3] = new Attack("Doubleslap", AttackType.NORMAL, 15, 0);
			player2Pokemons[5] = new Pokemon("Jigglypuff", INITIAL_HP, attacksJigglypuff, 
					AttackType.FAIRY, 170);
			
			Item[] player2Items = new Item[2];
			player2Items[0] = new Item("HP Up", 100, 2);
			player2Items[1] = new Item("Health Wing", 150, 2);
			
			Pokeball[] player2Pokeballs = {};
			
			player2 = new Player("Lucas Seiji", player2Pokemons, player2Items, 
					player2Pokeballs, false);
		}
	}
	
	public static void main(String[] args) {
		// for testing a battle (exercise 1)
		Player player1 = null;
		Player player2 = null;
		boolean wild = false; // not wild
		Battle newBattle = new Battle(player1, player2, wild);
		long tm = System.currentTimeMillis();
		newBattle.addEvent(newBattle.new StartBattle(tm));
		newBattle.run();
	}
}