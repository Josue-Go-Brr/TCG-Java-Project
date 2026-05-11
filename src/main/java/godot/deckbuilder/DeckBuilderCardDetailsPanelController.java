package godot.deckbuilder;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Button;
import godot.api.Label;
import godot.api.PanelContainer;
import godot.api.ResourceLoader;
import godot.api.RichTextLabel;
import godot.api.Texture2D;
import godot.api.TextureRect;
import godot.cards.BaseCarte;
import godot.cards.CarteMonster;
import godot.core.Callable;
import godot.core.StringNames;
import godot.deck.DeckState;

@RegisterClass
public class DeckBuilderCardDetailsPanelController extends PanelContainer {
	private TextureRect cardImageNode;
	private Label cardNameNode;
	private Label cardTypeNode;
	private Label cardCostNode;
	private Label cardMonsterTypeNode;
	private Label copiesInDeckLabelNode;
	private RichTextLabel cardDescriptionNode;
	private Button addCardButtonNode;

	private Label overlayStatsNode;
	private Label overlayNameNode;
	private Label overlayCostNode;

	private BaseCarte currentDetailCard;
	private DeckBuilderScreenController deckBuilderScreen;

	@RegisterFunction
	@Override
	public void _ready() {
		cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
		overlayStatsNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardStats");
		overlayNameNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardNameonCard");
		overlayCostNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardCostValue");
		cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");
		cardTypeNode = (Label) getNodeOrNull("Margin/Content/CardType");
		cardCostNode = (Label) getNodeOrNull("Margin/Content/CardCost");
		cardMonsterTypeNode = (Label) getNodeOrNull("Margin/Content/CardMonsterType");
		copiesInDeckLabelNode = (Label) getNodeOrNull("Margin/Content/CopiesInDeckLabel");
		cardDescriptionNode = (RichTextLabel) getNodeOrNull("Margin/Content/CardDescription");
		addCardButtonNode = (Button) getNodeOrNull("Margin/Content/AddCardButton");

		if (addCardButtonNode != null) {
			addCardButtonNode.connect(
					"pressed",
					Callable.create(this, StringNames.toGodotName("_on_add_card_button_pressed")),
					0
			);
		}

		clearSelection();
	}

	public void bindDeckBuilderScreen(DeckBuilderScreenController screen) {
		this.deckBuilderScreen = screen;
	}

	public void showCard(BaseCarte card) {
		currentDetailCard = card;
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
		if (cardMonsterTypeNode != null) {
			cardMonsterTypeNode.setText(buildMonsterTypeText(card));
		}
		if (cardDescriptionNode != null) {
			cardDescriptionNode.setText(card.getDescription());
		}
		if (overlayNameNode != null) overlayNameNode.setText(card.getName());
		if (overlayCostNode != null) overlayCostNode.setText(String.valueOf(card.getCost()));
		if (overlayStatsNode != null) {
			if (card instanceof godot.cards.CarteMonster monster) {
				overlayStatsNode.setText("ATK: " + monster.getAttack() + "\nDEF: " + monster.getDefense());
			} else {
				overlayStatsNode.setText("ATK: -\nDEF: -");
			}
		}
		applyCopiesAndAddButtonUi();
	}

	public void applyDeckCountsToUi() {
		if (currentDetailCard != null) {
			showCard(currentDetailCard);
		} else {
			clearSelection();
		}
	}

	public void clearSelection() {
		currentDetailCard = null;
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
		if (copiesInDeckLabelNode != null) {
			copiesInDeckLabelNode.setText("Copies in deck: 0");
		}
		if (cardDescriptionNode != null) {
			cardDescriptionNode.setText("Description");
		}
		if (addCardButtonNode != null) {
			addCardButtonNode.setDisabled(true);
		}
		if (overlayNameNode != null) overlayNameNode.setText("Name");
		if (overlayCostNode != null) overlayCostNode.setText("1");
		if (overlayStatsNode != null) overlayStatsNode.setText("ATK: -\nDEF: -");
	}

	@RegisterFunction
	public void _on_add_card_button_pressed() {
		if (currentDetailCard == null) {
			return;
		}
		if (!DeckState.tryAddOneCopy(currentDetailCard.getId())) {
			return;
		}
		if (deckBuilderScreen != null) {
			deckBuilderScreen.refreshAfterDeckChange();
		} else {
			applyCopiesAndAddButtonUi();
		}
	}

	private void applyCopiesAndAddButtonUi() {
		if (currentDetailCard == null) {
			return;
		}
		int copies = DeckState.getCopies(currentDetailCard.getId());
		if (copiesInDeckLabelNode != null) {
			copiesInDeckLabelNode.setText("Copies in deck: " + copies);
		}
		if (addCardButtonNode != null) {
			addCardButtonNode.setDisabled(
					copies >= DeckState.getMaxCopiesPerCard()
							|| DeckState.isDeckFull()
			);
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
