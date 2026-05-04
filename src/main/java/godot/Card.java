package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.core.Dictionary;
import godot.core.Signal;
import godot.core.VariantArray;
import godot.core.Vector2;
import godot.global.GD;

import java.lang.Object;

//added signals for later

@RegisterClass
public class Card extends Node2D {

	@Export
	@RegisterProperty
	public boolean in_slot = false;
	public Vector2 starting_pos = new Vector2();
	public Vector2 hovered_off = new Vector2(0.6, 0.6);
	public Vector2 hovered_on = new Vector2(0.7, 0.7);


	@RegisterFunction
	@Override
	public void _ready(){
		//GD.INSTANCE.print(starting_pos);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_entered() {
		setScale(hovered_on);
		this.setZIndex(3);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_exited() {
		setScale(hovered_off);
		this.setZIndex(1);
	}
}
