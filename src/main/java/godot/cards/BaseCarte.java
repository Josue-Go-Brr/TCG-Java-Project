package godot.cards;

public class BaseCarte {
	private int id;
	private String name;
	private int cost;
	private String description;
	private String imagePath;
	private String type;

	public BaseCarte(int id, String name, int cost, String description, String imagePath, String type) {
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.imagePath = imagePath;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getType() {
		return type;
	}
}
