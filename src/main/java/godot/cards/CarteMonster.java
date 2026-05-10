package godot.cards;

import godot.CardData;
import godot.api.Texture2D;

public class CarteMonster extends BaseCarte {
	private final CardData data;
	private final int attack;
	private final int defense;
	private final String monsterType;
	private final String effect;

	public CarteMonster(int id, String name, int cost, String description, Texture2D image, String imagePath,
			int attack, int defense, String monsterType, String effect, CardData data) {
		super(id, name, cost, description, image, imagePath, "MONSTER");
		this.data = data;
		this.attack = attack;
		this.defense = defense;
		this.monsterType = monsterType;
		this.effect = effect;
	}

	public String getMonsterType() {
		return data != null ? data.monster_type : "";
	}

	public int getDefense() {
		return data != null ? data.defense : 0;
	}

	public int getAttack() {
		return data != null ? data.atk : 0;
	}

	public String getEffect() {
		return effect;
	}
}
