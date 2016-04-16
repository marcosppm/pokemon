package pokemon_battle;

public class Attack {
	private String name;
	private TypeAttack type;
	private int dan;
	private int priority;
	
	public Attack(String name, TypeAttack type, int dan, int priority) {
		this.name = name;
		this.type = type;
		this.dan = dan;
		this.priority = priority;
	}
	
	public String getName() {
		return name;
	}

	public TypeAttack getType() {
		return type;
	}
	
	public int getDan() {
		return dan;
	}

	public int getPriority() {
		return priority;
	}

}
