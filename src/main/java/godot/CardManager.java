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
public class CardManager extends Node2D {
	@Export
	@RegisterProperty

	//Function for the player rules
	public boolean card_played_this_turn = false;

	@Export
	@RegisterProperty
	public boolean card_drawn_this_turn = false;

	@RegisterProperty @Export
	public List<Node> player_field = new ArrayList<>();

	@RegisterProperty @Export
	public boolean cardselected = false;
	public Node2D cardselectedNode;

	//Used with raycast
	public int COLLISION_MASK_CARD_SLOT = 2;
	public Node2D cardDragged;
	public Rect2 screen_Size;

	//references
	public Node player_hand_ref;
	public Node game_deck_ref;

	//collision mask
	public int COLLISION_MASK_DECK = 4;
	public int COLLISION_MASK_CARD = 1;



	@RegisterFunction
	@Override
	public void _ready(){
		screen_Size = getViewportRect();
		player_hand_ref = getNode("../PlayerHand");
		game_deck_ref = getNode("../Deck");
	}




	// Fonction process, s'execute à toutes les frames du code
	@RegisterFunction
	@Override
	public void _process(double delta) {

		if (cardDragged != null){
			Vector2 mouse_Position = getGlobalMousePosition();
			//We made a custom clamp func to achieve this, the cards are know confined in the viewport
			Vector2 drag = new Vector2(clamp(mouse_Position.getX(), 0, screen_Size.getEnd().getX()),
					clamp(mouse_Position.getY(), 0, screen_Size.getEnd().getY()));
			cardDragged.set("position", drag);
		}
	}



	// Fonction verification des différents inputs
	@RegisterFunction
	public void _input(InputEvent event){
		if (event instanceof InputEventMouseButton mouseEvent && mouseEvent.getButtonIndex() == MouseButton.LEFT){
			// Listener du Clique gauche
			if (mouseEvent.isPressed()){
				Node2D cardDeck = _raycast_check_for_deck();
				Node2D card = _raycast_check_for_card();
				Node2D Target = _raycast_check_for_target();

				//if (cardDeck != null && !card_drawn_this_turn) {
					//met une carte en main quand on clique sur le deck
					//game_deck_ref.call("draw_card");
					//card_drawn_this_turn = true;

				//}
//				if (Target != null) {
//					GD.INSTANCE.print(Target.getName());
//				}

				if (Target != null
						&& cardselected
						&& cardselectedNode.get("attacked_this_turn").equals(false)) {
					Target.set("target", true);
					player_hand_ref.call("attack");
				}



				if (card != null){
					start_drag(card);
				}

				if (cardDeck != null && !cardselected && cardDeck.get("attacked_this_turn").equals(false)) {
					cardDeck.set("selected", true);
					cardDeck.setScale(new Vector2(0.7, 0.7));
					cardselected = true;
					cardselectedNode = cardDeck;
				}

				if (cardselected && player_hand_ref.get("enemyfieldempty").equals(true)) {
					getNode("../EnemyBox/CollisionShape2D").set("collision_layer", 8);
					getNode("../EnemyBox/CollisionShape2D").set("collision_mask", 8);
				}





			}
			else {
				if (cardDragged != null) {
					stop_drag();
				}



			}

		}
	}

	// Fonction de récupération de la carte courante, vérification de la collision avec la souris
	@RegisterFunction
	public Node2D _raycast_check_for_card(){
		PhysicsDirectSpaceState2D space_state = getWorld2d().getDirectSpaceState();
		PhysicsPointQueryParameters2D parameters = new PhysicsPointQueryParameters2D();
		parameters.setPosition(getGlobalMousePosition());
		parameters.setCollideWithAreas(true);
		parameters.setCollisionMask(COLLISION_MASK_CARD);

		VariantArray<Dictionary<Object, Object>> result = space_state.intersectPoint(parameters);
		if (!result.isEmpty()){
			Dictionary<Object, Object> hit = result.get(0);
			Object collider = hit.get("collider");

			if (collider instanceof Node node) {
				Node parent = node.getParent();
				if (parent instanceof Node2D nodeCarte){
					return nodeCarte;
				}

			}
		}
		return null;
	}


