package pokemon_battle;

import utils.Event;

public class Player {
	private String name;
	private Pokemon[] pokemons = new Pokemon[6];
	private Pokemon current;
	private Item[] items;
	
	public Player(String name, Pokemon[] pokemons, Item[] items) {
		this.name = name;
		this.pokemons = pokemons;
		this.current = this.pokemons[0];
		this.items = items;
	}

	private class AttackWithCurrent extends Event {
		private Attack chosenAttack;
		private Player rival;
		
		public AttackWithCurrent(long eventTime, Attack attack, Player riv) {
			super(eventTime);
			chosenAttack = attack;
			rival = riv;
		}
		
		public void action() {
			int actualRivalPokHP = rival.getCurrent().getHp();
			int attackDan = chosenAttack.getDan();
			rival.getCurrent().setHp(actualRivalPokHP - attackDan);
		}
		
		public String description() {
			return (name + "'s turn: Pokémon " + current.getName() + " attack " + 
					chosenAttack.getType().name() + "!\n"
				    + "Pokémon " + rival.getCurrent().getName() + "'s HP: " +
				    rival.getCurrent().getHp());
		}
	}
	
	private class ChangeCurrentPokemon extends Event {
		private Pokemon newCurrent, previous;
		
		public ChangeCurrentPokemon(long eventTime, Pokemon nc) {
			super(eventTime);
			previous = current;
			newCurrent = nc;
		}
		
		public void action() {
			current = newCurrent;
		}
		
		public String description() {
			return (name + "'s turn: Pokémon " + previous.getName() + " changed to " + 
					current.getName());
		}
	}
	
	private class UseItem extends Event {
		private Item item;
		
		public UseItem(long eventTime, Item item) {
			super(eventTime);
			this.item = item;
		}
		
		public void action() {
			int actualPokHP = current.getHp();
			current.setHp(actualPokHP + item.getHpCure());
			item.takeOff();
			if (item.getQuantity() == 0)
				item = null;
		}
		
		public String description() {
			return (name + "'s turn: Pokémon " + current.getName() + " earn " + item.getHpCure() +
					" HP points.");
		}
	}
	
	private class RunAway extends Event {
		private Player rival;
		
		public RunAway(long eventTime, Player riv) {
			super(eventTime);
			rival = riv;
		}
		
		public void action() {
			
		}
		
		public String description() {
			return (name + "'s turn: Pokémon ");
		}
	}

	public String getName() {
		return name;
	}

	public Pokemon[] getPokemons() {
		return pokemons;
	}

	public Pokemon getCurrent() {
		return current;
	}

	public void setCurrent(Pokemon current) {
		this.current = current;
	}

	public Item[] getItems() {
		return items;
	}
	
}
