package pokemon_battle;

public class Pokemon {
	private String name;
	private int hp;
	private Attack[] attacks = new Attack[4];
	
	public Pokemon(String name, int hp) {
		this.name = name;
		this.hp = hp;
	}

	public String getName() {
		return name;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public Attack[] getAttacks() {
		return attacks;
	}

	public void setAttacks(Attack[] attacks) {
		this.attacks = attacks;
	}

}
