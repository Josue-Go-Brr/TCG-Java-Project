package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.core.Dictionary;
import godot.core.Signal;
import godot.core.VariantArray;
import godot.core.Vector2;
import godot.global.GD;

import java.lang.Object;

@RegisterClass
public class Card extends Node2D {


	@RegisterProperty @Export public String cardID = "";
	@RegisterProperty @Export public int atk;
	@RegisterProperty @Export public int defense;
	public Texture2D cardSprite;
	public CardDB db;		// Attribut DATABASE
	public CardData data;		// Attribut de Données d'une carte

	@RegisterFunction
	@Override
	public void _ready(){

		db = (CardDB) getNode("/root/main/CardDB");		// Récupération de la DATABASE

//		if (db == null){		// Affiche si la BDD n'est pas trouvée
//			GD.INSTANCE.print("Database not found !");
//			return;
//		}

		data = db.getCard(cardID);		// Récupére les données de la carte à partir de son ID (définir l'ID dans le Node à la main)

		if (data != null){

			atk = data.atk;		// Récupère les infos de la carte avec data.attribut
			defense = data.defense;
			cardSprite = data.image;

			GD.INSTANCE.print("ATK and DEF LOADED : " + atk + " " + defense);

			updateLabel();
			updateSprite();
		}
		else {
			GD.INSTANCE.print("Card not found for id :" + cardID);
		}

	}

	@RegisterFunction
	public String getCardID(){
		return cardID;
	}

	@RegisterFunction
	public void updateLabel(){
		CardLabel label = (CardLabel) getNode("CardLabel");

		if (label != null){
			label.updateFromCard(this);
		}
		else {
			GD.INSTANCE.print("Label not found");
		}
	}

	@RegisterFunction
	public void updateSprite(){
		CardSprite sprite = (CardSprite) getNode("CardSprite");

		if (sprite != null){
			sprite.updateFromCard(this);
		}
		else {
			GD.INSTANCE.print("Sprite not found");
		}

	}

	@RegisterFunction
	public void _on_area_2d_mouse_entered() {
		setScale(new Vector2(1.05, 1.05));
		this.setZIndex(2);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_exited() {
		setScale(new Vector2(1, 1));
		this.setZIndex(1);
	}

}
