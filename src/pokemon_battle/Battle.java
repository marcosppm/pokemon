package pokemon_battle;

import utils.Controller;
import utils.Event;

public class Battle extends Controller {
	private Player player1, player2;
	private Player turn, noTurn;
	private FullTurnType fullTurnType;
	
	public Battle(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		this.turn = this.player1; // player1 always starts the game
		this.noTurn = this.player2;
	}

	private class AttackWithCurrent extends Event {
		private Attack turnChosenAttack;
		private boolean dead;
		
		public AttackWithCurrent(long eventTime, Attack att) {
			super(eventTime);
			turnChosenAttack = att;
		}
		
		public void action() {
			int noTurnCurrHp = noTurn.getPokCurrent().getHp();
			int dan = turnChosenAttack.getDan();
			noTurn.getPokCurrent().setHp(noTurnCurrHp - dan);
			if (noTurn.getPokCurrent().getHp() == 0)
				dead = true;
		}
		
		public String description() {
			return (turn.getName() + "'s turn: " + "Pokémon " + turn.getPokCurrent().getName() 
					+ " attacks (" + turnChosenAttack.getName() + ")!"
					+ " " + noTurn.getName() + "'s Pokémon " + noTurn.getPokCurrent().getName()
					+ " actual HP: " + noTurn.getPokCurrent().getHp() + "."
					+ (dead ? " Pokémon " + noTurn.getPokCurrent().getName() + " is dead." : ""));
		}
	}
	
	private class ChangeCurrentPokemon extends Event {
		private Pokemon newCurrent, previous;
		
		public ChangeCurrentPokemon(long eventTime, Pokemon nc) {
			super(eventTime);
			previous = turn.getPokCurrent();
			newCurrent = nc;
		}
		
		public void action() {
			turn.setPokCurrent(newCurrent);
		}
		
		public String description() {
			return (turn.getName() + "'s turn: Pokémon " + previous.getName() + " changed to " + 
					newCurrent.getName());
		}
	}
	
	private class UseItem extends Event {
		private Item item;
		
		public UseItem(long eventTime, Item item) {
			super(eventTime);
			this.item = item;
		}
		
		public void action() {
			int actualPokHP = turn.getPokCurrent().getHp();
			turn.getPokCurrent().setHp(actualPokHP + item.getHpCure());
			item.takeOff();
		}
		
		public String description() {
			return (turn.getName() + "'s turn: Pokémon " + turn.getPokCurrent().getName() + " earned " + 
					item.getHpCure() + " HP points.");
		}
	}
	
	private class RunAway extends Event {
		private boolean pokemonsAreGone;
		
		public RunAway(long eventTime, boolean pokemonsAreGone) {
			super(eventTime);
			this.pokemonsAreGone = pokemonsAreGone;
		}
		
		public void action() {
			
		}
		
		public String description() {
			return (turn.getName() + "'s turn: He has fled of the battle... "
					+ (pokemonsAreGone ? "His/her pokémons are gone. " : "")
					+ "Player " + noTurn.getName() + " has won!!! :-)");
		}
	}
	
	private class StartBattle extends Event {
		private boolean finished = false;
		
		public StartBattle(long eventTime) {
			super(eventTime);
		}
		
