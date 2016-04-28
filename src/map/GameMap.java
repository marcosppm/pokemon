package map;

import java.util.Random;

import pokemon_battle.Attack;
import pokemon_battle.AttackType;
import pokemon_battle.Battle;
import pokemon_battle.Item;
import pokemon_battle.Player;
import pokemon_battle.Pokeball;
import pokemon_battle.Pokemon;
import utils.Controller;
import utils.Event;

public class GameMap extends Controller {
	private Player player, wildPokemon;
	private int stepsOnTheFloor, stepsOnTheGrass;
	private int chanceWildBattle; // from 0 to 80
	private Battle currentWildBattle;
	
	private static final int DELAY_OF_EVENT = 1500;
	private static final int INITIAL_HP = 500;
	
	public GameMap() {
		this.stepsOnTheFloor = 0;
		this.chanceWildBattle = 0;
	}

	private class WalkOnFloor extends Event {
		public WalkOnFloor(long eventTime) {
			super(eventTime);
		}
		
		public void action() {
			if (stepsOnTheFloor < 3) {
				stepsOnTheFloor++;
				addEvent(new GameMap.WalkOnFloor(
						System.currentTimeMillis() + DELAY_OF_EVENT));
			} else {
				stepsOnTheFloor = 0;
				addEvent(new GameMap.WalkOnGrass(
						System.currentTimeMillis() + DELAY_OF_EVENT));
			}
		}

		public String description() {
			return (player.getName() + " has walked 100 meters on the floor...");
		}
	}
	
	private class WalkOnGrass extends Event {
		private boolean foundWildPokemon;

		public WalkOnGrass(long eventTime) {
			super(eventTime);
			this.foundWildPokemon = false;
		}
		
		public void action() {
			if (stepsOnTheGrass < 5) {
				Random random = new Random();
				int randomNumber = random.nextInt(100) + 1;
				if (randomNumber <= chanceWildBattle) {
					stepsOnTheGrass = 0;
					chanceWildBattle = 0;
					foundWildPokemon = true;
					boolean wild = true;
					currentWildBattle = new Battle(player, wildPokemon, wild);					
				} else {
					stepsOnTheGrass++;
					chanceWildBattle = 20 * stepsOnTheGrass;
					addEvent(new GameMap.WalkOnGrass(
							System.currentTimeMillis() + DELAY_OF_EVENT));
				}
			} else {
				stepsOnTheGrass = 0;
				chanceWildBattle = 0;
				addEvent(new GameMap.WalkOnFloor(
						System.currentTimeMillis() + DELAY_OF_EVENT));
			}
		}

		public String description() {
			return player.getName() + " has walked 100 meters on the grass..."
				   + (foundWildPokemon ? " And has found a Wild Pokémon!!" : "");
		}
	}
	
