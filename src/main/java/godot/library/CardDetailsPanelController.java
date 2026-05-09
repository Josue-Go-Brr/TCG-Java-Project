package godot.library;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Label;
import godot.api.PanelContainer;
import godot.api.ResourceLoader;
import godot.api.RichTextLabel;
import godot.api.Texture2D;
import godot.api.TextureRect;
import godot.api.VBoxContainer;
import godot.cards.BaseCarte;
import godot.cards.CarteMonster;

@RegisterClass
public class CardDetailsPanelController extends PanelContainer {
	private TextureRect cardImageNode;
	private Label cardNameNode;
	private Label cardTypeNode;
	private Label cardCostNode;
	private Label cardMonsterTypeNode;
	private RichTextLabel cardDescriptionNode;

	@RegisterFunction
	@Override
	public void _ready() {
		cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
		cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");
		cardTypeNode = (Label) getNodeOrNull("Margin/Content/CardType");
		cardCostNode = (Label) getNodeOrNull("Margin/Content/CardCost");
		cardDescriptionNode = (RichTextLabel) getNodeOrNull("Margin/Content/CardDescription");
		cardMonsterTypeNode = resolveOrCreateMonsterTypeLabel();
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
		if (cardMonsterTypeNode == null) {
			cardMonsterTypeNode = resolveOrCreateMonsterTypeLabel();
		}
		if (cardMonsterTypeNode != null) {
			cardMonsterTypeNode.setText(buildMonsterTypeText(card));
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
		if (cardMonsterTypeNode != null) {
			cardMonsterTypeNode.setText("Monster type: -");
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

	private String buildMonsterTypeText(BaseCarte card) {
		if (!(card instanceof CarteMonster monster)) {
			return "Monster type: —";
		}
		String monsterType = monster.getMonsterType();
		if (monsterType == null || monsterType.isBlank()) {
			return "Monster type: —";
		}
		return "Monster type: " + monsterType;
	}

	private Label resolveOrCreateMonsterTypeLabel() {
		Label existing = (Label) getNodeOrNull("Margin/Content/CardMonsterType");
		if (existing != null) {
			return existing;
		}

		VBoxContainer contentNode = (VBoxContainer) getNodeOrNull("Margin/Content");
		if (contentNode == null) {
			return null;
		}

		Label created = new Label();
		created.setName("CardMonsterType");
		created.setText("Monster type: -");
		created.set("theme_override_font_sizes/font_size", 25);
		contentNode.addChild(created);

		if (cardDescriptionNode != null) {
			int descriptionIndex = cardDescriptionNode.getIndex();
			contentNode.moveChild(created, descriptionIndex);
		}
		return created;
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
