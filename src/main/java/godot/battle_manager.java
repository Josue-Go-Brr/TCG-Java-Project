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
	public Timer timer;
	public Node2D OppDeck;
	public Node2D PlayerDeck;
	@RegisterProperty @Export public int cristals;

	@RegisterProperty @Export public int enemyAtk;
	@RegisterProperty @Export public int playerAtk;

	public int turn = 1;
	@RegisterProperty @Export public boolean player_turn= true;




	@RegisterFunction
	@Override
	public void _ready() {
		cristals = 1;
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
	}



		@RegisterFunction
	public void onEndTurnPressed() {
		//resets selected card size

		 getNode("../Cardmanager").call("resetsize");

		player_turn = false;
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
		//Play the card with the highest number of cristal and chose an empty card slot
		getNode("../EnemyHand").call("highest_card");
		getNode("../EnemyHand").call("attack");

		//End turn, player can draw again, I call it in the end of the attack fonction
		//end_opponent_turn();

	}

	@RegisterFunction

	public void end_opponent_turn(){
		getNode("../Cardmanager").set("card_played_this_turn", false);
		getNode("../Cardmanager").set("card_drawn_this_turn", false);
		EndTurnButton.set("disabled", false);
		EndTurnButton.set("visible", true);

		player_turn = true;
		getNode("../Deck").call("draw_card");

		for (int i = 0; i < getNode("../Cardmanager").getChildCount(); i++) {
			getNode("../Cardmanager").getChild(i).set("attacked_this_turn", false);
			getNode("../Cardmanager").getChild(i).set("target", false);
			getNode("../Cardmanager").getChild(i).set("selected", false);
			getNode("../Cardmanager").set("cardselected", false);
			getNode("../PlayerHand").call("update_enemy_field");
		}

		//getNode("../EnemyHand").set("compteur", 0);
		//If you want to automaticaly get a card
		//PlayerDeck.callDeferred("draw_card");



	}

}
