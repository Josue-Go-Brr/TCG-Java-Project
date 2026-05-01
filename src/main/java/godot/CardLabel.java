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

	@Export
	@RegisterProperty
	public String labelID = "";

	@RegisterFunction
	@Override
	public void _ready() {

	}

	@RegisterFunction
	public void updateFromCard(Card thisCard){

		if (thisCard != null){
			if (this.labelID.equals("CardLabelAtkDef")){
				setText("ATK : " + thisCard.atk + "\nDEF : " + thisCard.defense);
			}
			if (this.labelID.equals("CardLabelCost")){
				setText(String.valueOf(thisCard.cost));
			}
			else {
				GD.INSTANCE.print(this.labelID + " is undefined or incorrect");
			}
			// GD.INSTANCE.print("ATK AND DEF LOADED IN LABEL : " + thisCard.atk  + " " + thisCard.defense + " FROM : " + thisCard.cardID);

		}
		else {
			GD.INSTANCE.print("thisCard is null");
		}
	}

} 
