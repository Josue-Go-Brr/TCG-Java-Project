package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.DirAccess;
import godot.api.Node;
import godot.api.ResourceLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RegisterClass
public class CardDB extends Node {

	public HashMap<String, CardData> cards = new HashMap<String, CardData>();

	@Override
	@RegisterFunction
	public void _ready(){

		DirAccess dir = DirAccess.open("res://src/main/resources/Cards_Data");
		if (dir == null) {
			return;
		}

		dir.listDirBegin();

		String file = dir.getNext();

		while(!file.isEmpty()){
			if (file.endsWith(".tres")){
				CardData card = (CardData) ResourceLoader.load("res://src/main/resources/Cards_Data/" + file);

				if (card != null && card.id != null && !card.id.isBlank()){
					cards.put(card.id, card);
				}
			}
			file = dir.getNext();
		}
		dir.listDirEnd();
	}


	@RegisterFunction
	public CardData getCard(String cardId){
		CardData c = cards.get(cardId);

		return c;
	}
	
	@RegisterFunction
	public List<CardData> getAllCards() {
		return new ArrayList<>(cards.values());
	}

}
