package godot.library;

import godot.CardDB;


import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Control;
import godot.api.GridContainer;
import godot.api.Label;
import godot.api.LineEdit;
import godot.api.Node;
import godot.api.OptionButton;
import godot.api.PanelContainer;
import godot.api.PackedScene;
import godot.api.Texture2D;
import godot.api.TextureRect;
import godot.cards.BaseCarte;
import godot.global.GD;
import godot.global.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

@RegisterClass
public class LibraryScreenController extends Control {
	private static final String CARD_TILE_SCENE_PATH = "res://scene/Library/card_tile.tscn";

	private LineEdit searchInputNode;
	private OptionButton typeFilterNode;
	private OptionButton sortFilterNode;
	private GridContainer cardGridNode;
	private Label emptyStateNode;
	private PanelContainer detailsPanelNode;

	private LibraryQueryService queryService;

	//CardDB cardDB=getNodeOrNull("/root/main/CardDB");
	//if (cardDB == null) {
	//	GD.INSTANCE.printErr("CardDB not found. Library will be empty.");
	//}
	//queryService = new LibraryQueryService(cardDB);

	private final List<CardTileController> tileControllers = new ArrayList<>();
	private PackedScene cardTileScene;
	private BaseCarte selectedCard;

	@RegisterFunction
	@Override
	public void _ready() {
		GD.INSTANCE.print("[Library] _ready entered");
		searchInputNode = getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SearchInput");
		typeFilterNode = getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/TypeFilter");
		sortFilterNode = getNodeOrNull("RootMargin/MainColumns/LeftSide/TopBar/SortFilter");
		cardGridNode = getNodeOrNull("RootMargin/MainColumns/LeftSide/CardGridScroll/CardArea/CardGrid");
		emptyStateNode = getNodeOrNull("RootMargin/MainColumns/LeftSide/EmptyState");
		detailsPanelNode = getNodeOrNull("RootMargin/MainColumns/RightSideDetails");

		// Always prepare dropdown UI first.
		setupFilterOptions();

		CardDB cardDB = resolveCardDB();
		if (cardDB == null) {
			GD.INSTANCE.printErr("CardDB not found. Library will be empty.");
		}
		queryService = new LibraryQueryService(cardDB);
		cardTileScene = ResourceLoader.load(CARD_TILE_SCENE_PATH, "PackedScene", ResourceLoader.CacheMode.REUSE);
		GD.INSTANCE.print("[Library] nodes -> search:" + (searchInputNode != null)
				+ " type:" + (typeFilterNode != null)
				+ " sort:" + (sortFilterNode != null)
				+ " grid:" + (cardGridNode != null)
				+ " detailsNode:" + (detailsPanelNode != null)
				+ " tileScene:" + (cardTileScene != null));
		refreshGrid();
	}

	@RegisterFunction
	public void _on_search_input_text_changed(String newText) {
		refreshGrid();
	}

	@RegisterFunction
	public void _on_type_filter_item_selected(int index) {
		refreshGrid();
	}

	@RegisterFunction
	public void _on_sort_filter_item_selected(int index) {
		refreshGrid();
	}

	public void onCardTileClicked(BaseCarte card) {
		selectedCard = card;
		CardDetailsPanelController detailsPanelController = getDetailsController();
		if (detailsPanelController != null) {
			detailsPanelController.showCard(card);
		}
	}

	private void setupFilterOptions() {
		if (typeFilterNode != null) {
			typeFilterNode.clear();
			typeFilterNode.addItem(LibraryQueryService.TYPE_ALL);
			typeFilterNode.addItem(LibraryQueryService.TYPE_MONSTER);
			typeFilterNode.addItem(LibraryQueryService.TYPE_MAGIE);
			typeFilterNode.addItem(LibraryQueryService.TYPE_TRAP);
			typeFilterNode.select(0);
		}

		if (sortFilterNode != null) {
			sortFilterNode.clear();
			sortFilterNode.addItem(LibraryQueryService.SORT_NAME);
			sortFilterNode.addItem(LibraryQueryService.SORT_COST);
			sortFilterNode.addItem(LibraryQueryService.SORT_ATK);
			sortFilterNode.select(0);
		}
	}

