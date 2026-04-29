package godot.library;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Label;
import godot.api.PanelContainer;
import godot.api.ResourceLoader;
import godot.api.RichTextLabel;
import godot.api.Texture2D;
import godot.api.TextureRect;
import godot.cards.BaseCarte;
import godot.cards.CarteMonster;

@RegisterClass
public class CardDetailsPanelController extends PanelContainer {
	private TextureRect cardImageNode;
	private Label cardNameNode;
	private Label cardTypeNode;
	private Label cardCostNode;
	private RichTextLabel cardDescriptionNode;

	@RegisterFunction
	@Override
	public void _ready() {
		cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
		cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");
		cardTypeNode = (Label) getNodeOrNull("Margin/Content/CardType");
		cardCostNode = (Label) getNodeOrNull("Margin/Content/CardCost");
		cardDescriptionNode = (RichTextLabel) getNodeOrNull("Margin/Content/CardDescription");
		clearSelection();
	}

	public void showCard(BaseCarte card) {
		if (card == null) {
			clearSelection();
			return;
		}

		if (cardImageNode != null) {
			cardImageNode.setTexture(resolveTexture(card));
		}
		if (cardNameNode != null) {
			cardNameNode.setText(card.getName());
		}
		if (cardTypeNode != null) {
			cardTypeNode.setText(buildTypeText(card));
		}
		if (cardCostNode != null) {
			cardCostNode.setText("Cost: " + card.getCost());
		}
		if (cardDescriptionNode != null) {
			cardDescriptionNode.setText(card.getDescription());
		}
	}

	public void clearSelection() {
		if (cardImageNode != null) {
			cardImageNode.setTexture(null);
		}
		if (cardNameNode != null) {
			cardNameNode.setText("Select a card");
		}
		if (cardTypeNode != null) {
			cardTypeNode.setText("Type: -");
		}
		if (cardCostNode != null) {
			cardCostNode.setText("Cost: -");
		}
		if (cardDescriptionNode != null) {
			cardDescriptionNode.setText("Description");
		}
	}

	private String buildTypeText(BaseCarte card) {
		String value = card.getType();
		if (card instanceof CarteMonster) {
			CarteMonster monster = (CarteMonster) card;
			value += " | ATK " + monster.getAttack() + " DEF " + monster.getDefense();
		}
		return "Type: " + value;
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
