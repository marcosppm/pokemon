package pokemon_battle;

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
