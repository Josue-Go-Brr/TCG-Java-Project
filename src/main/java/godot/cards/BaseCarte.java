package godot.cards;

import godot.api.Texture2D;

public class BaseCarte {
	private int id;
	private String name;
	private int cost;
	private String description;
	private Texture2D image;
	private String imagePath;
	private String type;

	public BaseCarte(int id, String name, int cost, String description, Texture2D image, String imagePath, String type) {
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.image = image;
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

	public Texture2D getImage() {
		return image;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getType() {
		return type;
	}
}
