package godot.cards;

public class CarteMagie extends BaseCarte {
	private String effect;

	public CarteMagie(int id, String name, int cost, String description, String imagePath, String effect) {
		super(id, name, cost, description, imagePath, "MAGIE");
		this.effect = effect;
	}

	public String getEffect() {
		return effect;
	}
}