		public void action() {
			int p1OrderPok = 0, p2OrderPok = 0;
			int p1OrderAtt = 0, p2OrderAtt = 0;
			int p1OrderItem = 0, p2OrderItem = 0;
			boolean p1hasItems = true, p2hasItems = true;
			
			Pokemon p1PokCurr = player1.getPokCurrent();
			Pokemon p2PokCurr = player2.getPokCurrent();
			Attack p1AttCurr = p1PokCurr.getAttCurrent();
			Attack p2AttCurr = p2PokCurr.getAttCurrent();
			Item p1ItemCurr = player1.getItemCurrent();
			Item p2ItemCurr = player2.getItemCurrent();
			
			while (!finished) {
				// if the priorities are equals, the player1 is the first a attack
				if (p1AttCurr.getPriority() <= p2AttCurr.getPriority()) {
					fullTurnType = FullTurnType.P1_ATTACK_P2_ATTACK_P1_PRIORITY;
				} else {
					fullTurnType = FullTurnType.P1_ATTACK_P2_ATTACK_P2_PRIORITY;
				}
				
				switch (fullTurnType) {
				case P1_ATTACK_P2_ATTACK_P1_PRIORITY:
					giveTurn(player1);
					addEvent(new Battle.AttackWithCurrent(
							System.currentTimeMillis(),
							p1AttCurr));
					p1OrderAtt = (p1OrderAtt + 1) % p1PokCurr.getAttacks().length;
					p1AttCurr = p1PokCurr.getAttacks()[p1OrderAtt];
					p1PokCurr.setAttCurrent(p1AttCurr);
					
					giveTurn(player2);
					if (p2PokCurr.isAlive()) {
						addEvent(new Battle.AttackWithCurrent(
								System.currentTimeMillis(),
								p2AttCurr));
						p2OrderAtt = (p2OrderAtt + 1) % p2PokCurr.getAttacks().length;
						p2AttCurr = p2PokCurr.getAttacks()[p2OrderAtt];
						p2PokCurr.setAttCurrent(p2AttCurr);
					} else {
						if (p2hasItems) {
							// use item
							addEvent(new Battle.UseItem(
									System.currentTimeMillis(),
									p2ItemCurr));
							if (p2ItemCurr.getQuantity() == 0) {
								if (++p2OrderItem == player2.getItems().length) {
									p2hasItems = false;
								} else {
									p2ItemCurr = player2.getItems()[p2OrderItem];
								}
							}
						} else {
							// try to change pokémon
							boolean pokemonsAreGone = (++p2OrderPok == player2.getPokemons().length);
							if (pokemonsAreGone) {
								addEvent(new Battle.RunAway(
										System.currentTimeMillis(),
										pokemonsAreGone));
							} else {
								p2PokCurr = player2.getPokemons()[p2OrderPok];
								addEvent(new Battle.ChangeCurrentPokemon(
										System.currentTimeMillis(),
										p2PokCurr));
							}
						}
					}
					break;
				
				case P1_ATTACK_P2_ATTACK_P2_PRIORITY:
					giveTurn(player2);
					addEvent(new Battle.AttackWithCurrent(
							System.currentTimeMillis(),
							p2AttCurr));
					p2OrderAtt = (p2OrderAtt + 1) % p2PokCurr.getAttacks().length;
					p2AttCurr = p2PokCurr.getAttacks()[p2OrderAtt];
					p2PokCurr.setAttCurrent(p2AttCurr);
					
					giveTurn(player1);
					if (p1PokCurr.isAlive()) {
						addEvent(new Battle.AttackWithCurrent(
								System.currentTimeMillis(),
								p1AttCurr));
						p1OrderAtt = (p1OrderAtt + 1) % p1PokCurr.getAttacks().length;
						p1AttCurr = p1PokCurr.getAttacks()[p1OrderAtt];
						p1PokCurr.setAttCurrent(p1AttCurr);
					} else {
						if (p1hasItems) {
							// use item
							addEvent(new Battle.UseItem(
									System.currentTimeMillis(),
									p1ItemCurr));
							if (p1ItemCurr.getQuantity() == 0) {
								if (++p1OrderItem == player1.getItems().length) {
									p1hasItems = false;
								} else {
									p1ItemCurr = player1.getItems()[p1OrderItem];
								}
							}
						} else {
							// try to change pokémon
							boolean pokemonsAreGone = (++p1OrderPok == player1.getPokemons().length);
							if (pokemonsAreGone) {
								addEvent(new Battle.RunAway(
										System.currentTimeMillis(),
										pokemonsAreGone));
							} else {
								p1PokCurr = player1.getPokemons()[p1OrderPok];
								addEvent(new Battle.ChangeCurrentPokemon(
										System.currentTimeMillis(),
										p1PokCurr));
							}
						}
					}
				}
			}
		}
		
		public String description() {
			return "The battle has started!";
		}
	}
	
	private void giveTurn(Player pl) {
		if (pl == player1) {
			turn = player1;
			noTurn = player2;
		} else {
			turn = player2;
			noTurn = player1;
		}
	}
	
