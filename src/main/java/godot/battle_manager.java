package godot;
import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.core.*;
import godot.global.GD;

import java.lang.Object;
import java.util.ArrayList;
import java.util.List;

@RegisterClass
public class battle_manager extends Node {

	@RegisterProperty
	@Export
	public Node EndTurnButton;
	public Node2D OppDeck;
	public Node2D PlayerDeck;
	public Timer timer;
	List<Node> enemy_hand = new ArrayList<>();
	public List<Node2D> empty_slots = new ArrayList<Node2D>();
	public int turn = 1;


	@RegisterFunction
	@Override
	public void _ready() {
		PlayerDeck = (Node2D) getNode("../Deck");
		OppDeck = (Node2D) getNode("../OpponentDeck");
		EndTurnButton = getNode("../EndTurnButton");
		timer = (Timer) getNode("../BattleTimer");
		timer.oneShotProperty(true);
		timer.waitTimeProperty(1.0);

		//I just rawdogged the timeout signal, there is no way to know this is the right signal
		timer.connect( "timeout",
				Callable.create(this, StringNames.toGodotName("_on_timer_timeout")),
				0);

		EndTurnButton.connect(
				"pressed",
				Callable.create(this, StringNames.toGodotName("onEndTurnPressed")),
				0
		);

		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot2"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot3"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot4"));
		empty_slots.add((Node2D) getNode("../Slots/EnemyCardSlot5"));
	}
	
	@RegisterFunction
	public void onEndTurnPressed() {
		EndTurnButton.set("disabled", true);
		EndTurnButton.set("visible", false);
		GD.INSTANCE.print("turn "+ turn);
		turn += 1;

		//We will make a function for opponent deck
		OppDeck.call("draw_card");

		//We are wasting a second to make it look like the AI thinks
		timer.start();
	}

	@RegisterFunction

	public void _on_timer_timeout(){
		//GD.INSTANCE.print("timeout");
		//Check if free monster card slot, if no end turn
//		if (empty_slots.isEmpty()) {
//			end_opponent_turn();
//			return;
//		}


		//Play the card with the highest number of cristal and chose an empty card slot
		getNode("../EnemyHand").call("highest_card");
		getNode("../EnemyHand").call("attack");


		//End turn, player can draw again
		end_opponent_turn();

	}

	@RegisterFunction

	public void end_opponent_turn(){
		getNode("../Cardmanager").set("card_played_this_turn", false);
		getNode("../Cardmanager").set("card_drawn_this_turn", false);
		EndTurnButton.set("disabled", false);
		EndTurnButton.set("visible", true);


		//If you want to automaticaly get a card
		//PlayerDeck.callDeferred("draw_card");



	}

}
