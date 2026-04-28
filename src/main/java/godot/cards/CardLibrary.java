package godot.cards;

import java.util.ArrayList;
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
	}

	public void addCard(BaseCarte card) {
		if (card != null) {
			database.put(card.getId(), card);
		}
	}

	public BaseCarte getById(int id) {
		return database.get(id);
	}

	public List<BaseCarte> getAllCards() {
		return new ArrayList<>(database.values());
	}

	public List<BaseCarte> getCardsByType(String type) {
		List<BaseCarte> result = new ArrayList<>();

		for (BaseCarte card : database.values()) {
			if (card.getType().equalsIgnoreCase(type)) {
				result.add(card);
			}
		}

		return result;
	}

	public List<BaseCarte> getCardsByName(String name) {
		List<BaseCarte> result = new ArrayList<>();

		for (BaseCarte card : database.values()) {
			if (card.getName().toLowerCase().contains(name.toLowerCase())) {
				result.add(card);
			}
		}

		return result;
	}

	public List<CarteMonster> getAllMonstersSortedByAttack() {
		List<CarteMonster> monstersOnly = new ArrayList<>();

		for (BaseCarte card : database.values()) {
			if (card instanceof CarteMonster) {
				monstersOnly.add((CarteMonster) card);
			}
		}

		monstersOnly.sort((monster1, monster2) ->
				Integer.compare(monster2.getAttack(), monster1.getAttack()));

		return monstersOnly;
	}
}
