package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Resource;
import godot.api.Texture2D;

@RegisterClass
public class CardData extends Resource{

	@Export
	@RegisterProperty
	public String id = "";

	@Export
	@RegisterProperty
	public String name = "";

	@Export
	@RegisterProperty
	public int alphabeticalId;

	@Export @RegisterProperty
	public String type = "MONSTER";// MONSTER, MAGIE, TRAP

	@Export @RegisterProperty
	public int cost = 0;

	@Export
	@RegisterProperty
	public int atk;

	@Export
	@RegisterProperty
	public int defense;

	@Export
	@RegisterProperty
	public String description = "";

	@Export
	@RegisterProperty
	public Texture2D image;

	@Export @RegisterProperty
	public String imagePath = ""; 

}
