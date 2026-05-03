package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Label;
import godot.core.Color;

@RegisterClass
public class CardLabel extends Label {

	@RegisterFunction
	@Override
	public void _ready() {

	}

	public void updateFromCard(Card thisCard){

		thisCard = (Card) this.getParent();

		
		if (thisCard != null){

			setText("ATK : " + thisCard.atk + " DEF : " + thisCard.defense);

		}
	}

} 
