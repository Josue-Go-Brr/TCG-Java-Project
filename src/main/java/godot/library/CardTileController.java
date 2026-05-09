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
import godot.cards.CarteMonster;
import godot.core.MouseButton;

@RegisterClass
public class CardTileController extends PanelContainer {
	private TextureRect cardImageNode;
	private Label cardNameNode;
	private Label cardStatsNode;
	private Label cardCostValueNode;
	private BaseCarte cardData;
	private LibraryScreenController libraryScreenController;

	@RegisterFunction
	@Override
	public void _ready() {
		cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
		cardStatsNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardStats");
		cardCostValueNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardCostValue");
		cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");
		ensureOverlayLabels();

		
		setMouseFilter(Control.MouseFilter.PASS);
		if (cardImageNode != null) {
			cardImageNode.setMouseFilter(Control.MouseFilter.IGNORE);
		}
		if (cardNameNode != null) {
			cardNameNode.setMouseFilter(Control.MouseFilter.IGNORE);
		}
		if (cardStatsNode != null) {
			cardStatsNode.setMouseFilter(Control.MouseFilter.IGNORE);
		}
		if (cardCostValueNode != null) {
			cardCostValueNode.setMouseFilter(Control.MouseFilter.IGNORE);
		}
	}

	public void setCardData(BaseCarte card) {
		cardData = card;
		if (cardStatsNode == null || cardCostValueNode == null) {
			ensureOverlayLabels();
		}
		if (cardNameNode != null) {
			cardNameNode.setText(card.getName());
		}
		if (cardImageNode != null) {
			cardImageNode.setTexture(resolveTexture(card));
		}
		if (cardStatsNode != null) {
			cardStatsNode.setText(buildStatsText(card));
		}
		if (cardCostValueNode != null) {
			cardCostValueNode.setText(String.valueOf(card.getCost()));
		}
		setTooltipText(card.getName());
	}

	private String buildStatsText(BaseCarte card) {
		if (card instanceof CarteMonster monster) {
			return "ATK: " + monster.getAttack() + "\nDEF: " + monster.getDefense();
		}
		return "ATK: -\nDEF: -";
	}

	private void ensureOverlayLabels() {
		cardStatsNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardStats");
		cardCostValueNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardCostValue");
		if (cardStatsNode != null && cardCostValueNode != null) {
			return;
		}
		if (cardImageNode == null) {
			cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
		}
		if (cardImageNode == null) {
			return;
		}

		Control overlay = (Control) cardImageNode.getNodeOrNull("Overlay");
		if (overlay == null) {
			overlay = new Control();
			overlay.setName("Overlay");
			overlay.setMouseFilter(Control.MouseFilter.IGNORE);
			overlay.set("anchor_right", 1.0);
			overlay.set("anchor_bottom", 1.0);
			overlay.set("offset_left", 0.0);
			overlay.set("offset_top", 0.0);
			overlay.set("offset_right", 0.0);
			overlay.set("offset_bottom", 0.0);
			cardImageNode.addChild(overlay);
		}

		cardStatsNode = (Label) overlay.getNodeOrNull("CardStats");
		if (cardStatsNode == null) {
			cardStatsNode = new Label();
			cardStatsNode.setName("CardStats");
			cardStatsNode.setPosition(new godot.core.Vector2(14, 260));
			cardStatsNode.setCustomMinimumSize(new godot.core.Vector2(156, 40));
			cardStatsNode.set("theme_override_font_sizes/font_size", 15);
			cardStatsNode.setText("ATK: -\nDEF: -");
			cardStatsNode.setMouseFilter(Control.MouseFilter.IGNORE);
			overlay.addChild(cardStatsNode);
		}

		cardCostValueNode = (Label) overlay.getNodeOrNull("CardCostValue");
		if (cardCostValueNode == null) {
			cardCostValueNode = new Label();
			cardCostValueNode.setName("CardCostValue");
			cardCostValueNode.setPosition(new godot.core.Vector2(186, 268));
			cardCostValueNode.setCustomMinimumSize(new godot.core.Vector2(40, 32));
			cardCostValueNode.set("horizontal_alignment", 1);
			cardCostValueNode.set("vertical_alignment", 1);
			cardCostValueNode.set("theme_override_font_sizes/font_size", 24);
			cardCostValueNode.setText("-");
			cardCostValueNode.setMouseFilter(Control.MouseFilter.IGNORE);
			overlay.addChild(cardCostValueNode);
		}
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
