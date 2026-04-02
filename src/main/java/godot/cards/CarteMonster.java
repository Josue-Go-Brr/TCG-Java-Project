package godot.cards;

public class CarteMonster extends BaseCarte{
	private int attack;
	private int defense;
	private String effect;

	public CarteMonster(int id, String name, int cost, String description, String imagePath,
					   int attack, int defense, String effect) {
		super(id, name, cost, description, imagePath, "MONSTER");
		this.attack = attack;
		this.defense = defense;
		this.effect = effect;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public String getEffect() {
		return effect;
	}
}
