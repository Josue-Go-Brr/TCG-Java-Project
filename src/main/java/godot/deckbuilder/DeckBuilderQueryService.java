package godot.deckbuilder;

import godot.CardDB;
import godot.cards.BaseCarte;
import godot.cards.CardLibrary;

import java.util.List;

public class DeckBuilderQueryService {
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

	public DeckBuilderQueryService(CardDB cardDB) {
		this.cardLibrary = new CardLibrary(cardDB);
	}

	public List<BaseCarte> queryCards(String searchText, String typeFilter, String monsterTypeFilter, String sortFilter, String sortOrder) {
		String safeSearch = searchText == null ? "" : searchText.trim().toLowerCase();
		String safeType = (typeFilter == null || typeFilter.isBlank()) ? TYPE_ALL : typeFilter.trim().toUpperCase();
		String safeMonsterType = (monsterTypeFilter == null || monsterTypeFilter.isBlank()) ? TYPE_ALL : monsterTypeFilter.trim();
		String safeSort = (sortFilter == null || sortFilter.isBlank()) ? SORT_NAME : sortFilter.trim().toUpperCase();

		boolean isDescending = sortOrder != null && ORDER_DESC.equalsIgnoreCase(sortOrder.trim());

		return cardLibrary.queryLibrary(safeSearch, safeType, safeMonsterType, safeSort, isDescending);
	}
}
