package godot;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Node2D;

@RegisterClass
public class Settings extends Node2D {


	Boutons_menu_click sfx;
	Music_manager music;
	Get_back_button_sound getBack;
	@Export
	float sfxVolume;


	@RegisterFunction
	@Override
	public void _ready(){
		sfx = (Boutons_menu_click) getNode("/root/BoutonsMenuClick");
		music = (Music_manager) getNode("/root/MusicManager");
		getBack = (Get_back_button_sound) getNode("/root/GetBackButtonSound");
	}

	@RegisterFunction
	public void _on_music_slider_value_changed(double value){
		music.setVolumeDb((float) value);
	}

	@RegisterFunction
	public void _on_sfx_slider_value_changed(double value){
		sfx.setVolumeDb((float) value);
		getBack.setVolumeDb((float) value);
		sfxVolume = (float) value;
	}
	@RegisterFunction
	public void _on_get_back_button_pressed(){

		getBack.play();
		getTree().changeSceneToFile("res://scene/menu/start_menu.tscn");

	}

}
