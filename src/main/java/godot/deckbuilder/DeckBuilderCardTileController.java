package godot.deckbuilder;

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
public class DeckBuilderCardTileController extends PanelContainer {
    private TextureRect cardImageNode;
    private Label cardNameNode;
    private Label cardNameonCardNode;
    private Label cardStatsNode;
    private Label cardCostValueNode;
    private BaseCarte cardData;
    private DeckBuilderScreenController deckBuilderScreenController;

    @RegisterFunction
    @Override
    public void _ready() {
        cardImageNode = (TextureRect) getNodeOrNull("Margin/Content/CardImage");
        cardStatsNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardStats");
        cardCostValueNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardCostValue");
        cardNameonCardNode = (Label) getNodeOrNull("Margin/Content/CardImage/Overlay/CardNameonCard");
        cardNameNode = (Label) getNodeOrNull("Margin/Content/CardName");

        setMouseFilter(Control.MouseFilter.PASS);
        if (cardImageNode != null) cardImageNode.setMouseFilter(Control.MouseFilter.IGNORE);
        if (cardNameNode != null) cardNameNode.setMouseFilter(Control.MouseFilter.IGNORE);
        if (cardNameonCardNode != null) cardNameonCardNode.setMouseFilter(Control.MouseFilter.IGNORE);
        if (cardStatsNode != null) cardStatsNode.setMouseFilter(Control.MouseFilter.IGNORE);
        if (cardCostValueNode != null) cardCostValueNode.setMouseFilter(Control.MouseFilter.IGNORE);
    }

    public void setCardData(BaseCarte card) {
        cardData = card;

        if (cardNameNode != null) cardNameNode.setText(card.getName());
        if (cardNameonCardNode != null) cardNameonCardNode.setText(card.getName());
        if (cardImageNode != null) cardImageNode.setTexture(resolveTexture(card));
        if (cardStatsNode != null) cardStatsNode.setText(buildStatsText(card));
        if (cardCostValueNode != null) cardCostValueNode.setText(String.valueOf(card.getCost()));

        setTooltipText(card.getName());
    }

    private String buildStatsText(BaseCarte card) {
        if (card instanceof CarteMonster monster) {
            return "ATK: " + monster.getAttack() + "\nDEF: " + monster.getDefense();
        }
        return "ATK: -\nDEF: -";
    }

    public BaseCarte getCardData() {
        return cardData;
    }

    public void setDeckBuilderScreenController(DeckBuilderScreenController controller) {
        deckBuilderScreenController = controller;
    }

    @RegisterFunction
    public void _gui_input(InputEvent event) {
        if (!(event instanceof InputEventMouseButton mouseEvent)) return;
        if (mouseEvent.getButtonIndex() != MouseButton.LEFT || !mouseEvent.isPressed()) return;

        if (deckBuilderScreenController != null && cardData != null) {
            deckBuilderScreenController.onCardTileClicked(cardData);
        }
    }

    private Texture2D loadTexture(String path) {
        if (path == null || path.isBlank()) return null;
        return (Texture2D) ResourceLoader.load(path, "Texture2D", ResourceLoader.CacheMode.REUSE);
    }

    private Texture2D resolveTexture(BaseCarte card) {
        if (card == null) return null;
        if (card.getImage() != null) return card.getImage();
        return loadTexture(card.getImagePath());
    }
}
