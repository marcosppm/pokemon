package pokemon_battle;

public class Attack {
	private TypeAttack type;
	private int dan;
	
	public Attack(TypeAttack type, int dan) {
		this.type = type;
		this.dan = dan;
	}

	public TypeAttack getType() {
		return type;
	}
	
	public int getDan() {
		return dan;
	}

}
