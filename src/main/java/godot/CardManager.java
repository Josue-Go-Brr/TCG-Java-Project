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
	public Node2D cardDragged;
	public Vector2 screen_Size;
	public Camera2D cam;


	@RegisterFunction
	@Override
	public void _ready(){
		cam = getViewport().getCamera2d();
	}

	
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



	
	@RegisterFunction
	public void _input(InputEvent event){
		if (event instanceof InputEventMouseButton mouseEvent && mouseEvent.getButtonIndex() == MouseButton.LEFT){
			
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

		if (event instanceof InputEventMouseButton mouseEvent && mouseEvent.getButtonIndex() == MouseButton.RIGHT){
			
			if (mouseEvent.isPressed()){
				
			}
			else {
				
			}
		}
	}

	
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
					
					return nodeCarte;
				}

			}
		}
		else {
			
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
		cardDragged.setScale(new Vector2(1, 1));
		cardDragged.setZIndex(3);
	}

	@RegisterFunction
	public void stop_drag(){
		cardDragged.setScale(new Vector2(1, 1));
		cardDragged.setZIndex(0);
		cardDragged = null;

	}


}
