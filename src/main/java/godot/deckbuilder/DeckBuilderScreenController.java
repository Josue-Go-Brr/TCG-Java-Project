package godot.deckbuilder;

import godot.CardDB;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Button;
import godot.api.Control;
import godot.api.GridContainer;
import godot.api.InputEvent;
import godot.api.InputEventMouseButton;
import godot.api.Label;
import godot.api.LineEdit;
import godot.api.OptionButton;
import godot.api.PanelContainer;
import godot.api.PackedScene;
import godot.api.ResourceLoader;
import godot.api.ScrollContainer;
import godot.cards.BaseCarte;
import godot.core.Callable;
import godot.core.Error;
import godot.core.MouseButton;
import godot.core.StringNames;
import godot.global.GD;
import godot.deck.DeckState;
import godot.deckbuilder.ui.DeckBuilderGridRenderer;
import godot.deckbuilder.ui.DeckBuilderSelectionCoordinator;
import godot.deckbuilder.ui.DeckBuilderUiBinder;

import java.util.ArrayList;
import java.util.List;


@RegisterClass
public class DeckBuilderScreenController extends Control {
	private static final String CARD_TILE_SCENE_PATH = "res://scene/deck_builder/card_tile.tscn";
	private static final String START_MENU_SCENE_PATH = "res://scene/menu/start_menu.tscn";
	private static final String DECK_SCENE_PATH = "res://scene/deck/deck_screen.tscn";
	private static final int MANUAL_SCROLL_STEP = 25;

	private LineEdit searchInputNode;
	private OptionButton typeFilterNode;
	private OptionButton sortFilterNode;
	private Button backButtonNode;
	private GridContainer cardGridNode;
	private ScrollContainer cardGridScrollNode;
	private Label emptyStateNode;
	private Label deckCardCountLabelNode;
	private PanelContainer detailsPanelNode;
	private PackedScene cardTileScene;

	private DeckBuilderQueryService queryService;
	private DeckBuilderUiBinder uiBinder;
	private DeckBuilderGridRenderer gridRenderer;
	private DeckBuilderSelectionCoordinator selectionCoordinator;

