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
import godot.core.Error;
import godot.core.StringNames;
import godot.deck.DeckState;
import godot.global.GD;

@RegisterClass
public class DeckBuilderCardDetailsPanelController extends PanelContainer {
	private TextureRect cardImageNode;
	private Label cardNameNode;
	private Label cardTypeNode;
	private Label cardCostNode;
	private Label copiesInDeckLabelNode;
	private RichTextLabel cardDescriptionNode;
	private Button addCardButtonNode;

	private BaseCarte currentDetailCard;
	private DeckBuilderScreenController deckBuilderScreen;

	@RegisterFunction
	@Override
	public void _ready() {
		cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
		cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");
		cardTypeNode = (Label) getNodeOrNull("Margin/Content/CardType");
		cardCostNode = (Label) getNodeOrNull("Margin/Content/CardCost");
		copiesInDeckLabelNode = (Label) getNodeOrNull("Margin/Content/CopiesInDeckLabel");
		cardDescriptionNode = (RichTextLabel) getNodeOrNull("Margin/Content/CardDescription");
		addCardButtonNode = (Button) getNodeOrNull("Margin/Content/AddCardButton");

		if (addCardButtonNode != null) {
			Error err = addCardButtonNode.connect(
					"pressed",
					Callable.create(this, StringNames.toGodotName("_on_add_card_button_pressed")),
					0
			);
			if (err != Error.OK) {
				GD.INSTANCE.printErr("[DeckBuilder][Details] Failed to connect Add card: " + err);
			}
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
		if (cardDescriptionNode != null) {
			cardDescriptionNode.setText(card.getDescription());
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
		if (copiesInDeckLabelNode != null) {
			copiesInDeckLabelNode.setText("Copies in deck: 0");
		}
		if (cardDescriptionNode != null) {
			cardDescriptionNode.setText("Description");
		}
		if (addCardButtonNode != null) {
			addCardButtonNode.setDisabled(true);
		}
	}

	@RegisterFunction
	public void _on_add_card_button_pressed() {
		if (currentDetailCard == null) {
			return;
		}
		if (!DeckState.tryAddOneCopy(currentDetailCard.getId())) {
			if (DeckState.isDeckFull()) {
				GD.INSTANCE.print("[DeckBuilder][Details] Deck already at max size (" + DeckState.getMaxDeckSize() + ").");
			} else {
				GD.INSTANCE.print("[DeckBuilder][Details] Already at max copies for this card.");
			}
			return;
		}
		GD.INSTANCE.print("[DeckBuilder][Details] Added copy; id=" + currentDetailCard.getId());
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
