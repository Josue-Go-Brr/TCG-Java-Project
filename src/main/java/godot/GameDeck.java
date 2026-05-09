package godot;
import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Node;
import godot.api.Node2D;
import godot.global.GD;
import godot.api.*;
import godot.core.*;

import java.util.ArrayList;
import java.util.List;



@RegisterClass
public class GameDeck extends Node2D {
	@Export
	@RegisterProperty

	public Node Forwardcard;
	public List<Integer> player_deck = new ArrayList<Integer>();
	public Node player_hand_ref;
	public Node cardManager;

	public int cardcompteur = -1;

	@RegisterFunction
	@Override
	public void _ready(){
		player_hand_ref = getNode("../PlayerHand");
		cardManager = getNode("../Cardmanager");

		player_deck.add(1);
		player_deck.add(2);
		player_deck.add(3);
	}

	@RegisterFunction
	public void draw_card() {

		PackedScene instance = GD.load("res://scene/card.tscn");
		//Getting the right Node to make it work with my card functions
		Node CardManagerPath = getNode("../Cardmanager");

		// And then make a card instance
		Node Card = instance.instantiate();
		CardManagerPath.addChild(Card);
		Card.setName("card");

		//get the name instead in a string and find it back
		//player_hand_ref.set("drawingcard", Card);


		//I get this compteur to the other file juste in case
		cardcompteur += 1;
		player_hand_ref.set("cardcompteur", cardcompteur);

		//to call add to deck from PlayerHand
		player_hand_ref.call("drawing");
	}
}
