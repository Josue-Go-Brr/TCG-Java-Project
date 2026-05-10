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

	public static final List<String> MONSTER_TYPE_FILTER_OPTIONS = List.of(
			"Dragon",
			"Machine",
			"Warrior",
			"Demon",
			"Spellcaster",
			"Beast",
			"Divine_Beast",
			"Aqua",
			"Elf",
			"Rock"
	);

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
		String monsterType = safe(resource.monster_type);
		String effect = safe(resource.description);
		return switch (type) {
			case "MAGIE" -> new CarteMagie(
				id, name, cost, description, image, imagePath, "none"
			);
			case "TRAP" -> new Cartepiege(
				id, name, cost, description, image, imagePath, "none"
			);
			default -> new CarteMonster(
				id, name, cost, description, image, imagePath, resource.atk, resource.defense, monsterType, effect,
				resource
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

		// Sort them by attack 
		monstersOnly.sort((monster1, monster2) ->
				Integer.compare(monster2.getAttack(), monster1.getAttack()));

		return monstersOnly;
	}

	public List<CarteMonster> sortMonstersByDefense() {
		List<CarteMonster> monstersOnly = new ArrayList<>();
		for (BaseCarte card : database.values()) {
			if (card instanceof CarteMonster) {
				monstersOnly.add((CarteMonster) card);
			}
		}
		monstersOnly.sort((m1, m2) -> Integer.compare(m2.getDefense(), m1.getDefense()));
		return monstersOnly;
	}

	/** All monster cards, sorted by monster type (then by name). */
	public List<CarteMonster> sortMonstersByMonsterType() {
		List<CarteMonster> monstersOnly = new ArrayList<>();
		for (BaseCarte card : database.values()) {
			if (card instanceof CarteMonster) {
				monstersOnly.add((CarteMonster) card);
			}
		}
		monstersOnly.sort(Comparator
				.comparing(CarteMonster::getMonsterType, String.CASE_INSENSITIVE_ORDER)
				.thenComparing(CarteMonster::getName, String.CASE_INSENSITIVE_ORDER));
		return monstersOnly;
	}

	
	public List<CarteMonster> getMonstersForMonsterTypeOption(String selectedMonsterType) {
		if (selectedMonsterType == null || selectedMonsterType.isBlank()) {
			return sortMonstersByMonsterType();
		}
		String want = normalizeMonsterTypeForMatch(selectedMonsterType);
		List<CarteMonster> filtered = new ArrayList<>();
		for (BaseCarte card : database.values()) {
			if (!(card instanceof CarteMonster monster)) {
				continue;
			}
			if (normalizeMonsterTypeForMatch(monster.getMonsterType()).equals(want)) {
				filtered.add(monster);
			}
		}
		filtered.sort(Comparator.comparing(CarteMonster::getName, String.CASE_INSENSITIVE_ORDER));
		return filtered;
	}

	private static String normalizeMonsterTypeForMatch(String value) {
		if (value == null) {
			return "";
		}
		String collapsed = value.trim().replace('_', ' ').replaceAll("\\s+", " ");
		return collapsed.toLowerCase();
	}

	public static final String QUERY_TYPE_ALL = "ALL";

	/** Filters and sorts cards for Library and Deck Builder (shared with {@code LibraryQueryService} / {@code DeckBuilderQueryService}). */
	public List<BaseCarte> queryLibrary(String searchText, String type, String monsterType, String sortField,
			boolean isDescending) {
		List<BaseCarte> results = new ArrayList<>(database.values());

		if (searchText != null && !searchText.trim().isEmpty()) {
			String q = searchText.trim().toLowerCase();
			results.removeIf(c -> !c.getName().toLowerCase().contains(q));
		}

		if (type != null && !QUERY_TYPE_ALL.equalsIgnoreCase(type.trim())) {
			String wantType = type.trim();
			results.removeIf(c -> !c.getType().equalsIgnoreCase(wantType));
		}

		if (monsterType != null && !QUERY_TYPE_ALL.equalsIgnoreCase(monsterType.trim())) {
			String wantMonster = normalizeMonsterTypeForMatch(monsterType);
			results.removeIf(c -> {
				if (!(c instanceof CarteMonster monster)) {
					return true;
				}
				return !normalizeMonsterTypeForMatch(monster.getMonsterType()).equals(wantMonster);
			});
		}

		String sort = sortField == null || sortField.isBlank() ? "NAME" : sortField.trim().toUpperCase();
		Comparator<BaseCarte> comparator = switch (sort) {
			case "ATK" -> Comparator.<BaseCarte>comparingInt(
					c -> c instanceof CarteMonster m ? m.getAttack() : Integer.MIN_VALUE)
					.thenComparing(BaseCarte::getName, String.CASE_INSENSITIVE_ORDER);
			case "DEFENSE" -> Comparator.<BaseCarte>comparingInt(
					c -> c instanceof CarteMonster m ? m.getDefense() : Integer.MIN_VALUE)
					.thenComparing(BaseCarte::getName, String.CASE_INSENSITIVE_ORDER);
			case "COST" -> Comparator.comparingInt(BaseCarte::getCost)
					.thenComparing(BaseCarte::getName, String.CASE_INSENSITIVE_ORDER);
			default -> Comparator.comparing(BaseCarte::getName, String.CASE_INSENSITIVE_ORDER);
		};

		if (isDescending) {
			comparator = comparator.reversed();
		}

		results.sort(comparator);
		return results;
	}
}
