package godot.deck;

import godot.CardDB;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.AcceptDialog;
import godot.api.Button;
import godot.api.Control;
import godot.api.FileAccess;
import godot.api.GridContainer;
import godot.api.InputEvent;
import godot.api.InputEventMouseButton;
import godot.api.Label;
import godot.api.PackedScene;
import godot.api.ResourceLoader;
import godot.api.ScrollContainer;
import godot.cards.BaseCarte;
import godot.cards.CardLibrary;
import godot.core.Callable;
import godot.core.MouseButton;
import godot.core.StringNames;
import godot.global.GD;
import godot.deck.ui.DeckGridRenderer;

import java.util.List;


@RegisterClass
public class DeckScreenController extends Control {
	private static final String CARD_TILE_SCENE_PATH = "res://scene/deck/card_tile.tscn";
	private static final String START_MENU_SCENE_PATH = "res://scene/menu/start_menu.tscn";
	private static final String DECK_BUILDER_SCENE_PATH = "res://scene/deck_builder/deck_builder_screen.tscn";
	private static final String USER_DECK_SAVE_PATH = "user://saved_deck.txt";
	private static final int MANUAL_SCROLL_STEP = 25;

	private Label deckCountLabelNode;
	private Button saveDeckButtonNode;
	private Button clearDeckButtonNode;
	private Button backButtonNode;
	private Button deckBuilderButtonNode;
	private GridContainer cardGridNode;
	private ScrollContainer cardGridScrollNode;
	private Label emptyStateNode;
	private AcceptDialog invalidDeckDialogNode;
	private PackedScene cardTileScene;

	private CardLibrary cardLibrary;
	private DeckGridRenderer gridRenderer;

