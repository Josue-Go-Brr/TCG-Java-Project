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

	//Variable used to place enemy card on the battlefield
	public int cost;


	//Attack
	public Node selectedcard;
	public Node selectedcardslot;

	List<Node> enemy_field = new ArrayList<>();
	List<Node> player_field = new ArrayList<>();

	public boolean playerfieldempty = true;
	public boolean enemyfieldempty = true;

	public List<Node2D> player_slots = new ArrayList<Node2D>();
	public List<Node2D> enemy_slots = new ArrayList<Node2D>();


	//Timer to make attack less often
	public Timer timer;

	public int cardcompteur;
	public Node game_deck_ref;
	public Node cardManager;

	public int HAND_SIZE;
	List<Node> player_hand = new ArrayList<>();

	int CARD_WIDTH = 130;
	int HAND_Y_POSITION = 890;
	public double center_screen_x;
	public double center_screen_y;

	public Node drawingcard;





	@RegisterFunction
	@Override
	public void _ready(){
		cardManager = getNode("../Cardmanager");
		game_deck_ref = getNode("../Deck");

		// Sah quel enfer, getViewport.get("size") return un objet non convertible, obligé de bidouller avec un rect
		//Getting the center screen coordinates for later
		center_screen_x = getViewportRect().getEnd().getX() / 2;
		center_screen_y = getViewportRect().getEnd().getY() / 2;

		//I'm loading the black magician
		PackedScene instance = GD.load("res://scene/card.tscn");

		enemy_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot"));
		enemy_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot2"));
		enemy_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot3"));
		enemy_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot4"));
		enemy_slots.add((Node2D) getNode("../SlotsEnemy/EnemyCardSlot5"));

		player_slots.add((Node2D) getNode("../Slots/CardSlot1"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot2"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot3"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot4"));
		player_slots.add((Node2D) getNode("../Slots/CardSlot5"));


//		for (int i = 0; i < HAND_COUNT; i++) {


//			//Getting the right Node to make it work with my card functions
//			Node CardManagerPath = getNode("../Cardmanager");
//
//			// And then make a card instance
//			Node MagicCard = instance.instantiate();
//			CardManagerPath.addChild(MagicCard);
//
//			MagicCard.setName("card");
//			drawing_card = MagicCard;
//			add_card_to_hand(MagicCard);
//


//		}

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

//	@RegisterFunction
//	@Override
//	public void _input(InputEvent event) {
//
//		if (event instanceof InputEventMouseButton mouseEvent && mouseEvent.getButtonIndex() == MouseButton.RIGHT) {
//			// Listener du Clique Droit
//			if (mouseEvent.isPressed()) {
//				update_hand_position();
//			}
//		}
//	}




	@RegisterFunction
	public void add_card_to_hand(Node card) {
		if (!player_hand.contains(card)) {
			player_hand.add(0, card);
			HAND_SIZE = player_hand.size();
			//GD.INSTANCE.print("added!");
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
		for (int i = 0; i < player_hand.size(); i++) {
			Vector2 new_position = new Vector2(calculate_card_position(i), HAND_Y_POSITION);
			Node card = player_hand.get(i);

			//this is used to snap the card back into the hand when dropped
			card.set("starting_pos", new_position);

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
		replace.tweenProperty(Card, "position", new_position, 0.2);
	}

	@RegisterFunction
	public void remove_card_from_hand() {
		for (int i = 0; i < player_hand.size(); i++) {

			if (player_hand.get(i).get("in_slot").equals(true)) {
				//GD.INSTANCE.print(player_hand.get(i).get("Starting_pos"));
				player_field.add(player_hand.get(i));
				player_hand.remove(i);

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

	@RegisterFunction
	public void attack() {
		update_enemy_field();

		//GD.INSTANCE.print(enemy_field);

		for (int i = 0; i < cardManager.getChildCount(); i++) {
			// On parcourt tout les enfants de card Manager en regardant si l'un d'entre eux a la bonne position
			if (cardManager.getChild(i).get("scale").equals(new Vector2(0.7, 0.7))) {
				selectedcard = cardManager.getChild(i);
			}
		}
			// Make the card attack, rescale normally and make it unable to attack again this turn
			if (getNode("../BattleManager").get("player_turn").equals(true)) {
				card_attack(selectedcard);
				selectedcard.set("selected", false);
				selectedcard.set("scale", new Vector2(0.6, 0.6));
				selectedcard.set("attacked_this_turn", true);
				cardManager.set("cardselected", false);
			}
		}



	//This function take argument the card that will attack and select one in opposing field
	@RegisterFunction
	public void card_attack(Node card) {

		// take a random card slot in player field a find the card there by comparing to the coordinate of the card slot
		//GD.INSTANCE.print("enemy_field " + enemy_field);
		//GD.INSTANCE.print("player_field " + player_field);

		//Searching for the card in the right coordinates
		for (int i = 0; i < cardManager.getChildCount(); i++) {
			// On parcourt tout les enfants de card Manager en regardant si l'un d'entre eux a la bonne position
			if (cardManager.getChild(i).get("target").equals(true)) {

				GD.INSTANCE.print("ATTACK card: " + card + "is attacking " + cardManager.getChild(i));

				//I will set an "incomingattack" variable in each card and compare it when loading the lables
				cardManager.getChild(i).set("incoming_atk", (card.get("atk")));
				alive();
			}
		}

	}

	@RegisterFunction
	public void update_enemy_field() {

		//Updating cards slot occupied in playerfield
		for (int i = 0; i < enemy_slots.size(); i++) {
			if (enemy_slots.get(i).get("card_in_slot").equals(true)) {
				if (!enemy_field.contains(enemy_slots.get(i))) {
					enemy_field.add(enemy_slots.get(i));
				}
				enemyfieldempty = false;
			}
		}

		for (int j = 0; j < enemy_field.size(); j++) {
			if (enemy_field.get(j).get("card_in_slot").equals(false)) {
				enemy_field.remove(j);
			}
		}

		if (enemy_field.isEmpty()) {
			enemyfieldempty = true;
		}
	}

	public void alive() {
		for (int i = 0; i < cardManager.getChildCount(); i++) {
			if (cardManager.getChild(i).get("isalive").equals(false)) {
				getNode("../Graveyard").addChild(cardManager.getChild(i));
				cardManager.removeChild(cardManager.getChild(i));
			}
		}
	}
}
