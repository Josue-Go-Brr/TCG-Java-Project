package godot.cards;

import godot.api.Texture2D;

public class CarteMonster extends BaseCarte{
	private int attack;
	private int defense;
	private String effect;

	public CarteMonster(int id, String name, int cost, String description, Texture2D image, String imagePath,
					   int attack, int defense, String effect) {
		super(id, name, cost, description, image, imagePath, "MONSTER");
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
