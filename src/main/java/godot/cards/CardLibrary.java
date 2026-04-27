package godot.cards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CardLibrary {
	private Map<Integer, BaseCarte> database;

	public CardLibrary() {
		database = new LinkedHashMap<>();
		loadCards();

	}

	private void loadCards() {
		database.put(1, new CarteMonster(
			1, "Blackland Fire Dragon", 4,
			"A dragon that dwells in the depths of darkness, its vulnerability lies in its poor eyesight.",
			"res://assets/sprites/Cartes/CartesMonstre/BlacklandFireDragon.png",
			1500, 800,
			"nothing"
		));

		database.put(2, new CarteMonster(
			2, "Black Rabbit", 4,
			"He leaps up, down, and all around! Nobody can lay a hand on this funny bunny.",
			"res://assets/sprites/Cartes/CartesMonstre/BlackRabbit.png",
			1100, 1500,
			"nothing"
		));

		database.put(3, new CarteMonster(
			3, "Black Rose Dragon", 5,
			"Black Rose Dragon is a black-skinned wyvern with magenta claws and plate edges. Its tail and neck are covered in overlapping plates of petal-shaped armor.",
			"res://assets/sprites/Cartes/CartesMonstre/BlackRoseDragon.png",
			2400, 1800,
			"nothing"
		));

	}}
