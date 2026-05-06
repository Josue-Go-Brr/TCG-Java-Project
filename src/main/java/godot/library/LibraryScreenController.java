package godot.library;

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
import godot.core.MouseButton;
import godot.core.StringNames;
import godot.global.GD;
import java.util.List;
import godot.library.ui.LibraryGridRenderer;
import godot.library.ui.LibrarySelectionCoordinator;
import godot.library.ui.LibraryUiBinder;

@RegisterClass
public class LibraryScreenController extends Control {
	private static final String CARD_TILE_SCENE_PATH = "res://scene/Library/card_tile.tscn";
	private static final String START_MENU_SCENE_PATH = "res://scene/menu/start_menu.tscn";
	private static final int MANUAL_SCROLL_STEP = 25;

	private LineEdit searchInputNode;
	private OptionButton typeFilterNode;
	private OptionButton sortFilterNode;
	private Button backButtonNode;
	private GridContainer cardGridNode;
	private ScrollContainer cardGridScrollNode;
	private Label emptyStateNode;
	private PanelContainer detailsPanelNode;
	private PackedScene cardTileScene;

	private LibraryQueryService queryService;
	private LibraryUiBinder uiBinder;
	private LibraryGridRenderer gridRenderer;
	private LibrarySelectionCoordinator selectionCoordinator;

	@RegisterFunction
	@Override
	public void _ready() {
		GD.INSTANCE.print("[Library] _ready entered");

		searchInputNode = (LineEdit) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SearchInput");
		typeFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/TypeFilter");
		sortFilterNode = (OptionButton) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SortFilter");
		backButtonNode = (Button) getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/BackButton");
		cardGridScrollNode = (ScrollContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll");
		cardGridNode = (GridContainer) getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll/CardArea/CardGrid");
		emptyStateNode = (Label) getNodeOrNull("RootMargin/MainColumns/LeftSide/EmptyState");
		detailsPanelNode = (PanelContainer) getNodeOrNull("RootMargin/MainColumns/RightSideDetails");
		cardTileScene = (PackedScene) ResourceLoader.load(
				CARD_TILE_SCENE_PATH,
				"PackedScene",
				ResourceLoader.CacheMode.REUSE
		);

		CardDB cardDB = resolveCardDB();
		queryService = new LibraryQueryService(cardDB);

		uiBinder = new LibraryUiBinder(searchInputNode, typeFilterNode, sortFilterNode);
		uiBinder.setupDefaultOptions();
		uiBinder.connect(this);
		connectBackButton();
		connectManualScrollFallback();

		CardDetailsPanelController detailsController = getDetailsController();
		selectionCoordinator = new LibrarySelectionCoordinator(detailsController);
		gridRenderer = new LibraryGridRenderer(cardGridNode, cardTileScene, this);

		refreshGrid();
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

	private void refreshGrid() {
		if (queryService == null || uiBinder == null || gridRenderer == null) {
			return;
		}

		List<BaseCarte> cards = queryService.queryCards(
				uiBinder.getSearchText(),
				uiBinder.getSelectedType(),
				uiBinder.getSelectedSort()
		);
		gridRenderer.render(cards);
		if (emptyStateNode != null) {
			emptyStateNode.setVisible(cards.isEmpty());
		}
		if (selectionCoordinator != null) {
			selectionCoordinator.sync(cards);
		}
	}

	private CardDetailsPanelController getDetailsController() {
		if (detailsPanelNode instanceof CardDetailsPanelController detailsController) {
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

}
