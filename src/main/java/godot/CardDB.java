package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.DirAccess;
import godot.api.Node;
import godot.api.ResourceLoader;
import godot.global.GD;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

		if (dir != null){
			dir.listDirBegin();

			String file = dir.getNext();

			while(!file.isEmpty()){			// Boucle qui remplit la DB automatiquement à partir des fichiers Ressources de Cards_Data
				if (file.endsWith(".tres")){

					CardData card = (CardData) ResourceLoader.load("res://src/main/resources/Cards_Data/" + file);

					if (card != null && card.id != null){
						// Defensive sync: ensure monster_type is populated even if JVM field binding misses it.
						if (card.monster_type == null || card.monster_type.isBlank()) {
							Object rawMonsterType = card.get("monster_type");
							if (rawMonsterType instanceof String value) {
								card.monster_type = value;
							}
						}
						GD.INSTANCE.print(
								"LOADED : " + card.id +
								" MONSTER_TYPE : " + card.monster_type +
								" ATK : " + card.atk +
								" DEF : " + card.defense
						);
						cards.put(card.id, card);
					}
					else {
						GD.INSTANCE.print("NULL CARD DETECTED");
					}
				}
				file = dir.getNext();
			}
			dir.listDirEnd();

			GD.INSTANCE.print("TOTAL CARDS LOADED : " + cards.size());
		}
	}


	@RegisterFunction
	public CardData getCard(String cardId){
		CardData c = cards.get(cardId);

		GD.INSTANCE.print("Requested card : " + cardId);		// Affiche la carte demandée

		return c;
	}

	@RegisterFunction
	public HashMap<String, CardData> getCards() {
		return cards;
	}

	@RegisterFunction
	public List<CardData> getAllCards() {
		return new ArrayList<>(cards.values());
	}
}
