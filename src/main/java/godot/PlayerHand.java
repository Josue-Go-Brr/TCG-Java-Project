package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Node2D;
import godot.api.Resource;
import godot.global.GD;


@RegisterClass
public class PlayerHand extends Node2D {

	@Export
	@RegisterProperty
	public int HAND_COUNT = 2;
	String CARD_SCENE_PATH = "res://scene/card.tscn/";
	//Resource card_scene = GD.load(CARD_SCENE_PATH);

	@RegisterFunction
	@Override
	public void _ready(){
		for (int i = 0; i < HAND_COUNT; i++) {
			GD.INSTANCE.print();
		}
	}
}
