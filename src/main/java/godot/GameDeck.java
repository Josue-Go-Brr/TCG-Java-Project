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
	public String id = "";
	public List<String> player_deck = new ArrayList<String>();
	public int cardcompteur = -1;
	public int random;

	//Calling property
	public Node player_hand_ref;
	public Node cardManager;


	@RegisterFunction
	@Override
	public void _ready(){

		player_hand_ref = getNode("../PlayerHand");
		cardManager = getNode("../Cardmanager");

		for (int i = 0; i < 10; i++) {
			player_deck.add("Dark_Blade");
			player_deck.add("Dark_Magician_Girl");
			player_deck.add("Blue_Eyes_White_Dragon");
			player_deck.add("Slifer_The_Sky_Dragon");
		}


	}


	@RegisterFunction
	public void draw_card() {

		if (!player_deck.isEmpty()) {

			//I get the first card
			id = player_deck.get(0);

			//remove the card from the deck
			player_deck.remove(0);

			//getting the right scene
			PackedScene instance = GD.load("res://scene/card.tscn");

			//Getting the right Node to make it work with my card functions
			Node CardManagerPath = getNode("../Cardmanager");

			// And then make a card instance
			Node Card = instance.instantiate();

			//getting my instance under CardManager
			CardManagerPath.addChild(Card);

			//Since you cannot modify a string that you initiate at the start of the Card script
			//I made the name of the node my String id, and set the variable Cardid to the name in the updatecard function
			Card.setName(id);


			//calling the update card fonction to modify the card
			Card.call("updatecard");

			//Card.getNode("Area2D").set("collision_mask", 1);

			//I get this compteur to the other file juste in case
			cardcompteur += 1;
			player_hand_ref.set("cardcompteur", cardcompteur);

			//to call addtohand from PlayerHand
			player_hand_ref.call("drawing");
		}

	}
}