	@RegisterFunction
	@Override
	public void _ready() {
		GD.INSTANCE.print("[Deck] _ready entered");

		deckCountLabelNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/DeckCountLabel");
		saveDeckButtonNode = (Button) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SaveDeckButton");
		clearDeckButtonNode = (Button) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/ClearDeckButton");
		backButtonNode = (Button) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/BackButton");
		deckBuilderButtonNode = (Button) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/DeckBuilderButton");
		cardGridScrollNode = (ScrollContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll");
		cardGridNode = (GridContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll/CardArea/CardGrid");
		emptyStateNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/EmptyState");
		invalidDeckDialogNode = (AcceptDialog) getNodeOrNull("InvalidDeckDialog");
		cardTileScene = (PackedScene) ResourceLoader.load(
				CARD_TILE_SCENE_PATH,
				"PackedScene",
				ResourceLoader.CacheMode.REUSE
		);

		CardDB cardDB = resolveCardDB();
		cardLibrary = cardDB == null ? null : new CardLibrary(cardDB);

		connectSaveDeckButton();
		connectClearDeckButton();
		connectBackButton();
		connectDeckBuilderButton();
		connectManualScrollFallback();

		gridRenderer = new DeckGridRenderer(cardGridNode, cardTileScene, this);

		updateDeckCountLabel();
		updateSaveDeckButtonState();
		refreshGrid();
	}

	@RegisterFunction
	public void _unhandled_input(InputEvent event) {
		handleManualWheelScroll(event);
	}

	@RegisterFunction
	public void _on_card_grid_scroll_gui_input(InputEvent event) {
		handleManualWheelScroll(event);
	}

	private void handleManualWheelScroll(InputEvent event) {
		if (cardGridScrollNode == null) {
			return;
		}
		if (!(event instanceof InputEventMouseButton mouseEvent)) {
			return;
		}
		if (!mouseEvent.isPressed()) {
			return;
		}

		if (mouseEvent.getButtonIndex() == MouseButton.WHEEL_UP) {
			cardGridScrollNode.setVScroll(Math.max(0, cardGridScrollNode.getVScroll() - MANUAL_SCROLL_STEP));
		} else if (mouseEvent.getButtonIndex() == MouseButton.WHEEL_DOWN) {
			cardGridScrollNode.setVScroll(cardGridScrollNode.getVScroll() + MANUAL_SCROLL_STEP);
		}
	}

	private void connectManualScrollFallback() {
		if (cardGridScrollNode == null) {
			return;
		}
		cardGridScrollNode.connect(
				"gui_input",
				Callable.create(this, StringNames.toGodotName("_on_card_grid_scroll_gui_input")),
				0
		);
	}

	private void connectSaveDeckButton() {
		if (saveDeckButtonNode == null) {
			return;
		}
		saveDeckButtonNode.connect(
				"pressed",
				Callable.create(this, StringNames.toGodotName("_on_save_deck_button_pressed")),
				0
		);
	}

	private void connectClearDeckButton() {
		if (clearDeckButtonNode == null) {
			return;
		}
		clearDeckButtonNode.connect(
				"pressed",
				Callable.create(this, StringNames.toGodotName("_on_clear_deck_button_pressed")),
				0
		);
	}

	private void connectBackButton() {
		if (backButtonNode == null) {
			return;
		}
		backButtonNode.connect(
				"pressed",
				Callable.create(this, StringNames.toGodotName("_on_back_button_pressed")),
				0
		);
	}

	private void connectDeckBuilderButton() {
		if (deckBuilderButtonNode == null) {
			return;
		}
		deckBuilderButtonNode.connect(
				"pressed",
				Callable.create(this, StringNames.toGodotName("_on_deck_builder_button_pressed")),
				0
		);
	}

	public void onCardTileClicked(BaseCarte card) {
	}

	private void updateDeckCountLabel() {
		if (deckCountLabelNode == null) {
			return;
		}
		deckCountLabelNode.setText(DeckState.buildDeckStatusLabel());
		updateSaveDeckButtonState();
	}

	private void updateSaveDeckButtonState() {
		if (saveDeckButtonNode == null) {
			return;
		}
		boolean valid = DeckState.isDeckValid();
		saveDeckButtonNode.setDisabled(!valid);
		
		saveDeckButtonNode.setModulate(valid
				? new godot.core.Color(0.7, 0.7, 0.7, 1.0)
				: new godot.core.Color(0.45, 0.45, 0.45, 1.0));
		saveDeckButtonNode.setTooltipText(valid
				? "Deck is valid. Save is enabled."
				: "Deck is invalid (must be 20 to 40 cards).");
	}

	private void refreshGrid() {
		if (gridRenderer == null) {
			return;
		}
		List<BaseCarte> cards = cardLibrary == null
				? List.of()
				: DeckState.expandDeckForGrid(cardLibrary);
		gridRenderer.render(cards);
		if (emptyStateNode != null) {
			boolean empty = DeckState.getTotalCardCount() == 0;
			emptyStateNode.setVisible(empty);
			if (empty) {
				emptyStateNode.setText("No cards yet. Add cards in Deck Builder (max 3 copies each).");
			}
		}
		updateSaveDeckButtonState();
	}

	private CardDB resolveCardDB() {
		CardDB local = (CardDB) getNodeOrNull("CardDB");
		if (local != null) {
			return local;
		}
		CardDB fromMain = (CardDB) getNodeOrNull("/root/main/CardDB");
		if (fromMain != null) {
			return fromMain;
		}
		return (CardDB) getNodeOrNull("/root/CardDB");
	}

	@RegisterFunction
	public void _on_save_deck_button_pressed() {
		if (!DeckState.isDeckValid()) {
			updateSaveDeckButtonState();
			if (invalidDeckDialogNode != null) {
				invalidDeckDialogNode.popupCentered();
			}
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("total=").append(DeckState.getTotalCardCount()).append('\n');
		for (String line : DeckState.serializeLines()) {
			sb.append(line).append('\n');
		}
		FileAccess file = FileAccess.open(USER_DECK_SAVE_PATH, FileAccess.ModeFlags.WRITE);
		if (file == null) {
			return;
		}
		file.storeString(sb.toString());
		file.close();
	}

	@RegisterFunction
	public void _on_clear_deck_button_pressed() {
		DeckState.clear();
		updateDeckCountLabel();
		refreshGrid();
	}

	@RegisterFunction
	public void _on_back_button_pressed() {
		getTree().changeSceneToFile(START_MENU_SCENE_PATH);
	}

	@RegisterFunction
	public void _on_deck_builder_button_pressed() {
		getTree().changeSceneToFile(DECK_BUILDER_SCENE_PATH);
	}

}
