package godot.cards;

public class Cartepiege extends BaseCarte {
	private String effect;

	public Cartepiege(int id, String name, int cost, String description, String imagePath, String effect) {
		super(id, name, cost, description, imagePath, "TRAP");
		this.effect = effect;
	}

	public String getEffect() {
		return effect;
	}
}
