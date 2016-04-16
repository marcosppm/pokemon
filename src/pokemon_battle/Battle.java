package pokemon_battle;

import utils.Controller;
import utils.Event;

public class Battle extends Controller {
	private Player player1, player2; // player1 always starts the game
	private Player turn, noTurn;
	
	private Battle(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		this.turn = this.player1;
		this.noTurn = this.player2;
	}

	private class AttackWithCurrent extends Event {
		private Attack chosenAttack;
		
		public AttackWithCurrent(long eventTime, Attack attack) {
			super(eventTime);
			chosenAttack = attack;
		}
		
		public void action() {
			int actualRivalPokHP = noTurn.getCurrent().getHp();
			int attackDan = chosenAttack.getDan();
			noTurn.getCurrent().setHp(actualRivalPokHP - attackDan);
		}
		
		public String description() {
			return ;
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
		public StartBattle(long eventTime) {
			super(eventTime);
		}
		
		public void action() {
			
		}
		
		public String description() {
			return "The battle has started!";
		}
	}
	
	public static void main(String[] args) {
	}
}