	private void prepareBattle() {
		// player 1
		Pokemon[] player1Pokemons = new Pokemon[6];
		
		Attack[] attacksBulbasaur = new Attack[4];
		attacksBulbasaur[0] = new Attack("Solar Beam", AttackType.PLANT, 120, 3);
		attacksBulbasaur[1] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		attacksBulbasaur[2] = new Attack("Petal Dance", AttackType.PLANT, 70, 2);
		attacksBulbasaur[3] = new Attack("Razor Leaf", AttackType.PLANT, 55, 1);
		player1Pokemons[0] = new Pokemon("Bulbasaur", 500, attacksBulbasaur);
		
		Attack[] attacksCharmander = new Attack[4];
		attacksCharmander[0] = new Attack("Ember", AttackType.FIRE, 40, 2);
		attacksCharmander[1] = new Attack("Flame Thrower", AttackType.FIRE, 95, 2);
		attacksCharmander[2] = new Attack("Metal Claw", AttackType.METALLIC, 50, 1);
		attacksCharmander[3] = new Attack("Rage", AttackType.NORMAL, 20, 0);
		player1Pokemons[1] = new Pokemon("Charmander", 500, attacksCharmander);
		
		Attack[] attacksSandslash = new Attack[3];
		attacksSandslash[0] = new Attack("Swift", AttackType.NORMAL, 60, 1);
		attacksSandslash[1] = new Attack("Fury Swipes", AttackType.NORMAL, 35, 0);
		attacksSandslash[2] = new Attack("Slash", AttackType.NORMAL, 70, 1);
		player1Pokemons[2] = new Pokemon("Sandslash", 500, attacksSandslash);
		
		Attack[] attacksPidgey = new Attack[1];
		attacksPidgey[0] = new Attack("Gust", AttackType.FLIER, 40, 0);
		player1Pokemons[3] = new Pokemon("Pidgey", 500, attacksPidgey);
		
		Attack[] attacksPikachu = new Attack[4];
		attacksPikachu[0] = new Attack("Thunderbolt", AttackType.ELECTRIC, 95, 1);
		attacksPikachu[1] = new Attack("Quick Attack", AttackType.NORMAL, 40, 0);
		attacksPikachu[2] = new Attack("Iron Tail", AttackType.METALLIC, 100, 2);
		attacksPikachu[3] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		player1Pokemons[4] = new Pokemon("Pikachu", 500, attacksPikachu);
		
		Attack[] attacksSquirtle = new Attack[4];
		attacksSquirtle[0] = new Attack("Bubble", AttackType.WATER, 20, 0);
		attacksSquirtle[1] = new Attack("Hydro Pump", AttackType.WATER, 120, 2);
		attacksSquirtle[2] = new Attack("Ice Beam", AttackType.ICE, 95, 1);
		attacksSquirtle[3] = new Attack("Skull Bash", AttackType.NORMAL, 100, 1);
		player1Pokemons[5] = new Pokemon("Squirtle", 500, attacksSquirtle);
		
		Item[] player1Items = new Item[2];
		player1Items[0] = new Item("HP Up", 100, 3);
		player1Items[1] = new Item("Health Wing", 150, 1);
		
		player1 = new Player("Marcos Paulo", player1Pokemons, player1Items);
		
		// player 2
		Pokemon[] player2Pokemons = new Pokemon[6];
		
		Attack[] attacksVenusaur = new Attack[4];
		attacksVenusaur[0] = new Attack("Vine Whip", AttackType.PLANT, 35, 1);
		attacksVenusaur[1] = new Attack("Razor Leaf", AttackType.PLANT, 55, 1);
		attacksVenusaur[2] = new Attack("Sweet Scent", AttackType.NORMAL, 0, 0);
		attacksVenusaur[3] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		player2Pokemons[0] = new Pokemon("Venusaur", 500, attacksVenusaur);
		
		Attack[] attacksCharizard = new Attack[4];
		attacksCharizard[0] = new Attack("Dragon Breath", AttackType.DRAGON, 60, 0);
		attacksCharizard[1] = new Attack("Flame Thrower", AttackType.FIRE, 95, 2);
		attacksCharizard[2] = new Attack("Steal Wing", AttackType.METALLIC, 70, 1);
		attacksCharizard[3] = new Attack("Iron Tail", AttackType.METALLIC, 1020, 3);
		player2Pokemons[1] = new Pokemon("Charizard", 500, attacksCharizard);
		
		Attack[] attacksButterfree = new Attack[3];
		attacksButterfree[0] = new Attack("Confusion", AttackType.PSYCHIC, 50, 1);
		attacksButterfree[1] = new Attack("Gust", AttackType.FLIER, 40, 0);
		attacksButterfree[2] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		player2Pokemons[2] = new Pokemon("Butterfree", 500, attacksButterfree);
		
		Attack[] attacksPidgeot = new Attack[3];
		attacksPidgeot[0] = new Attack("Fly", AttackType.FLIER, 70, 2);
		attacksPidgeot[1] = new Attack("Peck", AttackType.FLIER, 35, 1);
		attacksPidgeot[2] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		player2Pokemons[3] = new Pokemon("Pidgeot", 500, attacksPidgeot);
		
		Attack[] attacksArbok = new Attack[4];
		attacksArbok[0] = new Attack("Wrap", AttackType.NORMAL, 15, 0);
		attacksArbok[1] = new Attack("Acid", AttackType.POISONOUS, 40, 1);
		attacksArbok[2] = new Attack("Headbutt", AttackType.NORMAL, 70, 2);
		attacksArbok[3] = new Attack("Poison Sting", AttackType.POISONOUS, 15, 0);
		player2Pokemons[4] = new Pokemon("Arbok", 500, attacksArbok);
		
		Attack[] attacksJigglypuff = new Attack[4];
		attacksJigglypuff[0] = new Attack("Body Slum", AttackType.NORMAL, 85, 2);
		attacksJigglypuff[1] = new Attack("Rollout", AttackType.ROCK, 30, 1);
		attacksJigglypuff[2] = new Attack("Flamethrower", AttackType.FIRE, 95, 2);
		attacksJigglypuff[3] = new Attack("Doubleslap", AttackType.NORMAL, 15, 0);
		player2Pokemons[5] = new Pokemon("Jigglypuff", 500, attacksJigglypuff);
		
		Item[] player2Items = new Item[2];
		player2Items[0] = new Item("HP Up", 100, 2);
		player2Items[1] = new Item("Health Wing", 150, 2);
		
		player1 = new Player("Lucas Seiji", player2Pokemons, player2Items);
	}
	
	public static void main(String[] args) {
		Player player1 = null;
		Player player2 = null;
		Battle newBattle = new Battle(player1, player2);
		newBattle.prepareBattle();
		long tm = System.currentTimeMillis();
		newBattle.addEvent(newBattle.new StartBattle(tm));
		newBattle.run();
	}
}
