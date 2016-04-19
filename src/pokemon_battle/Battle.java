package pokemon_battle;

import utils.Controller;
import utils.Event;

public class Battle extends Controller {
	private Player player1, player2;
	
	public Battle(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	private class AttackWithCurrent extends Event {
		private Attack attackerChosenAttack;
		private Player attacker, attacked;
		private boolean dead;
		
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
			int dan = attackerChosenAttack.getDan();
			attacked.getPokCurrent().setHp(attackedCurrHp - dan);
			if (attacked.getPokCurrent().getHp() == 0)
				dead = true;
			
			int proxOrderAtt = (attrPokCurr.getAttOrder() + 1) % attrPokCurr.getAttacks().length;
			attrPokCurr.setAttOrder(proxOrderAtt);
			attrPokCurr.setAttCurrent(attrPokCurr.getAttacks()[proxOrderAtt]);
			
			if (attdPokCurr.isAlive()) {
				addEvent(new Battle.AttackWithCurrent(
						System.currentTimeMillis() + 500,
						attdAttCurr, attacked, attacker));
			} else {
				if (attacked.hasItens()) {
					// use item
					addEvent(new Battle.UseItem(
							System.currentTimeMillis() + 500,
							attdItemCurr, attacked, attacker));
				} else {
					// try to change pokémon
					attacked.setPokOrder(attacked.getPokOrder() + 1);
					boolean pokemonsAreGone = (attacked.getPokOrder() == attacked.getPokemons().length);
					if (pokemonsAreGone) {
						addEvent(new Battle.RunAway(
								System.currentTimeMillis() + 500,
								pokemonsAreGone, attacked, attacker));
					} else {
						Pokemon newCurrent = attacked.getPokemons()[attacked.getPokOrder()];
						addEvent(new Battle.ChangeCurrentPokemon(
								System.currentTimeMillis(),
								newCurrent, attacked, attacker));
					}
				}
			}
		}
		
		public String description() {
			return (attacker.getName() + "'s turn: " + "Pokémon " + attacker.getPokCurrent().getName() 
					+ " attacks (" + attackerChosenAttack.getName() + ")!"
					+ " " + attacked.getName() + "'s Pokémon " + attacked.getPokCurrent().getName()
					+ " actual HP: " + attacked.getPokCurrent().getHp() + "."
					+ (dead ? " Pokémon " + attacked.getPokCurrent().getName() + " is dead." : ""));
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
					System.currentTimeMillis() + 500,
					next.getPokCurrent().getAttCurrent(),
					next, changer));
		}
		
		public String description() {
			return (changer.getName() + "'s turn: Pokémon " + previous.getName() + " changed to " + 
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
				if (user.getItemOrder() != player2.getItems().length) {
					user.setItemCurrent(user.getItems()[user.getItemOrder()]);
				}
			}
			
			addEvent(new Battle.AttackWithCurrent(
					System.currentTimeMillis() + 500,
					next.getPokCurrent().getAttCurrent(),
					next, user));
		}
		
		public String description() {
			return (user.getName() + "'s turn: Pokémon " + user.getPokCurrent().getName() + " earned " + 
					item.getHpCure() + " HP points (" + user.getPokCurrent().getHp() + " total HP " +
					"points).");
		}
	}
	
	private class RunAway extends Event {
		private boolean pokemonsAreGone;
		private Player loser, winner;
		
		public RunAway(long eventTime, boolean pokemonsAreGone, Player loser, Player winner) {
			super(eventTime);
			this.loser = loser;
			this.winner = winner;
			this.pokemonsAreGone = pokemonsAreGone;
		}
		
		public void action() {
			// no events added anymore, the battle is finished!
		}
		
		public String description() {
			return (loser.getName() + "'s turn: He has fled of the battle... "
					+ (pokemonsAreGone ? "His/her pokémons are gone. " : "")
					+ "Player " + winner.getName() + " has won!!! :-)");
		}
	}
	
	private class StartBattle extends Event {
		public StartBattle(long eventTime) {
			super(eventTime);
		}
		
		public void action() {
			Pokemon p1PokCurr = player1.getPokCurrent();
			Pokemon p2PokCurr = player2.getPokCurrent();
			Attack p1AttCurr = p1PokCurr.getAttCurrent();
			Attack p2AttCurr = p2PokCurr.getAttCurrent();
			
			// if the priorities are equals, the player1 is the first to attack
			if (p1AttCurr.getPriority() <= p2AttCurr.getPriority()) {
				addEvent(new Battle.AttackWithCurrent(
						System.currentTimeMillis() + 500,
						p1AttCurr, player1, player2));
			} else {
				addEvent(new Battle.AttackWithCurrent(
						System.currentTimeMillis() + 500,
						p2AttCurr, player2, player1));
			}
		}
		
		public String description() {
			return "The battle has started!";
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
		
		player2 = new Player("Lucas Seiji", player2Pokemons, player2Items);
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
