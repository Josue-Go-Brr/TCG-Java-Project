package godot.library;

import godot.CardDB;
import godot.cards.BaseCarte;
import godot.cards.CardLibrary;

import java.util.List;

public class LibraryQueryService {
	public static final String TYPE_ALL = "ALL";
	public static final String TYPE_MONSTER = "MONSTER";
	public static final String TYPE_MAGIE = "MAGIE";
	public static final String TYPE_TRAP = "TRAP";

	public static final String SORT_NAME = "NAME";
	public static final String SORT_COST = "COST";
	public static final String SORT_ATK = "ATK";
	public static final String SORT_DEFENSE = "DEFENSE";

	public static final String ORDER_ASC = "CROISSANT";
	public static final String ORDER_DESC = "DECROISSANT";

	private final CardLibrary cardLibrary;

	public LibraryQueryService(CardDB cardDB) {
		this.cardLibrary = new CardLibrary(cardDB);
	}

	public List<BaseCarte> queryCards(String searchText, String typeFilter, String monsterTypeFilter, String sortFilter, String sortOrder) {
		String normalizedSearch = searchText == null ? "" : searchText.trim().toLowerCase();
		String normalizedType = normalizeString(typeFilter, TYPE_ALL);
		String normalizedMonsterType = normalizeString(monsterTypeFilter, TYPE_ALL);
		String normalizedSort = normalizeString(sortFilter, SORT_NAME);
		boolean isDescending = ORDER_DESC.equalsIgnoreCase(normalizeString(sortOrder, ORDER_ASC));

		return cardLibrary.queryLibrary(normalizedSearch, normalizedType, normalizedMonsterType, normalizedSort, isDescending);
	}

	private static String normalizeString(String val, String def) {
		return (val == null || val.isBlank()) ? def : val.trim().toUpperCase();
	}
}
