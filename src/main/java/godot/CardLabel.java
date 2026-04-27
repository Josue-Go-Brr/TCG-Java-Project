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

	public void updateFromCard(Card thisCard){

		thisCard = (Card) this.getParent();

		// Adapter pour aller chercher ATk/DEF dans le dictionnaire/BDD ou toutes les cartes sont présentes.
		if (thisCard != null){

			setText("ATK : " + thisCard.atk + " DEF : " + thisCard.defense);

			GD.INSTANCE.print("ATK AND DEF LOADED IN LABEL : " + thisCard.atk  + " " + thisCard.defense + " FROM : " + thisCard.cardID);

		}
		else {
			GD.INSTANCE.print("thisCard is null");
		}
	}

} 
