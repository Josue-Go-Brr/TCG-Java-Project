package godot;
import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.*;
import godot.core.*;
import godot.global.GD;

import java.lang.Object;

@RegisterClass
public class Card extends Node2D {

	@RegisterProperty @Export

	public boolean in_slot = false;

	public Vector2 starting_pos = new Vector2();
	public Vector2 hovered_off = new Vector2(0.6, 0.6);
	public Vector2 hovered_on = new Vector2(0.7, 0.7);

	public String labelID = "";
	public StringName cardID;
	public String name = "";
	public int atk;
	public int defense;
	public int cost;
	
	public Texture2D cardSprite;
	public CardDB db;		// Attribut DATABASE
	public CardData data;		// Attribut de Données d'une carte

	//this is used to get the last card instaciated
	public Node cardManager;
	public Node player_hand_ref;
	public Node card;

	@RegisterFunction
	@Override
	public void _ready(){
		player_hand_ref = getNode("../../PlayerHand");
		cardManager = getNode("../Cardmanager");
		db = (CardDB) getNode("/root/main/CardDB");	// Récupération de la DATABASE
	}


	// This fonction is used to update the card date with the help of her cardID
	@RegisterFunction
	public void updatecard() {

		//setting the card ID to instanciate the card and then modify the name to avoid conflicts
		cardID = getName();
		setName(cardID + (String.valueOf(player_hand_ref.get("cardcompteur"))));


		if (db == null){		// Affiche si la BDD n'est pas trouvée
			GD.INSTANCE.print("Database not found !");
			return;
		}

		data = db.getCard(String.valueOf(cardID));		// Récupére les données de la carte à partir de son ID (définir l'ID dans le Node à la main)

		if (data != null){

			atk = data.atk;		// Récupère les infos de la carte avec data.attribut
			defense = data.defense;
			cost = data.cost;
			cardSprite = data.image;
			name = data.name;

			updateSprite();
			updateLabel();		// Update Label et Sprite pour qu'ils s'affichent après l'exécution du script de la carte

		}
		else {
			//GD.INSTANCE.print("Card not found for id :" + cardID);
		}

	}



	@RegisterFunction
	public StringName getCardID(){
		return cardID;
	}

	@RegisterFunction
	public void updateLabel(){
		GD.INSTANCE.print("card Label updated!");

		RichTextLabel label = (RichTextLabel) getNode("CardLabelAtkDef");
		RichTextLabel  label2 = (RichTextLabel) getNode("CardLabelCost");
		RichTextLabel  label3 = (RichTextLabel) getNode("CardLabelName");

		label.setText("ATK : " + this.atk + "\nDEF : " + this.defense);
		label2.setText(String.valueOf(this.cost));
		label3.setText(this.name);

	}


	@RegisterFunction
	public void updateSprite(){

		//GD.INSTANCE.print("card sprite updated!");

		//checking if CardSprite exists
		//GD.INSTANCE.print(getNode("CardSprite").isInsideTree(), " Cardsprite");
		// GD.INSTANCE.print(getNode("CardSprite").getPropertyList(), " Cardsprite");

		// WHEN YOU WANT TO KNOW A PROPERTY HOVER AND GET Property: THISTHING: NOT THIS
		getNode("CardSprite").set("texture", cardSprite);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_entered() {
		setScale(hovered_on);
		this.setZIndex(2);
	}

	@RegisterFunction
	public void _on_area_2d_mouse_exited() {
		setScale(hovered_off);
		this.setZIndex(1);
	}

}
