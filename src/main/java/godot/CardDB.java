package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.DirAccess;
import godot.api.Node;
import godot.api.ResourceLoader;
import godot.global.GD;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RegisterClass
public class CardDB extends Node {

	public HashMap<String, CardData> cards = new HashMap<String, CardData>();

	@Override
	@RegisterFunction
	public void _ready(){

		GD.INSTANCE.print("------LOADING CARD IN DB------");
		DirAccess dir = DirAccess.open("res://src/main/resources/Cards_Data");
		if (dir == null) {
			GD.INSTANCE.print("Cards_Data directory not found.");
			return;
		}

		dir.listDirBegin();

		String file = dir.getNext();

		while(!file.isEmpty()){
			if (file.endsWith(".tres")){
				CardData card = (CardData) ResourceLoader.load("res://src/main/resources/Cards_Data/" + file);

				if (card != null && card.id != null && !card.id.isBlank()){
					GD.INSTANCE.print(
							"LOADED : " + card.id +
							" ATK : " + card.atk +
							" DEF : " + card.defense
					);
					cards.put(card.id, card);
				}
				else {
					GD.INSTANCE.print("NULL CARD DETECTED"+ file);
				}
			}
			file = dir.getNext();
		}
		dir.listDirEnd();

		GD.INSTANCE.print("TOTAL CARDS LOADED : " + cards.size());
	}


	@RegisterFunction
	public CardData getCard(String cardId){
		CardData c = cards.get(cardId);

		GD.INSTANCE.print("Requested card : " + cardId);

		return c;
	}
	
	@RegisterFunction
	public List<CardData> getAllCards() {
		return new ArrayList<>(cards.values());
	}

}