	private void refreshGrid() {
		if (cardGridNode == null) {
			GD.INSTANCE.pushWarning("[Library] CardGrid node not found, cannot render tiles.");
			return;
		}
		if (queryService == null){
			GD.INSTANCE.pushWarning("[Library] Query service not initialized");
			return;
		}
		
		List<BaseCarte> cards = queryService.queryCards(getSearchText(), getSelectedTypeFilter(), getSelectedSortFilter());
		GD.INSTANCE.print("[Library] refreshGrid cards count = " + cards.size());
		rebuildGrid(cards);
		updateEmptyState(cards.isEmpty());

		if (cards.isEmpty()) {
			selectedCard = null;
			CardDetailsPanelController detailsPanelController = getDetailsController();
			if (detailsPanelController != null) {
				detailsPanelController.clearSelection();
			}
			return;
		}

		if (selectedCard == null || cards.stream().noneMatch(card -> card.getId() == selectedCard.getId())) {
			selectedCard = cards.get(0);
		}
		CardDetailsPanelController detailsPanelController = getDetailsController();
		if (detailsPanelController != null) {
			detailsPanelController.showCard(selectedCard);
		}
	}

	private void rebuildGrid(List<BaseCarte> cards) {
		for (CardTileController tileController : tileControllers) {
			if (tileController != null) {
				tileController.queueFree();
			}
		}
		tileControllers.clear();

		if (cardTileScene == null) {
			GD.INSTANCE.pushWarning("[Library] card_tile.tscn failed to load.");
			return;
		}

		for (BaseCarte card : cards) {
			Node nodeInstance = cardTileScene.instantiate();
			if (nodeInstance == null) {
				continue;
			}
			cardGridNode.addChild(nodeInstance);

			if (nodeInstance instanceof CardTileController tileController) {
				tileController.setCardData(card);
				tileController.setLibraryScreenController(this);
				tileControllers.add(tileController);
			} else {
				// Fallback in case Godot instantiates as base node type.
				Label nameNode = nodeInstance.getNodeOrNull("Margin/Content/CardName");
				TextureRect imageNode = nodeInstance.getNodeOrNull("Margin/Content/CardImage");
				if (nameNode != null) {
					nameNode.setText(card.getName());
				}
				if (imageNode != null) {
					imageNode.setTexture(loadTexture(card.getImagePath()));
				}
				GD.INSTANCE.pushWarning("[Library] Tile instance is not CardTileController for card: " + card.getName());
			}
		}
	}

	private void updateEmptyState(boolean isEmpty) {
		if (emptyStateNode != null) {
			emptyStateNode.setVisible(isEmpty);
		}
	}

	private String getSearchText() {
		return searchInputNode == null ? "" : searchInputNode.getText();
	}

	private String getSelectedTypeFilter() {
		if (typeFilterNode == null || typeFilterNode.getItemCount() == 0) {
			return LibraryQueryService.TYPE_ALL;
		}
		return typeFilterNode.getItemText(typeFilterNode.getSelected());
	}

	private String getSelectedSortFilter() {
		if (sortFilterNode == null || sortFilterNode.getItemCount() == 0) {
			return LibraryQueryService.SORT_NAME;
		}
		return sortFilterNode.getItemText(sortFilterNode.getSelected());
	}

	private Texture2D loadTexture(String path) {
		if (path == null || path.isBlank()) {
			return null;
		}
		return ResourceLoader.load(path, "Texture2D", ResourceLoader.CacheMode.REUSE);
	}

	private CardDetailsPanelController getDetailsController() {
		if (detailsPanelNode instanceof CardDetailsPanelController detailsPanelController) {
			return detailsPanelController;
		}
		return null;
	}

	private CardDB resolveCardDB() {
		CardDB local = getNodeOrNull("CardDB");
		if (local != null) {
			return local;
		}

		CardDB mainPath = getNodeOrNull("/root/main/CardDB");
		if (mainPath != null) {
			return mainPath;
		}

		return getNodeOrNull("/root/CardDB");
	}
}
