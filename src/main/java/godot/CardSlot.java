package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterProperty;
import godot.api.Node2D;


@RegisterClass
public class CardSlot extends Node2D {

	@Export
	@RegisterProperty
	public boolean card_in_slot = false;


}
