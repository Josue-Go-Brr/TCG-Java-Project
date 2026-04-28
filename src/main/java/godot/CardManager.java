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
	public Vector2 screen_Size;
	public Camera2D cam;


	@RegisterFunction
	@Override
	public void _ready(){
		cam = getViewport().getCamera2d();
	}

	// Fonction process, s'execute à toutes les frames du code
	@RegisterFunction
	@Override
	public void _process(double delta) {

		if (cam == null) return;

		Vector2 camPos = cam.getGlobalPosition();
		screen_Size = getViewport().getVisibleRect().getSize();
		Vector2 zoom = cam.getZoom();

		float halfW = (float) (screen_Size.getX() / zoom.getX()) /2;
		float halfH = (float) (screen_Size.getY() / zoom.getY()) /2;

		if (cardDragged != null){
			Vector2 mouse_Position = getGlobalMousePosition();

			cardDragged.setGlobalPosition(new Vector2(
					clamp((float) mouse_Position.getX(), (float) (camPos.getX() - halfW), (float) (camPos.getX() + halfW)),
					clamp((float) mouse_Position.getY(), (float) (camPos.getY() - halfH), (float) (camPos.getY() + halfH))
			));
		}
	}



	// Fonction verification des différents inputs
	@RegisterFunction
	public void _input(InputEvent event){
		if (event instanceof InputEventMouseButton mouseEvent && mouseEvent.getButtonIndex() == MouseButton.LEFT){
			// Listener du Clique gauche
			if (mouseEvent.isPressed()){
				//GD.INSTANCE.print("Left Click");
				Node2D card = _raycast_check_for_card();
				if (card != null){
					start_drag(card);
				}
			}
			else {
				//GD.INSTANCE.print("Left Click Released");
				if (cardDragged != null) {
					stop_drag();
				}
			}
		}

		if (event instanceof InputEventMouseButton mouseEvent && mouseEvent.getButtonIndex() == MouseButton.RIGHT){
			// Listener du Clique droit
			if (mouseEvent.isPressed()){
				//GD.INSTANCE.print("Right Click");
			}
			else {
				//GD.INSTANCE.print("Right Click Released");
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
	public float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(value, max));
	}

	@RegisterFunction
	public void start_drag(Node2D card){
		cardDragged = card;
		card.setScale(new Vector2(1, 1));
	}

	@RegisterFunction
	public void stop_drag(){
		cardDragged.setScale(new Vector2(1, 1));
		Node2D card_slot_found = _raycast_check_for_card_slot();

		//since card_slot_found.get returns an object the .equals method is used instead
		if (card_slot_found != null && card_slot_found.get("card_in_slot").equals(false)) {

			cardDragged.setPosition(card_slot_found.getPosition());

			//Basic solution is to disable the collision of the card, but I prefered to set a different collision layer and mask instead
			//cardDragged.getNode("Area2D/CollisionShape2D").set("Disabled", false);
			cardDragged.getNode("Area2D").set("collision_mask", 2);
			cardDragged.getNode("Area2D").set("collision_layer", 2);


			// I made a variable in each CardSlots, when true the slot is occupied
			GD.INSTANCE.print(card_slot_found.get("card_in_slot"));
			GD.INSTANCE.print(cardDragged.getNode("Area2D").get("collision_mask"));
			card_slot_found.set("card_in_slot", true);
		}
		cardDragged = null;
	}


}
