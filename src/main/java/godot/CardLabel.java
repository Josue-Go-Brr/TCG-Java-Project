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

		// Adapter pour aller chercher ATk/DEF dans le dictionnaire/BDD ou toutes les cartes sont présentes.
		setText("ATK :DEF : ");
		if (getParent() instanceof Card card){
			if ("MagicienSombre".equals(card.getCardID())){
				setText("Magicien Sombre");
			}
		}

	}
} 
