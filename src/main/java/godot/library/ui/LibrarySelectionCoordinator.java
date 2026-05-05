package godot.library.ui;

import godot.cards.BaseCarte;
import godot.library.CardDetailsPanelController;

import java.util.List;

public class LibrarySelectionCoordinator {
	private final CardDetailsPanelController detailsController;
	private BaseCarte selectedCard;

	public LibrarySelectionCoordinator(CardDetailsPanelController detailsController) {
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
