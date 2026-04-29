package godot.library;

import godot.CardDB;
import godot.cards.BaseCarte;
import godot.cards.CardLibrary;
import godot.cards.CarteMonster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibraryQueryService {
	public static final String TYPE_ALL = "ALL";
	public static final String TYPE_MONSTER = "MONSTER";
	public static final String TYPE_MAGIE = "MAGIE";
	public static final String TYPE_TRAP = "TRAP";

	public static final String SORT_NAME = "NAME";
	public static final String SORT_COST = "COST";
	public static final String SORT_ATK = "ATK";

	private final CardLibrary cardLibrary;

	public LibraryQueryService(CardDB cardDB) {
		this.cardLibrary = new CardLibrary(cardDB);
	}

	public List<BaseCarte> queryCards(String searchText, String typeFilter, String sortFilter) {
		List<BaseCarte> cards = new ArrayList<>(cardLibrary.getAllCards());
		String normalizedSearch = searchText == null ? "" : searchText.trim().toLowerCase();
		String normalizedType = normalizeType(typeFilter);
		String normalizedSort = normalizeSort(sortFilter);

		cards.removeIf(card -> !matchesSearch(card, normalizedSearch));
		cards.removeIf(card -> !matchesType(card, normalizedType));

		sortCards(cards, normalizedSort);
		return cards;
	}

	private boolean matchesSearch(BaseCarte card, String normalizedSearch) {
		return normalizedSearch.isEmpty()
				|| card.getName().toLowerCase().contains(normalizedSearch);
	}

	private boolean matchesType(BaseCarte card, String normalizedType) {
		return TYPE_ALL.equals(normalizedType) || card.getType().equalsIgnoreCase(normalizedType);
	}

	private void sortCards(List<BaseCarte> cards, String normalizedSort) {
		if (SORT_COST.equals(normalizedSort)) {
			cards.sort(Comparator.comparingInt(BaseCarte::getCost).thenComparing(BaseCarte::getName));
			return;
		}

		if (SORT_ATK.equals(normalizedSort)) {
			cards.sort((left, right) -> {
				int rightAtk = right instanceof CarteMonster ? ((CarteMonster) right).getAttack() : Integer.MIN_VALUE;
				int leftAtk = left instanceof CarteMonster ? ((CarteMonster) left).getAttack() : Integer.MIN_VALUE;
				int compareAtk = Integer.compare(rightAtk, leftAtk);
				if (compareAtk != 0) {
					return compareAtk;
				}
				return left.getName().compareToIgnoreCase(right.getName());
			});
			return;
		}

		cards.sort(Comparator.comparing(BaseCarte::getName, String.CASE_INSENSITIVE_ORDER));
	}

	private String normalizeType(String typeFilter) {
		if (typeFilter == null || typeFilter.isBlank()) {
			return TYPE_ALL;
		}
		return typeFilter.trim().toUpperCase();
	}

	private String normalizeSort(String sortFilter) {
		if (sortFilter == null || sortFilter.isBlank()) {
			return SORT_NAME;
		}
		return sortFilter.trim().toUpperCase();
	}
}
