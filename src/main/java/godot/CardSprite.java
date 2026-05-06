package godot;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Sprite2D;
import godot.api.TextureRect;

@RegisterClass
public class CardSprite extends Sprite2D {

	@Override
	@RegisterFunction
	public void _ready(){

	}

	@RegisterFunction
	public void updateFromCard(Card thisCard){
		setTexture(thisCard.cardSprite);
	}

}
