package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.core.Dictionary;
import godot.core.MouseButton;
import godot.core.VariantArray;
import godot.core.Vector2;
import godot.global.GD;

import java.lang.Object;

@RegisterClass
public class cardmanager extends Node2D {
	@Export
	@RegisterProperty
	public Node2D cardDragged;


	// Fonction process, s'execute à toutes les frames du code
	@RegisterFunction
	@Override
	public void _process(double delta) {
		if (cardDragged != null){
			Vector2 mouse_Position = getGlobalMousePosition();
			cardDragged.setPosition(mouse_Position);
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
					cardDragged = card;
				}
			}
			else {
				//GD.INSTANCE.print("Left Click Released");
				cardDragged = null;
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
		else {
			GD.INSTANCE.print("No Collision");
		}

		return null;
	}

}
