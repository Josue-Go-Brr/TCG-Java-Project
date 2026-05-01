package godot.library.ui;

import godot.api.Control;
import godot.api.GridContainer;
import godot.api.Node;
import godot.api.PackedScene;
import godot.cards.BaseCarte;
import godot.core.Vector2;
import godot.global.GD;
import godot.library.CardTileController;
import godot.library.LibraryScreenController;

import java.util.ArrayList;
import java.util.List;

public class LibraryGridRenderer {
	private static final float BASE_MIN_HEIGHT = 500f;
	private static final float ESTIMATED_TILE_HEIGHT = 280f;
	private static final float ESTIMATED_VERTICAL_SEPARATION = 10f;

	private final GridContainer cardGrid;
	private final PackedScene cardTileScene;
	private final LibraryScreenController controller;
	private final List<CardTileController> activeTiles = new ArrayList<>();

	public LibraryGridRenderer(GridContainer cardGrid, PackedScene cardTileScene, LibraryScreenController controller) {
		this.cardGrid = cardGrid;
		this.cardTileScene = cardTileScene;
		this.controller = controller;
	}

	public void render(List<BaseCarte> cards) {
		clear();

		if (cardGrid == null || cardTileScene == null) {
			GD.INSTANCE.pushWarning("[Library] Missing grid or tile scene.");
			return;
		}

		for (BaseCarte card : cards) {
			Node instance = cardTileScene.instantiate();
			if (!(instance instanceof CardTileController tileController)) {
				if (instance != null) {
					instance.queueFree();
				}
				GD.INSTANCE.pushWarning("[Library] card_tile instance is not CardTileController.");
				continue;
			}

			cardGrid.addChild(tileController);
			tileController.setCardData(card);
			tileController.setLibraryScreenController(controller);
			activeTiles.add(tileController);
		}

		updateScrollableContentHeight(cards.size());
	}

	private void clear() {
		for (CardTileController tile : activeTiles) {
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
		GD.INSTANCE.print(
				"[Library][GridHeight] cards=" + cardCount
						+ ", columns=" + columns
						+ ", rows=" + rows
						+ ", finalHeight=" + finalHeight
		);

		cardGrid.setCustomMinimumSize(new Vector2(0, finalHeight));

		Node parent = cardGrid.getParent();
		if (parent instanceof Control parentControl) {
			parentControl.setCustomMinimumSize(new Vector2(0, finalHeight));
			GD.INSTANCE.print("[Library][GridHeight] parent min size set to " + finalHeight);
		}
	}
}
