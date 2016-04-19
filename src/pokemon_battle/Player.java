package pokemon_battle;

public class Player {
	private String name;
	
	private Pokemon[] pokemons;
	private Pokemon pokCurrent;
	private int pokOrder;
	
	private Item[] items;
	private Item itemCurrent;
	private int itemOrder;
	
	public Player(String name, Pokemon[] pokemons, Item[] items) {
		this.name = name;
		this.pokemons = pokemons;
		this.pokCurrent = this.pokemons[0];
		this.pokOrder = 0;
		this.items = items;
		this.itemCurrent = this.items[0];
		this.itemOrder = 0;
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
	
	public boolean hasItens() {
		boolean hasItens = false;
		
		for (Item it : this.items) {
			if (it.getQuantity() > 0) {
				hasItens = true;
				break;
			}
		}
		
		return hasItens;
	}

	public Item getItemCurrent() {
		return itemCurrent;
	}

	public void setItemCurrent(Item itemCurrent) {
		this.itemCurrent = itemCurrent;
	}

	public int getPokOrder() {
		return pokOrder;
	}

	public void setPokOrder(int pokOrder) {
		this.pokOrder = pokOrder;
	}

	public int getItemOrder() {
		return itemOrder;
	}

	public void setItemOrder(int itemOrder) {
		this.itemOrder = itemOrder;
	}
	
}
