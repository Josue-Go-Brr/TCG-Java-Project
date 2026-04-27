package godot.library;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.InputEvent;
import godot.api.InputEventMouseButton;
import godot.api.Label;
import godot.api.MouseButton;
import godot.api.PanelContainer;
import godot.api.Texture2D;
import godot.api.TextureRect;
import godot.cards.BaseCarte;
import godot.global.ResourceLoader;

@RegisterClass
public class CardTileController extends PanelContainer {
	private TextureRect cardImageNode;
	private Label cardNameNode;
	private BaseCarte cardData;
	private LibraryScreenController libraryScreenController;

	@RegisterFunction
	@Override
	public void _ready() {
		cardImageNode = getNodeOrNull("Margin/Content/CardImage");
		cardNameNode = getNodeOrNull("Margin/Content/CardName");
	}

	public void setCardData(BaseCarte card) {
		cardData = card;
		if (cardNameNode != null) {
			cardNameNode.setText(card.getName());
		}
		if (cardImageNode != null) {
			cardImageNode.setTexture(loadTexture(card.getImagePath()));
		}
		setTooltipText(card.getName());
	}

	public BaseCarte getCardData() {
		return cardData;
	}

	public void setLibraryScreenController(LibraryScreenController controller) {
		libraryScreenController = controller;
	}

	@RegisterFunction
	public void _gui_input(InputEvent event) {
		if (!(event instanceof InputEventMouseButton mouseEvent)) {
			return;
		}
		if (mouseEvent.getButtonIndex() != MouseButton.LEFT || !mouseEvent.isPressed()) {
			return;
		}
		if (libraryScreenController != null && cardData != null) {
			libraryScreenController.onCardTileClicked(cardData);
		}
	}

	private Texture2D loadTexture(String path) {
		if (path == null || path.isBlank()) {
			return null;
		}
		return ResourceLoader.load(path, "Texture2D", ResourceLoader.CacheMode.REUSE);
	}
}
