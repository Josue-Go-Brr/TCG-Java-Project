package godot.cards;

import godot.CardData;
import godot.CardDB;
import godot.api.ResourceLoader;
import godot.api.Texture2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CardLibrary {
	private final Map<Integer, BaseCarte> database = new LinkedHashMap<>();

	public CardLibrary(CardDB cardDB) {
		loadCardsFromDB(cardDB);
	}

	private void loadCardsFromDB(CardDB cardDB) {
		if (cardDB == null) {
			return;
		}

		List<CardData> resources = cardDB.getAllCards();
		resources.sort(Comparator.comparing(c -> c.alphabeticalId));

		int fallbackId = 1;

		for (CardData resource : resources) {
			BaseCarte carte = toBaseCarte(fallbackId++, resource);
			if (carte != null) {
				database.put(carte.getId(), carte);
			}
		}
	}

	private BaseCarte toBaseCarte(int id, CardData resource) {
		if (resource == null) {
			return null;
		}

		String name = safe(resource.name);
		String description = safe(resource.description);
		String imagePath = !safe(resource.imagePath).isBlank() ? resource.imagePath : "";
		Texture2D image = resource.image;
		if (image == null && !imagePath.isBlank()) {
			image = (Texture2D) ResourceLoader.load(imagePath, "Texture2D", ResourceLoader.CacheMode.REUSE);
		}
		int cost = resource.cost;
		String type = safe(resource.type).toUpperCase();
		return switch (type) {
			case "MAGIE" -> new CarteMagie(
				id, name, cost, description, image, imagePath, "none"
			);
			case "TRAP" -> new Cartepiege(
				id, name, cost, description, image, imagePath, "none"
			);
			default -> new CarteMonster(
				id, name, cost, description, image, imagePath, resource.atk, resource.defense, "none"
			);
		};
	}

	private String safe(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

	public List<BaseCarte> getAllCards() {
		return new ArrayList<>(database.values());
	}

	/** Numeric id assigned when the library was built from {@link CardDB}. */
	public BaseCarte getCardById(int id) {
		return database.get(id);
	}

	// Filtre par type.
	public List<BaseCarte> getCardsByType(String type) {
		List<BaseCarte> filteredList = new ArrayList<>();

		for (BaseCarte card : database.values()) {
			if (card.getType().equalsIgnoreCase(type)) {
				filteredList.add(card);
			}
		}

		return filteredList;
	}

	// Recherche par nom
	public List<BaseCarte> searchCardsByName(String query) {
		List<BaseCarte> searchResults = new ArrayList<>();

		String lowerCaseQuery = query == null ? "" : query.toLowerCase();

		for (BaseCarte card : database.values()) {
			if (card.getName().toLowerCase().contains(lowerCaseQuery)) {
				searchResults.add(card);

			}
		}
		
		return searchResults;
	}

	// Tri par cout
	public List<BaseCarte> sortCardsByCost(List<BaseCarte> cardsToSort) {
		cardsToSort.sort(Comparator.comparingInt(BaseCarte::getCost));
		return cardsToSort;
	}

	// Tri alphabetique.
	public List<BaseCarte> sortCardsByName(List<BaseCarte> cardsToSort) {
		cardsToSort.sort(Comparator.comparing(BaseCarte::getName));
		return cardsToSort;
	}

	// Tri des monstres
	public List<CarteMonster> sortMonstersByAttack() {
		List<CarteMonster> monstersOnly = new ArrayList<>();

		// Find all monsters
		for (BaseCarte card : database.values()) {
			if (card instanceof CarteMonster) {
				monstersOnly.add((CarteMonster) card);
			}
		}

		// Sort them by attack (highest to lowest)
		monstersOnly.sort((monster1, monster2) ->
				Integer.compare(monster2.getAttack(), monster1.getAttack()));

		return monstersOnly;
	}
}
