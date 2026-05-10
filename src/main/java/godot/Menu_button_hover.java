package godot;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.AudioStreamPlayer;

@RegisterClass
public class Menu_button_hover extends AudioStreamPlayer{

	Settings set;

	@RegisterFunction
	@Override
	public void _ready(){
		set = (Settings) getNode("res://scene/settings.tscn");
		setVolumeDb(set.sfxVolume);
	}

	@RegisterFunction
	public void _on_play_button_mouse_entered(){
		play();
	}

	@RegisterFunction
	public void _on_deck_button_mouse_entered(){
		play();
	}

	@RegisterFunction
	public void _on_library_button_mouse_entered(){
		play();
	}

	@RegisterFunction
	public void _on_quit_button_mouse_entered(){
		play();
	}

	@RegisterFunction
	public void _on_settings_button_mouse_entered(){
		play();
	}

}
