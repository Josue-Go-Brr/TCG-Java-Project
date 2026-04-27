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

@RegisterClass
public class Card extends Node2D {


	@RegisterProperty @Export public String cardID = " ";
	@RegisterProperty public int atk;
	@RegisterProperty public int defense;


	@RegisterFunction
	@Override
	public void _ready(){
		// Récuperer l'atk et la defense depuis la BDD
		atk = 0;
		defense = 0;
	}

	@RegisterFunction
	public String getCardID(){
		return cardID;
	}

	@RegisterFunction
	public void _on_area_2d_mouse_entered() {
		setScale(new Vector2(1.05, 1.05));
		this.setZIndex(2);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_exited() {
		setScale(new Vector2(1, 1));
		this.setZIndex(1);
	}

}
