package godot.cards;

import godot.api.Texture2D;

public class CarteMagie extends BaseCarte {
	private String effect;

	public CarteMagie(int id, String name, int cost, String description, Texture2D image, String imagePath, String effect) {
		super(id, name, cost, description, image, imagePath, "MAGIE");
		this.effect = effect;
	}

	public String getEffect() {
		return effect;
	}
}
