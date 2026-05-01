package godot.deckbuilder.ui;

import godot.cards.BaseCarte;
import godot.deckbuilder.DeckBuilderCardDetailsPanelController;

import java.util.List;

/**
 * Keeps the deck builder details panel in sync with the current filtered list and tile clicks.
 */
public class DeckBuilderSelectionCoordinator {
	private final DeckBuilderCardDetailsPanelController detailsController;
	private BaseCarte selectedCard;

	public DeckBuilderSelectionCoordinator(DeckBuilderCardDetailsPanelController detailsController) {
		this.detailsController = detailsController;
	}

	public void select(BaseCarte card) {
		selectedCard = card;
		if (detailsController != null) {
			detailsController.showCard(card);
		}
	}

	public void sync(List<BaseCarte> cards) {
		if (cards == null || cards.isEmpty()) {
			selectedCard = null;
			if (detailsController != null) {
				detailsController.clearSelection();
			}
			return;
		}

		if (selectedCard == null || cards.stream().noneMatch(card -> card.getId() == selectedCard.getId())) {
			selectedCard = cards.get(0);
		}

		if (detailsController != null) {
			detailsController.showCard(selectedCard);
		}
	}
}
