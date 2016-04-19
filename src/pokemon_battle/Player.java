package pokemon_battle;

public class Player {
	private String name;
	private Pokemon[] pokemons;
	private Pokemon pokCurrent;
	private Item[] items;
	private Item itemCurrent;
	
	public Player(String name, Pokemon[] pokemons, Item[] items) {
		this.name = name;
		this.pokemons = pokemons;
		this.pokCurrent = this.pokemons[0];
		this.items = items;
		this.itemCurrent = this.items[0];
	}

	public String getName() {
		return name;
	}

	public Pokemon[] getPokemons() {
		return pokemons;
	}

	public Pokemon getPokCurrent() {
		return pokCurrent;
	}

	public void setPokCurrent(Pokemon pokCurrent) {
		this.pokCurrent = pokCurrent;
	}

	public Item[] getItems() {
		return items;
	}

	public Item getItemCurrent() {
		return itemCurrent;
	}

	public void setItemCurrent(Item itemCurrent) {
		this.itemCurrent = itemCurrent;
	}
	
}