	@RegisterFunction
	@Override
	public void _ready() {
		GD.INSTANCE.print("[DeckBuilder] _ready entered");

		searchInputNode = (LineEdit) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SearchInput");
		typeFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/TypeFilter");
		sortFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SortFilter");
		backButtonNode = (Button) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/BackButton");
		cardGridScrollNode = (ScrollContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll");
		cardGridNode = (GridContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll/CardArea/CardGrid");
		emptyStateNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/EmptyState");
		deckCardCountLabelNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/DeckInfoBar/DeckCardCountLabel");
		if (deckCardCountLabelNode == null) {
			GD.INSTANCE.printErr("[DeckBuilder] DeckCardCountLabel not found at DeckInfoBar/DeckCardCountLabel.");
		}
		detailsPanelNode = (PanelContainer) getNodeOrNull("RootMargin/MainColumns/RightSideDetails");
		cardTileScene = (PackedScene) ResourceLoader.load(
				CARD_TILE_SCENE_PATH,
				"PackedScene",
				ResourceLoader.CacheMode.REUSE
		);

		CardDB cardDB = resolveCardDB();
		if (cardDB == null) {
			GD.INSTANCE.printErr("[DeckBuilder] CardDB not found. Deck builder catalog will stay empty.");
		}
		queryService = new DeckBuilderQueryService(cardDB);

		uiBinder = new DeckBuilderUiBinder(searchInputNode, typeFilterNode, sortFilterNode);
		uiBinder.setupDefaultOptions();
		uiBinder.connect(this);
		connectBackButton();
		// Back to Deck: signal connected in deck_builder_screen.tscn (reliable with JVM + Callable quirks).
		connectManualScrollFallback();
		logScrollState("ready-before-render");

		DeckBuilderCardDetailsPanelController detailsController = getDetailsController();
		if (detailsController != null) {
			detailsController.bindDeckBuilderScreen(this);
		}
		selectionCoordinator = new DeckBuilderSelectionCoordinator(detailsController);
		gridRenderer = new DeckBuilderGridRenderer(cardGridNode, cardTileScene, this);

		refreshGrid();
		callDeferred(StringNames.toGodotName("_deferred_refresh_deck_count_label"));
		callDeferred(StringNames.toGodotName("debugDeferredScrollProbe"));
	}

	@RegisterFunction
	public void _on_search_input_text_changed(String newText) {
		refreshGrid();
	}

	@RegisterFunction
	public void _on_type_filter_item_selected(long index) {
		refreshGrid();
	}

	@RegisterFunction
	public void _on_sort_filter_item_selected(long index) {
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
			int before = cardGridScrollNode.getVScroll();
			cardGridScrollNode.setVScroll(Math.max(0, cardGridScrollNode.getVScroll() - MANUAL_SCROLL_STEP));
			GD.INSTANCE.print("[DeckBuilder][WheelUp] vScroll " + before + " -> " + cardGridScrollNode.getVScroll());
			logScrollState("wheel-up");
		} else if (mouseEvent.getButtonIndex() == MouseButton.WHEEL_DOWN) {
			int before = cardGridScrollNode.getVScroll();
			cardGridScrollNode.setVScroll(cardGridScrollNode.getVScroll() + MANUAL_SCROLL_STEP);
			GD.INSTANCE.print("[DeckBuilder][WheelDown] vScroll " + before + " -> " + cardGridScrollNode.getVScroll());
			logScrollState("wheel-down");
		}
	}

	private void connectManualScrollFallback() {
		if (cardGridScrollNode == null) {
			return;
		}
		Error err = cardGridScrollNode.connect(
				"gui_input",
				Callable.create(this, StringNames.toGodotName("_on_card_grid_scroll_gui_input")),
				0
		);
		if (err != Error.OK) {
			GD.INSTANCE.printErr("[DeckBuilder] Failed to connect CardGridScroll gui_input: " + err);
		}
	}

	private void connectBackButton() {
		if (backButtonNode == null) {
			GD.INSTANCE.printErr("[DeckBuilder] BackButton not found.");
			return;
		}

		Error err = backButtonNode.connect(
				"pressed",
				Callable.create(this, StringNames.toGodotName("_on_back_button_pressed")),
				0
		);
		if (err != Error.OK) {
			GD.INSTANCE.printErr("[DeckBuilder] Failed to connect BackButton pressed: " + err);
		}
	}

	public void onCardTileClicked(BaseCarte card) {
		if (selectionCoordinator != null) {
			selectionCoordinator.select(card);
		}
	}

	/** Called after a card is added from the details panel: refreshes grid (hides cards at max copies) and details. */
	public void refreshAfterDeckChange() {
		refreshGrid();
		callDeferred(StringNames.toGodotName("_deferred_refresh_deck_count_label"));
	}

	@RegisterFunction
	public void _deferred_refresh_deck_count_label() {
		updateDeckCardCountLabel();
	}

	private void updateDeckCardCountLabel() {
		if (deckCardCountLabelNode == null) {
			deckCardCountLabelNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/DeckInfoBar/DeckCardCountLabel");
		}
		if (deckCardCountLabelNode == null) {
			return;
		}
		deckCardCountLabelNode.setText(DeckState.buildDeckStatusLabel());
	}

	private void refreshGrid() {
		updateDeckCardCountLabel();
		if (queryService == null || uiBinder == null || gridRenderer == null) {
			return;
		}

		List<BaseCarte> cards = new ArrayList<>(queryService.queryCards(
				uiBinder.getSearchText(),
				uiBinder.getSelectedType(),
				uiBinder.getSelectedSort()
		));
		if (DeckState.isDeckFull()) {
			cards.clear();
		}
		cards.removeIf(c -> DeckState.isAtOrOverMax(c.getId()));
		GD.INSTANCE.print("[DeckBuilder] refreshGrid -> cards (max copies hidden): " + cards.size());
		gridRenderer.render(cards);
		logScrollState("after-render");
		if (emptyStateNode != null) {
			emptyStateNode.setVisible(cards.isEmpty());
		}
		if (selectionCoordinator != null) {
			selectionCoordinator.sync(cards);
		}
	}

	private DeckBuilderCardDetailsPanelController getDetailsController() {
		if (detailsPanelNode instanceof DeckBuilderCardDetailsPanelController detailsController) {
			return detailsController;
		}
		return null;
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
	public void _on_back_button_pressed() {
		Error err = getTree().changeSceneToFile(START_MENU_SCENE_PATH);
		GD.INSTANCE.print("[DeckBuilder] Back pressed -> " + START_MENU_SCENE_PATH + " | result: " + err);
	}

	@RegisterFunction
	public void _on_back_to_deck_button_pressed() {
		GD.INSTANCE.print("[DeckBuilder] Back to Deck pressed -> defer scene change");
		callDeferred(StringNames.toGodotName("_deferred_change_scene_to_deck"));
	}

	@RegisterFunction
	public void _deferred_change_scene_to_deck() {
		Error err = getTree().changeSceneToFile(DECK_SCENE_PATH);
		GD.INSTANCE.print("[DeckBuilder] Back to Deck -> " + DECK_SCENE_PATH + " | result: " + err);
	}

	@RegisterFunction
	public void debugDeferredScrollProbe() {
		if (cardGridScrollNode == null) {
			GD.INSTANCE.printErr("[DeckBuilder][Debug] cardGridScrollNode is null in deferred probe.");
			return;
		}
		int before = cardGridScrollNode.getVScroll();
		cardGridScrollNode.setVScroll(before + 200);
		int after = cardGridScrollNode.getVScroll();
		GD.INSTANCE.print("[DeckBuilder][DeferredProbe] vScroll " + before + " -> " + after);
		logScrollState("deferred-probe");
	}

	private void logScrollState(String context) {
		if (cardGridScrollNode == null) {
			GD.INSTANCE.printErr("[DeckBuilder][ScrollState][" + context + "] Scroll node is null.");
			return;
		}
		GD.INSTANCE.print(
				"[DeckBuilder][ScrollState][" + context + "] vScroll=" + cardGridScrollNode.getVScroll()
		);
	}
}
