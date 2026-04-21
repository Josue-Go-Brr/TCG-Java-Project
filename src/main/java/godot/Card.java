package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.core.Dictionary;
import godot.core.Signal;
import godot.core.VariantArray;
import godot.global.GD;

import java.lang.Object;

@RegisterClass
public class Card extends Node2D {

	@Export
	@RegisterProperty
	public Signal hovered;
	public Signal hovered_off;

	@RegisterFunction
	@Override
	public void _ready(){
		// Toutes les cartes doivent être enfant de cardManager ou ça va crash

		Card carte = this;
		//getParent().connect_card_signals(carte);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_entered() {
		GD.INSTANCE.print("Hover ON");
		emitSignal("hovered", this);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_exited() {
		GD.INSTANCE.print("Hover OFF");
		emitSignal("hovered_off", this);
	}

}
