package godot.cards;

import godot.api.Texture2D;

public class CarteMonster extends BaseCarte{
	private int attack;
	private int defense;
	private String monsterType;
	private String effect;

	public CarteMonster(int id, String name, int cost, String description, Texture2D image, String imagePath,
					   int attack, int defense, String monsterType, String effect) {
		super(id, name, cost, description, image, imagePath, "MONSTER");
		this.attack = attack;
		this.defense = defense;
		this.monsterType = monsterType;
		this.effect = effect;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public String getMonsterType() {
		return monsterType;
	}

	public String getEffect() {
		return effect;
	}
}
