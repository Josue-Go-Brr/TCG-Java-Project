package godot;
import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Node;
import godot.api.Node2D;
import godot.global.GD;
import godot.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@RegisterClass
public class GameDeckEnemy extends Node2D {
	@Export
	@RegisterProperty

	//see the enemy cards
	public boolean debugmode = false;

	public boolean card_drawn = false;
	public boolean starting_hand = false;

	public String id = "";
	@Export public List<String> enemy_deck = new ArrayList<String>();
	@Export public Node bestcard;
	public int cardcompteur = -1;
	public int random;


	//Calling property
	public Node enemy_hand_ref;
	public Node cardManager;


	@RegisterFunction
	@Override
	public void _ready() {

		enemy_hand_ref = getNode("../EnemyHand");
		cardManager = getNode("../Cardmanager");


		for (int i = 0; i < 10; i++) {
			enemy_deck.add("Dark_Blade");
			enemy_deck.add("Dark_Magician_Girl");
			enemy_deck.add("Blue_Eyes_White_Dragon");

		}

		for (int i = 0; i < 5; i++) {
			draw_card();
		}

		// Label for deck size Useless for enemy deck
		//this.getNode("CardNumber").set("text", String.valueOf(player_deck.size()));
	}


	@RegisterFunction
	public void draw_card() {
		if (card_drawn) {
			return;
		}
			if (!enemy_deck.isEmpty()) {

				random = GD.INSTANCE.randiRange(0, enemy_deck.size() - 1);
				//I get the first card, you can make an random number here
				id = enemy_deck.get(random);

				//remove the card from the deck
				enemy_deck.remove(random);
				this.getNode("Deckopp/CardNumber").set("text", String.valueOf(enemy_deck.size()));

				//getting the right scene
				PackedScene instance = GD.load("res://scene/cardEnemy.tscn");

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
				enemy_hand_ref.set("cardcompteur", cardcompteur);


				//Card.set("position", this.get("position"));
				if (debugmode) {
					AnimationPlayer anim = (AnimationPlayer) Card.getNode("AnimationPlayer");
					anim.play("card_flip");
				}



				//to call addtohand from PlayerHand
				enemy_hand_ref.call("drawing");


			}
		}
	}
