package godot;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.AudioStreamPlayer;

@RegisterClass
public class Boutons_menu_click extends AudioStreamPlayer {

	@RegisterFunction
	@Override
	public void _ready() {
		//setVolumeDb(-20);
	}

	@RegisterFunction
	public void playSound(){
		if (!isPlaying()){
			play();
		}
	}

	@RegisterFunction
	public void stopSound(){
		stop();
	}

}
