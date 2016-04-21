package pokemon_battle;

public class Item {
	private String name;
	private int hpCure;
	private int quantity;
	
	public Item(String name, int hpCure, int quantity) {
		this.name = name;
		this.hpCure = hpCure;
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getHpCure() {
		return hpCure;
	}
	
	public void setHpCure(int hpCure) {
		this.hpCure = hpCure;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void takeOff() {
		this.quantity--;
	}
}

