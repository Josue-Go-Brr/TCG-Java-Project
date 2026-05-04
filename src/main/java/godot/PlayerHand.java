package godot;
import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.core.*;
import godot.global.GD;

import javax.swing.plaf.metal.MetalIconFactory;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;



@RegisterClass
public class PlayerHand extends Node2D {

	@Export
	@RegisterProperty
	public int HAND_COUNT = 5;
	List<Node> player_hand = new ArrayList<>();

	int CARD_WIDTH = 130;
	int HAND_Y_POSITION = 890;
	public double center_screen_x;
	public double center_screen_y;

	@RegisterFunction
	@Override
	public void _ready(){


		// Sah quel enfer, getViewport.get("size") return un objet non convertible, obligé de bidouller avec un rect
		//Getting the center screen coordinates for later
		center_screen_x = getViewportRect().getEnd().getX() / 2;
		center_screen_y = getViewportRect().getEnd().getY() / 2;

		//I'm loading the black magician
		PackedScene instance = GD.load("res://scene/card.tscn");

		for (int i = 0; i < HAND_COUNT; i++) {


			//Getting the right Node to make it work with my card functions
			Node CardManagerPath = getNode("../Cardmanager");

			// And then make a card instance
			Node MagicCard = instance.instantiate();
			CardManagerPath.addChild(MagicCard);

			MagicCard.setName("card");
			add_card_to_hand(MagicCard);



		}

	}

//	@RegisterFunction
//	@Override
//	public void _process(double delta) {
//		for (int i = 0; i < player_hand.size(); i++) {
//			if (player_hand.get(i).getNode("Card").get("flag").equals(true)) {
//				remove_card_from_hand(player_hand.get(i));
//			}
//		}
//	}

	@RegisterFunction
	@Override
	public void _input(InputEvent event) {

		if (event instanceof InputEventMouseButton mouseEvent && mouseEvent.getButtonIndex() == MouseButton.RIGHT) {
			// Listener du Clique Droit
			if (mouseEvent.isPressed()) {
				update_hand_position();
			}
		}
	}



	@RegisterFunction
	public void add_card_to_hand(Node card) {
		if (!player_hand.contains(card)) {
			player_hand.add(0, card);
			GD.INSTANCE.print("added!");
			update_hand_position();
		}

		else {
			//In the card script you have a starting position that you should update, I ignored it
			//GD.INSTANCE.print(card.get("starting_pos"));
			animate_card_to_position(card, new Vector2(0,0));
		}

	}

	@RegisterFunction
	public void update_hand_position() {
		for (int i = 0; i < player_hand.size(); i++) {
			Vector2 new_position = new Vector2(calculate_card_position(i), HAND_Y_POSITION);
			Node card = player_hand.get(i);

			//this is used to snap the card back into the hand when dropped
			card.set("starting_pos", new_position);
			//GD.INSTANCE.print(card.get("starting_pos"));

			//same thing but when created
			animate_card_to_position(card, new_position);
		}
	}

	@RegisterFunction
	public int calculate_card_position(int index) {
		// this sets the total hand space
		double total_width = (player_hand.size() - 1) * CARD_WIDTH;
		// this is there to make cards side to side
		double x_offset = center_screen_x + index * CARD_WIDTH - total_width / 2;
		return (int) x_offset;
	}

	@RegisterFunction
	public void animate_card_to_position(Node Card, Vector2 new_position) {
		Tween replace = getTree().createTween();
		replace.tweenProperty(Card, "position", new_position, 0.1);
	}

	@RegisterFunction
	public void remove_card_from_hand() {
		for (int i = 0; i < player_hand.size(); i++) {
			if (player_hand.get(i).get("in_slot").equals(true)) {
				player_hand.remove(i);
			}

				update_hand_position();
			}

	}

	@RegisterFunction
	public void quoi() {
		//CardManagerRef.get("cardDragged");
		GD.INSTANCE.print("feur");
		remove_card_from_hand();
		update_hand_position();

	}
}
