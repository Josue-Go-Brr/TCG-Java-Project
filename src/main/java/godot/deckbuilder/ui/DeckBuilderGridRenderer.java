package godot.deckbuilder.ui;

import godot.api.Control;
import godot.api.GridContainer;
import godot.api.Node;
import godot.api.PackedScene;
import godot.cards.BaseCarte;
import godot.core.Vector2;
import godot.deckbuilder.DeckBuilderCardTileController;
import godot.deckbuilder.DeckBuilderScreenController;

import java.util.ArrayList;
import java.util.List;




public class DeckBuilderGridRenderer {
	private static final float BASE_MIN_HEIGHT = 500f;
	private static final float ESTIMATED_TILE_HEIGHT = 280f;
	private static final float ESTIMATED_VERTICAL_SEPARATION = 10f;

	private final GridContainer cardGrid;
	private final PackedScene cardTileScene;
	private final DeckBuilderScreenController controller;
	private final List<DeckBuilderCardTileController> activeTiles = new ArrayList<>();

	public DeckBuilderGridRenderer(GridContainer cardGrid, PackedScene cardTileScene, DeckBuilderScreenController controller) {
		this.cardGrid = cardGrid;
		this.cardTileScene = cardTileScene;
		this.controller = controller;
	}

	public void render(List<BaseCarte> cards) {
		clear();

		if (cardGrid == null || cardTileScene == null) {
			return;
		}

		for (BaseCarte card : cards) {
			Node instance = cardTileScene.instantiate();
			if (!(instance instanceof DeckBuilderCardTileController tileController)) {
				if (instance != null) {
					instance.queueFree();
				}
				continue;
			}

			cardGrid.addChild(tileController);
			tileController.setCardData(card);
			tileController.setDeckBuilderScreenController(controller);
			activeTiles.add(tileController);
		}

		updateScrollableContentHeight(cards.size());
	}

	private void clear() {
		for (DeckBuilderCardTileController tile : activeTiles) {
			if (tile != null) {
				tile.queueFree();
			}
		}
		activeTiles.clear();
	}

	private void updateScrollableContentHeight(int cardCount) {
		if (cardGrid == null) {
			return;
		}

		int columns = Math.max(1, cardGrid.getColumns());
		int rows = (int) Math.ceil(cardCount / (double) columns);
		float rowsHeight = rows * ESTIMATED_TILE_HEIGHT;
		float separatorsHeight = Math.max(0, rows - 1) * ESTIMATED_VERTICAL_SEPARATION;
		float finalHeight = Math.max(BASE_MIN_HEIGHT, rowsHeight + separatorsHeight);

		cardGrid.setCustomMinimumSize(new Vector2(0, finalHeight));

		Node parent = cardGrid.getParent();
		if (parent instanceof Control parentControl) {
			parentControl.setCustomMinimumSize(new Vector2(0, finalHeight));
		}
	}
}
