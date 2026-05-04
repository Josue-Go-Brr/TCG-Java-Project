package godot;
import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;

import godot.api.*;
import godot.core.*;
import godot.global.GD;
import java.lang.Object;

@RegisterClass
public class CardManager extends Node2D {
	@Export
	@RegisterProperty
	public int COLLISION_MASK_CARD_SLOT = 2;
	public Node2D cardDragged;
	public Rect2 screen_Size;
	public Node player_hand_ref;



	@RegisterFunction
	@Override
	public void _ready(){
		screen_Size = getViewportRect();
		player_hand_ref = getNode("../PlayerHand");
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
				Node2D card = _raycast_check_for_card();
				if (card != null){
					start_drag(card);
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
		parameters.setCollisionMask(1);

		VariantArray<Dictionary<Object, Object>> result = space_state.intersectPoint(parameters);
		if (!result.isEmpty()){
			Dictionary<Object, Object> hit = result.get(0);
			Object collider = hit.get("collider");

			if (collider instanceof Node node) {
				Node parent = node.getParent();
				if (parent instanceof Node2D nodeCarte){
					GD.INSTANCE.print("Node name: " + node.getParent());
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
					GD.INSTANCE.print("Node name: " + node.getParent());
					return nodeCardSlot;
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

		//since card_slot_found.get returns an object the .equals method is used instead
		if (card_slot_found != null && card_slot_found.get("card_in_slot").equals(false)) {

			cardDragged.setPosition(card_slot_found.getPosition());

			//Basic solution is to disable the collision of the card
			//Never trust shown name, right click > copy property path is safer
			cardDragged.getNode("Area2D/CollisionShape2D").set("disabled", true);
			cardDragged.set("flag", true);
			//if you want to make things with those cards later on use those instead:
			//cardDragged.getNode("Area2D").set("collision_mask", 2);
			//cardDragged.getNode("Area2D").set("collision_layer", 2);


			// I made a variable in each CardSlots, when true the slot is occupied
			card_slot_found.set("card_in_slot", true);
			cardDragged.set("in_slot", true);
		}
		else {


			// heures perdues ici : 6
			// ça c'est la pire méthode du monde, essaie de mettre le moindre argument ça explose
			player_hand_ref.call("quoi");
		}

		player_hand_ref.call("quoi");
		cardDragged = null;
	}


}
