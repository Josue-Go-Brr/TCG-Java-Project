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
import java.util.Objects;


@RegisterClass
public class EnemyHand extends Node2D {

	@Export
	@RegisterProperty

	//Variable used to place enemy card on the battlefield
	public int cost;
	public int bettercost = 0;
	public int bettercostindex;
	public Node bestcard;
	public Node2D chosen_slot;

	//Attack
	List<Node> enemy_field = new ArrayList<>();
	List<Node> player_field = new ArrayList<>();
	public boolean playerfieldempty = true;
	public boolean enemyfieldempty = true;
	public List<Node2D> empty_slots = new ArrayList<Node2D>();
	public List<Node2D> player_empty_slots = new ArrayList<Node2D>();
	public List<Node2D> player_slots = new ArrayList<Node2D>();
	public List<Node2D> enemy_slots = new ArrayList<Node2D>();
	public int compteur = 0;

	//Timer to make attack less often
	public Timer timer;


	//References
	public Node game_deck_ref;
	public Node cardManager;

	//Hand variables
	public int HAND_SIZE;
	List<Node> enemy_hand = new ArrayList<>();
	List<Integer> cost_hand = new ArrayList<>();


	//Variables to position cards in hand
	int CARD_WIDTH = 130;
	int HAND_Y_POSITION = 190;
	public double center_screen_x;
	public double center_screen_y;

	public Node drawingcard;

	@RegisterFunction
	@Override
	public void _ready() {
		cardManager = getNode("../Cardmanager");
		game_deck_ref = getNode("../OpponentDeck");

		// Sah quel enfer, getViewport.get("size") return un objet non convertible, obligé de bidouller avec un rect
		//Getting the center screen coordinates for later
		center_screen_x = getViewportRect().getEnd().getX() / 2;
		center_screen_y = getViewportRect().getEnd().getY() / 2;

		//I'm loading the black magician
		PackedScene instance = GD.load("res://scene/card.tscn");

		empty_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot"));
		empty_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot2"));
		empty_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot3"));
		empty_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot4"));
		empty_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot5"));

		player_slots.add((Node2D) getNode("../Slots/CardSlot1"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot2"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot3"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot4"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot5"));

		enemy_slots = empty_slots;
		player_empty_slots = player_slots;

		timer = (Timer) getNode("../AttackTimer");
		timer.oneShotProperty(true);
		timer.waitTimeProperty(0.5);

		timer.connect("timeout",
				Callable.create(this, StringNames.toGodotName("_on_timer_timeout")),
				0);
	}


	@RegisterFunction
	public void add_card_to_hand(Node card) {
		if (!enemy_hand.contains(card)) {
			enemy_hand.add(0, card);
			HAND_SIZE = enemy_hand.size();

			card.call("cost");
			cost_hand.add(0, cost);
			//GD.INSTANCE.print(cost);

			update_hand_position();
		} else {
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
				enemy_field.add(enemy_hand.get(i));
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

		bestcard = null;
		bettercost = 0;

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

			//not enough updated
			//enemy_field.add(bestcard);
		}
		//GD.INSTANCE.print("empty slots " + empty_slots);
		quoi();

	}

	@RegisterFunction
	public void attack() {
		//Updating cards slot occupied in playerfield, Enemyfield is updated when removecardfromhand is updated
		update_player_field();

		//Enemy is attacking with each card on his terrain
		compteur = 0;
		update_player_field();
		timer.start();
	}


	//This function take argument the card that will attack and select one in opposing field
	@RegisterFunction
	public void card_attack(Node card) {

		if (playerfieldempty && !enemy_field.isEmpty()) {
			GD.INSTANCE.print("Direct attack! " + card);
			return;
		}

		// take a random card slot in player field a find the card there by comparing to the coordinate of the card slot
		int random;
		random = GD.INSTANCE.randiRange(0, player_field.size() - 1);

		GD.INSTANCE.print("enemy_field " + enemy_field);
		GD.INSTANCE.print("player_field " + player_field);

		//Searching for the card in the right coordinates
		for (int i = 0; i < cardManager.getChildCount(); i++) {
			// On parcourt tout les enfants de card Manager en regardant si l'un d'entre eux a la bonne position
			if (cardManager.getChild(i).get("position").equals(player_field.get(random).get("position"))) {
				GD.INSTANCE.print("ATTACK card: " + card + "is attacking " + cardManager.getChild(i));

				//I will set an "incomingattack" variable in each card and compare it when loading the lables
				cardManager.getChild(i).set("incoming_atk", (card.get("atk")));
			}
		}
	}

	@RegisterFunction
	public void update_player_field() {

		//Updating cards slot occupied in playerfield
		for (int i = 0; i < player_slots.size(); i++) {
			if (player_slots.get(i).get("card_in_slot").equals(true)) {
				if (!player_field.contains(player_slots.get(i))) {
					player_field.add(player_slots.get(i));
				}
				playerfieldempty = false;
			}
		}

		for (int j = 0; j < player_field.size(); j++) {
			if (player_field.get(j).get("card_in_slot").equals(false)) {
				player_field.remove(j);
			}
		}

		if (player_field.isEmpty()) {
			playerfieldempty = true;
		}

	}

	@RegisterFunction
	public void _on_timer_timeout() {
			if (compteur < enemy_field.size()) {
				update_player_field();
				card_attack(enemy_field.get(compteur));
				timer.start();
			}
			compteur+=1;
	}
}