	//Same function but returns everything on COLLISION_MASK_CARD_SLOT
	@RegisterFunction
	public Node2D _raycast_check_for_card_slot(){
		PhysicsDirectSpaceState2D space_state = getWorld2d().getDirectSpaceState();
		PhysicsPointQueryParameters2D parameters = new PhysicsPointQueryParameters2D();
		parameters.setPosition(getGlobalMousePosition());
		parameters.setCollideWithAreas(true);
		parameters.setCollisionMask(COLLISION_MASK_CARD_SLOT);

		VariantArray<Dictionary<Object, Object>> result = space_state.intersectPoint(parameters);
		// If there is any collision Point, return.
		if (!result.isEmpty()){
			Dictionary<Object, Object> hit = result.get(0);
			Object collider = hit.get("collider");


			if (collider instanceof Node node) {
				Node parent = node.getParent();
				if (parent instanceof Node2D nodeCardSlot){
					return nodeCardSlot;
				}

			}
		}
		return null;
	}


	//Same function but returns everything on COLLISION_MASK_CARD_SLOT
	@RegisterFunction
	public Node2D _raycast_check_for_deck(){
		PhysicsDirectSpaceState2D space_state = getWorld2d().getDirectSpaceState();
		PhysicsPointQueryParameters2D parameters = new PhysicsPointQueryParameters2D();
		parameters.setPosition(getGlobalMousePosition());
		parameters.setCollideWithAreas(true);
		parameters.setCollisionMask(COLLISION_MASK_DECK);

		VariantArray<Dictionary<Object, Object>> result = space_state.intersectPoint(parameters);
		// If there is any collision Point, return.
		if (!result.isEmpty()){
			Dictionary<Object, Object> hit = result.get(0);
			Object collider = hit.get("collider");


			if (collider instanceof Node node) {
				Node parent = node.getParent();
				if (parent instanceof Node2D nodeGameDeck){
					return nodeGameDeck;
				}

			}
		}
		return null;
	}

	@RegisterFunction
	public Node2D _raycast_check_for_target(){
		PhysicsDirectSpaceState2D space_state = getWorld2d().getDirectSpaceState();
		PhysicsPointQueryParameters2D parameters = new PhysicsPointQueryParameters2D();
		parameters.setPosition(getGlobalMousePosition());
		parameters.setCollideWithAreas(true);
		parameters.setCollisionMask(8);

		VariantArray<Dictionary<Object, Object>> result = space_state.intersectPoint(parameters);
		// If there is any collision Point, return.
		if (!result.isEmpty()){
			Dictionary<Object, Object> hit = result.get(0);
			Object collider = hit.get("collider");


			if (collider instanceof Node node) {
				Node parent = node.getParent();
				if (parent instanceof Node2D nodeGameTarget){
					return nodeGameTarget;
				}

			}
		}
		return null;
	}



	@RegisterFunction
	public double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(value, max));
	}

	@RegisterFunction
	public void start_drag(Node2D card){
		cardDragged = card;
	}

	@RegisterFunction
	public void stop_drag(){
		Node2D card_slot_found = _raycast_check_for_card_slot();

		//if (card.get("type").equals("MONSTER"))

		//since card_slot_found.get returns an object the .equals method is used instead
		//add this line if you only want to move Monster card cardDragged.get("type").equals("MONSTER")
		if (card_slot_found != null && card_slot_found.get("card_in_slot").equals(false)
				&& !card_played_this_turn) {


			card_played_this_turn = true;
			cardDragged.setPosition(card_slot_found.getPosition());
			player_field.add(cardDragged);
			//Basic solution is to disable the collision of the card
			//Never trust shown name, right click > copy property path is safer
			//if you want to make things with those cards later on use those instead:
			cardDragged.getNode("Area2D").set("collision_mask", 4);
			cardDragged.getNode("Area2D").set("collision_layer", 4);
			//cardDragged.getNode("Area2D/CollisionShape2D").set("disabled", true);


			// I made a variable in each CardSlots, when true the slot is occupied
			card_slot_found.set("card_in_slot", true);
			cardDragged.set("in_slot", true);
		}
		else {
			// heures perdues ici : 6
			// ça c'est la pire méthode du monde, essaie de mettre le moindre argument ça explose
		}
		cardDragged = null;
		player_hand_ref.call("quoi");
	}

	@RegisterFunction
	public void resetsize() {
		if (cardselectedNode != null) {
			cardselectedNode.setScale(new Vector2(0.6, 0.6));
		}
	}

}
