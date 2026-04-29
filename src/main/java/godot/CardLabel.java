package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Label;
import godot.core.Color;
import godot.global.GD;

@RegisterClass
public class CardLabel extends Label {

	@RegisterFunction
	@Override
	public void _ready() {

	}

	@RegisterFunction
	public void updateFromCard(Card thisCard){

		if (thisCard != null){

			setText("ATK : " + thisCard.atk + "\nDEF : " + thisCard.defense);
			//setZIndex(thisCard.getZIndex());
			// GD.INSTANCE.print("ATK AND DEF LOADED IN LABEL : " + thisCard.atk  + " " + thisCard.defense + " FROM : " + thisCard.cardID);

		}
		else {
			GD.INSTANCE.print("thisCard is null");
		}
	}

} 
