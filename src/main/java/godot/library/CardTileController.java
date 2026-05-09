package godot.library;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.FontFile;
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
	private static final String FONT_STONE_SERIF = "res://assets/fonts/TCG fonts/Stone Serif Semibold.ttf";
	private static final String FONT_COST = "res://assets/fonts/TCG fonts/Yu-Gi-Oh! ITC Stone Serif Small Caps Bold.ttf";

	private static final String NODE_CARD_IMAGE = "Margin/Content/CardImage";
	private static final String NODE_OVERLAY_STATS = "Margin/Content/CardImage/Overlay/CardStats";
	private static final String NODE_OVERLAY_COST = "Margin/Content/CardImage/Overlay/CardCostValue";
	private static final String NODE_OVERLAY_NAME = "Margin/Content/CardImage/Overlay/CardName";

	private TextureRect cardImageNode;
	private Label cardNameNode;
	private Label cardStatsNode;
	private Label cardCostValueNode;
	private BaseCarte cardData;
	private LibraryScreenController libraryScreenController;

	@RegisterFunction
	@Override
	public void _ready() {
		resolveTileNodes();
		ensureOverlayLabels();
		if (cardData != null) {
			applyOverlayValues(cardData);
		}

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
		resolveTileNodes();
		ensureOverlayLabels();
		if (cardNameNode != null) {
			cardNameNode.setText(card.getName());
		}
		if (cardImageNode != null) {
			cardImageNode.setTexture(resolveTexture(card));
		}
		applyOverlayValues(card);
		setTooltipText(card.getName());
	}

	private void resolveTileNodes() {
		if (cardImageNode == null) {
			cardImageNode = (TextureRect) getNodeOrNull(NODE_CARD_IMAGE);
		}
		if (cardStatsNode == null) {
			cardStatsNode = (Label) getNodeOrNull(NODE_OVERLAY_STATS);
		}
		if (cardCostValueNode == null) {
			cardCostValueNode = (Label) getNodeOrNull(NODE_OVERLAY_COST);
		}
		if (cardNameNode == null) {
			cardNameNode = (Label) getNodeOrNull(NODE_OVERLAY_NAME);
			if (cardNameNode == null) {
				cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");
			}
		}
	}

	private void applyOverlayValues(BaseCarte card) {
		if (card == null) {
			return;
		}
		if (cardStatsNode != null) {
			cardStatsNode.setVisible(true);
			cardStatsNode.setText(buildStatsText(card));
		}
		if (cardCostValueNode != null) {
			cardCostValueNode.setVisible(true);
			cardCostValueNode.setText(String.valueOf(card.getCost()));
		}
	}

	private String buildStatsText(BaseCarte card) {
		if (card instanceof CarteMonster monster) {
			return "ATK : " + monster.getAttack() + "\nDEF : " + monster.getDefense();
		}
		return "ATK : -\nDEF : -";
	}

	private void ensureOverlayLabels() {
		resolveTileNodes();
		if (cardStatsNode != null && cardCostValueNode != null) {
			return;
		}
		if (cardImageNode == null) {
			cardImageNode = (TextureRect) getNodeOrNull(NODE_CARD_IMAGE);
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
			cardStatsNode.setPosition(new godot.core.Vector2(24, 271));
			cardStatsNode.setCustomMinimumSize(new godot.core.Vector2(156, 47));
			applyAtkDefLabelTheme(cardStatsNode);
			cardStatsNode.setText("ATK : -\nDEF : -");
			cardStatsNode.setMouseFilter(Control.MouseFilter.IGNORE);
			overlay.addChild(cardStatsNode);
		}

		cardCostValueNode = (Label) overlay.getNodeOrNull("CardCostValue");
		if (cardCostValueNode == null) {
			cardCostValueNode = new Label();
			cardCostValueNode.setName("CardCostValue");
			cardCostValueNode.setPosition(new godot.core.Vector2(178, 278));
			cardCostValueNode.setCustomMinimumSize(new godot.core.Vector2(40, 32));
			cardCostValueNode.set("horizontal_alignment", 1);
			cardCostValueNode.set("vertical_alignment", 1);
			applyCostLabelTheme(cardCostValueNode);
			cardCostValueNode.setText("-");
			cardCostValueNode.setMouseFilter(Control.MouseFilter.IGNORE);
			overlay.addChild(cardCostValueNode);
		}
	}

	private FontFile loadFontFile(String path) {
		Object loaded = ResourceLoader.load(path, "FontFile", ResourceLoader.CacheMode.REUSE);
		return loaded instanceof FontFile ? (FontFile) loaded : null;
	}

	private void applyAtkDefLabelTheme(Label label) {
		label.set("theme_override_colors/font_color", new godot.core.Color(0, 0, 0, 1));
		FontFile font = loadFontFile(FONT_STONE_SERIF);
		if (font != null) {
			label.set("theme_override_fonts/font", font);
		}
		label.set("theme_override_font_sizes/font_size", 18);
	}

	private void applyCostLabelTheme(Label label) {
		label.set("theme_override_colors/font_color", new godot.core.Color(0.618854f, 0.748261f, 0.952736f, 1));
		label.set("theme_override_colors/font_shadow_color", new godot.core.Color(0, 0, 0, 1));
		label.set("theme_override_colors/font_outline_color", new godot.core.Color(0, 0, 0, 1));
		label.set("theme_override_constants/shadow_offset_x", 4);
		label.set("theme_override_constants/shadow_offset_y", 2);
		label.set("theme_override_constants/outline_size", 10);
		FontFile font = loadFontFile(FONT_COST);
		if (font != null) {
			label.set("theme_override_fonts/font", font);
		}
		label.set("theme_override_font_sizes/font_size", 25);
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
