package godot.deck;

import godot.CardDB;
import godot.cards.BaseCarte;
import godot.cards.CardLibrary;

import java.util.Collections;
import java.util.List;

/**
 * Loads the full card catalog for the deck view (no search, filter, or sort UI).
 */
public class DeckCatalogService {
	private final CardLibrary cardLibrary;

	public DeckCatalogService(CardDB cardDB) {
		this.cardLibrary = cardDB == null ? null : new CardLibrary(cardDB);
	}

	public List<BaseCarte> getAllCards() {
		if (cardLibrary == null) {
			return Collections.emptyList();
		}
		return cardLibrary.getAllCards();
	}
}
