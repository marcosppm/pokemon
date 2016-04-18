package pokemon_battle;

public class Pokemon {
	private String name;
	private int hp;
	private Attack[] attacks = new Attack[4];
	private Attack attCurrent;
	
	private Pokemon(String name, int hp, Attack[] attacks) {
		super();
		this.name = name;
		this.hp = hp;
		this.attacks = attacks;
		this.attCurrent = this.attacks[0];
	}

	public String getName() {
		return name;
	}

	public int getHp() {
		return hp;
	}
	
	public boolean isAlive() {
		return (hp > 0);
	}

	public void setHp(int hp) {
		if (hp < 0) {
			this.hp = 0;
		} else {
			this.hp = hp;
		}
	}

	public Attack[] getAttacks() {
		return attacks;
	}

	public Attack getAttCurrent() {
		return attCurrent;
	}

	public void setAttCurrent(Attack attCurrent) {
		this.attCurrent = attCurrent;
	}

}
