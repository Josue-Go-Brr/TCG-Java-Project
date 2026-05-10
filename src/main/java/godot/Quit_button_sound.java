package godot;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.AudioStreamPlayer;

@RegisterClass
public class Quit_button_sound extends AudioStreamPlayer{

	@RegisterFunction
	public void _on_quit_button_pressed(){
		play();
	}

}
