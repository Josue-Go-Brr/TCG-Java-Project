package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Node;
import godot.global.GD;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

@RegisterClass
public class CardDB extends Node {
	@Export
	public ArrayList<CardData> cardList = new ArrayList<>();
	public HashMap<String, CardData> cards = new HashMap<String, CardData>();

	@Override
	@RegisterFunction
	public void _ready(){

		for (CardData card : cardList){
			if (card != null && card.id != null){
				cards.put(card.id, card);
			}
		}


	}


}