	private void createPlayer() {
		// player
		Pokemon[] playerPokemons = new Pokemon[6];
		
		Attack[] attacksVenusaur = new Attack[4];
		attacksVenusaur[0] = new Attack("Vine Whip", AttackType.GRASS, 35, 1);
		attacksVenusaur[1] = new Attack("Razor Leaf", AttackType.GRASS, 55, 1);
		attacksVenusaur[2] = new Attack("Sweet Scent", AttackType.NORMAL, 0, 0);
		attacksVenusaur[3] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		playerPokemons[0] = new Pokemon("Venusaur", INITIAL_HP, attacksVenusaur, 
				AttackType.GRASS, 45);
		
		Attack[] attacksCharizard = new Attack[4];
		attacksCharizard[0] = new Attack("Dragon Breath", AttackType.DRAGON, 60, 0);
		attacksCharizard[1] = new Attack("Flame Thrower", AttackType.FIRE, 95, 2);
		attacksCharizard[2] = new Attack("Steal Wing", AttackType.STEEL, 70, 1);
		attacksCharizard[3] = new Attack("Iron Tail", AttackType.STEEL, 1020, 3);
		playerPokemons[1] = new Pokemon("Charizard", INITIAL_HP, attacksCharizard, 
				AttackType.FIRE, 45);
		
		Attack[] attacksButterfree = new Attack[3];
		attacksButterfree[0] = new Attack("Confusion", AttackType.PSYCHIC, 50, 1);
		attacksButterfree[1] = new Attack("Gust", AttackType.FLYING, 40, 0);
		attacksButterfree[2] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		playerPokemons[2] = new Pokemon("Butterfree", INITIAL_HP, attacksButterfree, 
				AttackType.BUG, 45);
		
		Attack[] attacksPidgeot = new Attack[3];
		attacksPidgeot[0] = new Attack("Fly", AttackType.FLYING, 70, 2);
		attacksPidgeot[1] = new Attack("Peck", AttackType.FLYING, 35, 1);
		attacksPidgeot[2] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		playerPokemons[3] = new Pokemon("Pidgeot", INITIAL_HP, attacksPidgeot, 
				AttackType.FLYING, 45);
		
		Attack[] attacksArbok = new Attack[4];
		attacksArbok[0] = new Attack("Wrap", AttackType.NORMAL, 15, 0);
		attacksArbok[1] = new Attack("Acid", AttackType.POISON, 40, 1);
		attacksArbok[2] = new Attack("Headbutt", AttackType.NORMAL, 70, 2);
		attacksArbok[3] = new Attack("Poison Sting", AttackType.POISON, 15, 0);
		playerPokemons[4] = new Pokemon("Arbok", INITIAL_HP, attacksArbok, 
				AttackType.POISON, 90);
		
		Attack[] attacksJigglypuff = new Attack[4];
		attacksJigglypuff[0] = new Attack("Body Slum", AttackType.NORMAL, 85, 2);
		attacksJigglypuff[1] = new Attack("Rollout", AttackType.ROCK, 30, 1);
		attacksJigglypuff[2] = new Attack("Flamethrower", AttackType.FIRE, 95, 2);
		attacksJigglypuff[3] = new Attack("Doubleslap", AttackType.NORMAL, 15, 0);
		playerPokemons[5] = new Pokemon("Jigglypuff", INITIAL_HP, attacksJigglypuff, 
				AttackType.FAIRY, 170);
		
		Item[] playerItems = new Item[2];
		playerItems[0] = new Item("HP Up", 100, 2);
		playerItems[1] = new Item("Health Wing", 150, 2);
		
		Pokeball[] player2Pokeballs = new Pokeball[3];
		player2Pokeballs[0] = new Pokeball("Pok� ball", 1);
		player2Pokeballs[1] = new Pokeball("Great ball", 1.5);
		player2Pokeballs[2] = new Pokeball("Ultra ball", 2);
		
		player = new Player("Lucas Seiji", playerPokemons, playerItems, player2Pokeballs, false);
	}
	
	private void createWildPokemon() {
		// wild pok�mon
		Pokemon[] wildPokemon = new Pokemon[1];
		
		Attack[] attacksPikachu = new Attack[4];
		attacksPikachu[0] = new Attack("Thunderbolt", AttackType.ELECTRIC, 95, 1);
		attacksPikachu[1] = new Attack("Quick Attack", AttackType.NORMAL, 40, 0);
		attacksPikachu[2] = new Attack("Iron Tail", AttackType.STEEL, 100, 2);
		attacksPikachu[3] = new Attack("Tackle", AttackType.NORMAL, 35, 0);
		wildPokemon[0] = new Pokemon("Pikachu", INITIAL_HP, attacksPikachu, 
				AttackType.ELECTRIC, 190);
		
		Item[] wildPokemonItems = {};
		
		Pokeball[] wildPokPokeballs = {};
		
		this.wildPokemon = new Player("Wild Pok�mon", wildPokemon, wildPokemonItems, 
				wildPokPokeballs, true);
	}
	
	public static void main(String[] args) {
		// find a wild battle in grass (exercise 2)
		GameMap gmap = new GameMap();
		gmap.createPlayer();
		gmap.createWildPokemon();
		long tm = System.currentTimeMillis();
		gmap.addEvent(gmap.new WalkOnFloor(tm));
		gmap.run(); // starts walking
		// when finishes walking, finds a wild pok�mon to fight 
		gmap.currentWildBattle.addEvent(gmap.currentWildBattle.
				new StartBattle(System.currentTimeMillis() + DELAY_OF_EVENT));
		gmap.currentWildBattle.run();
	}
	
}
