package godot.deckbuilder;

import godot.CardDB;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.*;
import godot.cards.BaseCarte;
import godot.core.Callable;
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
    private OptionButton monsterTypeFilterNode;
    private OptionButton sortFilterNode;
    private OptionButton sortOrderFilterNode;
    
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

        // 1. Fetch nodes from TopBar
        searchInputNode = (LineEdit) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SearchInput");
        typeFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/TypeFilter");
        monsterTypeFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/MonsterTypeFilter");
        
        // 2. Fetch sorted buttons from the new DeckInfoBar
        sortFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/DeckInfoBar/SortFilter");
        sortOrderFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/DeckInfoBar/SortOrderFilter");

        if (sortFilterNode == null) GD.INSTANCE.printErr("ERROR: SortFilter NOT FOUND in DeckInfoBar!");
        if (sortOrderFilterNode == null) GD.INSTANCE.printErr("ERROR: SortOrderFilter NOT FOUND in DeckInfoBar!");

        // 3. Fetch remaining UI
        backButtonNode = (Button) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/BackButton");
        cardGridScrollNode = (ScrollContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll");
        cardGridNode = (GridContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll/CardArea/CardGrid");
        emptyStateNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/EmptyState");
        deckCardCountLabelNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/DeckInfoBar/DeckCardCountLabel");
        detailsPanelNode = (PanelContainer) getNodeOrNull("RootMargin/MainColumns/RightSideDetails");
        cardTileScene = (PackedScene) ResourceLoader.load(CARD_TILE_SCENE_PATH, "PackedScene", ResourceLoader.CacheMode.REUSE);

        // 4. Initialize Services
        CardDB cardDB = resolveCardDB();
        DeckState.loadSavedDeckLocal(cardDB);

        queryService = new DeckBuilderQueryService(cardDB);

        uiBinder = new DeckBuilderUiBinder(searchInputNode, typeFilterNode, monsterTypeFilterNode, sortFilterNode, sortOrderFilterNode);
        uiBinder.setupDefaultOptions();
        uiBinder.connect(this);

        connectBackButton();
        connectManualScrollFallback();

        DeckBuilderCardDetailsPanelController detailsController = getDetailsController();
        if (detailsController != null) {
            detailsController.bindDeckBuilderScreen(this);
        }

        selectionCoordinator = new DeckBuilderSelectionCoordinator(detailsController);
        gridRenderer = new DeckBuilderGridRenderer(cardGridNode, cardTileScene, this);

        refreshGrid();
        callDeferred(StringNames.toGodotName("_deferred_refresh_deck_count_label"));
    }

    @RegisterFunction
    public void _on_dropdown_item_selected(long index) {
        refreshGrid();
    }

    @RegisterFunction
    public void _on_search_input_text_changed(String newText) {
        refreshGrid();
    }

    private void refreshGrid() {
        updateDeckCardCountLabel();
        if (queryService == null || uiBinder == null || gridRenderer == null) return;

        List<BaseCarte> cards = new ArrayList<>(queryService.queryCards(
                uiBinder.getSearchText(),
                uiBinder.getSelectedType(),
                uiBinder.getSelectedMonsterType(),
                uiBinder.getSelectedSort(),
                uiBinder.getSelectedSortOrder()
        ));
        
        gridRenderer.render(cards);
        if (emptyStateNode != null) emptyStateNode.setVisible(cards.isEmpty());
        if (selectionCoordinator != null) selectionCoordinator.sync(cards);
    }

    // ----------------------------------------------------
    // --- PRESERVED LOGIC --- 
    // Do not delete anything below this line!
    // ----------------------------------------------------

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

	public void onCardTileClicked(BaseCarte card) {
		if (selectionCoordinator != null) {
			selectionCoordinator.select(card);
		}
	}

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
		getTree().changeSceneToFile(START_MENU_SCENE_PATH);
	}

	@RegisterFunction
	public void _on_back_to_deck_button_pressed() {
		callDeferred(StringNames.toGodotName("_deferred_change_scene_to_deck"));
	}

	@RegisterFunction
	public void _deferred_change_scene_to_deck() {
		getTree().changeSceneToFile(DECK_SCENE_PATH);
	}

}
