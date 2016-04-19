package pokemon_battle;

import utils.Controller;
import utils.Event;

public class Battle extends Controller {
	private Player player1, player2;
	private Player turn, noTurn;
	private TurnType turnType;
	
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
					+ (dead ? " Pokémon " + noTurn.getPokCurrent().getName() + "" : ""));
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
				if (p1PokCurr.isAlive()) {
					if (p2PokCurr.isAlive()) {
						if (p1PokCurr.getHp() > 20) { // p1 will attack
							if (p2PokCurr.getHp() > 20) { // p2 will attack
								if (p1AttCurr.getPriority() <= p2AttCurr.getPriority()) {
									turnType = TurnType.P1_PLUS_20_P2_PLUS_20_P1_PRIORITY;
								} else {
									turnType = TurnType.P1_PLUS_20_P2_PLUS_20_P2_PRIORITY;
								}
							} else { // p2 will not attack
								turnType = TurnType.P1_PLUS_20_P2_LESS_20;
							}
						} else { // p1 will not attack
							if (p2PokCurr.getHp() > 20) {
								turnType = TurnType.P1_LESS_20_P2_PLUS_20;
							} else {
								turnType = TurnType.P1_LESS_20_P2_LESS_20;
							}
						}
					} else { // p2 dead
						if (p1PokCurr.getHp() > 20) {
							turnType = TurnType.P1_PLUS_20_P2_DEAD;
						} else {
							turnType = TurnType.P1_LESS_20_P2_DEAD;
						}
					}
				} else { // p1 dead
					if (p2PokCurr.isAlive()) {
						if (p2PokCurr.getHp() > 20) {
							turnType = TurnType.P1_DEAD_P2_PLUS_20;
						} else {
							turnType = TurnType.P1_DEAD_P2_LESS_20;
						}
					} else { // p2 dead
						turnType = TurnType.P1_DEAD_P2_DEAD;
					}
				}
				
				switch (turnType) {
				case P1_PLUS_20_P2_PLUS_20_P1_PRIORITY:
					giveTurn(player1);
					addEvent(new Battle.AttackWithCurrent(
							System.currentTimeMillis() + 1000,
							p1AttCurr));
					p1OrderAtt = (p1OrderAtt + 1) % p1PokCurr.getAttacks().length;
					p1AttCurr = p1PokCurr.getAttacks()[p1OrderAtt];
					p1PokCurr.setAttCurrent(p1AttCurr);
					
					giveTurn(player2);
					break;
				
				case P1_PLUS_20_P2_PLUS_20_P2_PRIORITY:
					break;
					
				case P1_PLUS_20_P2_LESS_20:
					break;
				
				case P1_PLUS_20_P2_DEAD:
					if (++p2OrderPok == player2.getPokemons().length) {
						// the pokémons of player2 are gone...
						giveTurn(player2);
						addEvent(new Battle.RunAway(
								System.currentTimeMillis() + 500,
								true));
						giveTurn(player1);
						finished = true;
					}
					break;
					
				case P1_LESS_20_P2_PLUS_20:
					break;
					
				case P1_LESS_20_P2_LESS_20:
					break;
					
				case P1_LESS_20_P2_DEAD:
					// player2 will change your pokémon
					giveTurn(player2);
					p2PokCurr = player2.getPokemons()[p2OrderPok];
					addEvent(new Battle.ChangeCurrentPokemon(
							System.currentTimeMillis() + 500,
							p2PokCurr));
					p2OrderAtt = 0;
					p2AttCurr = p2PokCurr.getAttacks()[0];
					p2PokCurr.setAttCurrent(p2AttCurr);
					
					giveTurn(player1);
					if (p1PokCurr.getHp() > 20 || !p1hasItems) {
						// player1 attacks!
						addEvent(new Battle.AttackWithCurrent(
								System.currentTimeMillis() + 1000,
								p1AttCurr));
						p1OrderAtt = (p1OrderAtt + 1) % p1PokCurr.getAttacks().length;
						p1AttCurr = p1PokCurr.getAttacks()[p1OrderAtt];
						p1PokCurr.setAttCurrent(p1AttCurr);
						
					} else {
						// player1 uses item
						addEvent(new Battle.UseItem(
								System.currentTimeMillis() + 1000,
								p1ItemCurr));
						if (p1ItemCurr.getQuantity() == 0) {
							if (++p1OrderItem == player1.getItems().length) {
								p1hasItems = false;
							} else {
								p1ItemCurr = player1.getItems()[p1OrderItem];
								player1.setItemCurrent(p1ItemCurr);
							}
						}	
					}
					break;
					
				case P1_DEAD_P2_PLUS_20:
					break;
					
				case P1_DEAD_P2_LESS_20:
					break;
					
				case P1_DEAD_P2_DEAD:
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
	
	public static void main(String[] args) {
	}
}
