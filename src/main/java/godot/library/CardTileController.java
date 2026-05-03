package godot.library;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.InputEvent;
import godot.api.InputEventMouseButton;
import godot.api.Label;
import godot.api.Control;
import godot.api.PanelContainer;
import godot.api.ResourceLoader;
import godot.api.Texture2D;
import godot.api.TextureRect;
import godot.cards.BaseCarte;
import godot.core.MouseButton;

@RegisterClass
public class CardTileController extends PanelContainer {
	private TextureRect cardImageNode;
	private Label cardNameNode;
	private BaseCarte cardData;
	private LibraryScreenController libraryScreenController;

	@RegisterFunction
	@Override
	public void _ready() {
		cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
		cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");

		
		setMouseFilter(Control.MouseFilter.PASS);
		if (cardImageNode != null) {
			cardImageNode.setMouseFilter(Control.MouseFilter.IGNORE);
		}
		if (cardNameNode != null) {
			cardNameNode.setMouseFilter(Control.MouseFilter.IGNORE);
		}
	}

	public void setCardData(BaseCarte card) {
		cardData = card;
		if (cardNameNode != null) {
			cardNameNode.setText(card.getName());
		}
		if (cardImageNode != null) {
			cardImageNode.setTexture(resolveTexture(card));
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
		return (Texture2D) ResourceLoader.load(path, "Texture2D", ResourceLoader.CacheMode.REUSE);
	}

	private Texture2D resolveTexture(BaseCarte card) {
		if (card == null) {
			return null;
		}
		if (card.getImage() != null) {
			return card.getImage();
		}
		return loadTexture(card.getImagePath());
	}
}
