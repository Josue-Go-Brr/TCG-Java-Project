package godot;
import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.api.Object;
import godot.core.*;
import godot.global.GD;

import javax.swing.plaf.metal.MetalIconFactory;
import java.util.ArrayList;
import java.util.List;



@RegisterClass
public class EnemyHand extends Node2D {

	@Export
	@RegisterProperty

	public int cost;
	public int bettercost = 0;
	public int bettercostindex;
	public Node bestcard;
	public Node2D chosen_slot;
	//public int random;

	public Node game_deck_ref;
	public Node cardManager;

	public int HAND_SIZE;
	List<Node> enemy_hand = new ArrayList<>();
	List<Integer> cost_hand = new ArrayList<>();
	public List<Node2D> empty_slots = new ArrayList<Node2D>();

	int CARD_WIDTH = 130;
	int HAND_Y_POSITION = 190;
	public double center_screen_x;
	public double center_screen_y;

	public Node drawingcard;

	@RegisterFunction
	@Override
	public void _ready(){
		cardManager = getNode("../Cardmanager");
		game_deck_ref = getNode("../OpponentDeck");

		// Sah quel enfer, getViewport.get("size") return un objet non convertible, obligé de bidouller avec un rect
		//Getting the center screen coordinates for later
		center_screen_x = getViewportRect().getEnd().getX() / 2;
		center_screen_y = getViewportRect().getEnd().getY() / 2;

		//I'm loading the black magician
		PackedScene instance = GD.load("res://scene/card.tscn");

		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot2"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot3"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot4"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot5"));

	}





	@RegisterFunction
	public void add_card_to_hand(Node card) {
		if (!enemy_hand.contains(card)) {
			enemy_hand.add(0, card);
			HAND_SIZE = enemy_hand.size();

			card.call("cost");
			cost_hand.add(0, cost);
			GD.INSTANCE.print(cost);

			update_hand_position();
		}

		else {
			//In the card script you have a starting position that you should update, I ignored it
			//GD.INSTANCE.print(card.get("starting_pos"));
			//animate_card_to_position(card, (Vector2) card.get("starting_pos"));
		}

	}

	@RegisterFunction
	public void update_hand_position() {
		for (int i = 0; i < enemy_hand.size(); i++) {
			Vector2 new_position = new Vector2(calculate_card_position(i), HAND_Y_POSITION);
			Node card = enemy_hand.get(i);

			//this is used to snap the card back into the hand when dropped
			card.set("starting_pos", new_position);

			//same thing but when created
			animate_card_to_position(card, new_position);
		}
	}

	@RegisterFunction
	public int calculate_card_position(int index) {
		// this sets the total hand space
		double total_width = (enemy_hand.size() - 1) * CARD_WIDTH;
		// this is there to make cards side to side
		double x_offset = center_screen_x + index * CARD_WIDTH - total_width / 2;
		return (int) x_offset;
	}

	@RegisterFunction
	public void animate_card_to_position(Node Card, Vector2 new_position) {
		Tween replace = getTree().createTween();
		replace.tweenProperty(Card, "position", new_position, 0.2);
	}

	@RegisterFunction
	public void remove_card_from_hand() {
		for (int i = 0; i < enemy_hand.size(); i++) {

			if (enemy_hand.get(i).get("in_slot").equals(true)) {
				//GD.INSTANCE.print(player_hand.get(i).get("Starting_pos"));
				enemy_hand.remove(i);
			}
				//GD.INSTANCE.print(player_hand);
			}

	}
	
	
	//cette fonction ne sert qu'a être appelée grâce .call pour exécuter d'autres trucs 
	@RegisterFunction
	public void quoi() {
		//GD.INSTANCE.print("feur");
		remove_card_from_hand();
		//keep this here and not inside removecard func if you don't want it to explode
		update_hand_position();
	}

	@RegisterFunction
	public void drawing() {
		drawingcard = cardManager.getChild(-1);

		//I just get the last card instanciated in card manager
		add_card_to_hand(drawingcard);

		}



		//Known issue, when drawing a high crystal card he won't place it
	@RegisterFunction
	public void highest_card() {


		for (int i = 0; i < cost_hand.size(); i++) {
			if (cost_hand.get(i) > bettercost) {
				bettercost = cost_hand.get(i);
				bettercostindex = i;
			}
		}

		bestcard = enemy_hand.get(bettercostindex);

		if (!empty_slots.isEmpty()) {
			int random;
			random = GD.INSTANCE.randiRange(0, empty_slots.size() - 1);
			chosen_slot = empty_slots.get(random);
			empty_slots.remove(random);
			Tween tween1 = getTree().createTween();
			tween1.tweenProperty(bestcard, "position", chosen_slot.get("position"), 0.2);

			Tween tween2 = getTree().createTween();
			tween2.tweenProperty(bestcard, "scale", 0.65, 0.2);
			bestcard.set("in_slot", true);

			AnimationPlayer anim = (AnimationPlayer) bestcard.getNode("AnimationPlayer");
			anim.play("card_flip");

			cost_hand.remove(bettercostindex);

		}
		GD.INSTANCE.print(empty_slots);
		quoi();

	}
}




