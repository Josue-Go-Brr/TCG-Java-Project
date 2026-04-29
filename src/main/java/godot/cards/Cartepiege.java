package godot.cards;

import godot.api.Texture2D;

public class Cartepiege extends BaseCarte {
	private String effect;

	public Cartepiege(int id, String name, int cost, String description, Texture2D image, String imagePath, String effect) {
		super(id, name, cost, description, image, imagePath, "TRAP");
		this.effect = effect;
	}

	public String getEffect() {
		return effect;
	}
}
