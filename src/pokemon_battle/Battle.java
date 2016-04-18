package pokemon_battle;

import utils.Controller;
import utils.Event;

public class Battle extends Controller {
	private Player player1, player2;
	private Player turn, noTurn;
	
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
			int noTurnCurrHp = noTurn.getCurrent().getHp();
			int dan = turnChosenAttack.getDan();
			noTurn.getCurrent().setHp(noTurnCurrHp - dan);
			if (noTurn.getCurrent().getHp() == 0)
				dead = true;
			changeTurn();
		}
		
		public String description() {
			return (turn.getName() + "'s turn: " + "Pokémon " + turn.getCurrent().getName() 
					+ " attacks (" + turnChosenAttack.getName() + ")!"
					+ " " + noTurn.getName() + "'s Pokémon " + noTurn.getCurrent().getName()
					+ " actual HP: " + noTurn.getCurrent().getHp() + "."
					+ (dead ? " Pokémon " + noTurn.getCurrent().getName() + "" : ""));
		}
	}
	
	private class ChangeCurrentPokemon extends Event {
		private Pokemon newCurrent, previous;
		
		public ChangeCurrentPokemon(long eventTime, Pokemon nc) {
			super(eventTime);
			previous = turn.getCurrent();
			newCurrent = nc;
		}
		
		public void action() {
			turn.setCurrent(newCurrent);
			changeTurn();
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
			int actualPokHP = turn.getCurrent().getHp();
			turn.getCurrent().setHp(actualPokHP + item.getHpCure());
			item.takeOff();
			if (item.getQuantity() == 0)
				item = null;
			changeTurn();
		}
		
		public String description() {
			return (turn.getName() + "'s turn: Pokémon " + turn.getCurrent().getName() + " earned " + 
					item.getHpCure() + " HP points.");
		}
	}
	
	private class RunAway extends Event {
		public RunAway(long eventTime) {
			super(eventTime);
		}
		
		public void action() {
			
		}
		
		public String description() {
			return (turn.getName() + "'s turn: He has fled of the battle... Player " +
					noTurn.getName() + " has won!!! :-)");
		}
	}
	
	private class StartBattle extends Event {
		private Pokemon turnPokCurrent;
		private Pokemon noTurnPokCurrent;
		private int p1OrderAtt = 0, p2OrderAtt = 0;
		private int p1OrderPok = 0, p2OrderPok = 0;
		private boolean finished = false;
		
		public StartBattle(long eventTime) {
			super(eventTime);
		}
		
		public void action() {
			while (!finished) {
				turnPokCurrent = turn.getCurrent();
				noTurnPokCurrent = noTurn.getCurrent();
				
				if (turnPokCurrent.getHp() > 10) {
					int orderAttack = -1;
					if (turn == player1) {
						orderAttack = p1OrderAtt;
						p1OrderAtt = (p1OrderAtt + 1) % turnPokCurrent.getAttacks().length;
					} else {
						orderAttack = p2OrderAtt;
						p2OrderAtt = (p2OrderAtt + 1) % turnPokCurrent.getAttacks().length;
					}
					addEvent(new Battle.AttackWithCurrent(
							System.currentTimeMillis() + 500,
							turnPokCurrent.getAttacks()[orderAttack]));
				} else {
					if (turnPokCurrent.getHp() == 0) {
						if (turn == player1) {
							addEvent(new Battle.ChangeCurrentPokemon(
									System.currentTimeMillis() + 500,
									player1.getPokemons()[p1OrderPok]));
							
						}
					}
				}
			}
		}
		
		public String description() {
			return "The battle has started!";
		}
	}
	
	private void changeTurn() {
		if (turn == player1) {
			turn = player2;
			noTurn = player1; 
		} else {
			turn = player1;
			noTurn = player2; 
		}
	}
	
	public static void main(String[] args) {
	}
}
