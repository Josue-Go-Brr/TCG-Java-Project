package godot;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.AudioStreamPlayer;

@RegisterClass
public class Get_back_button_sound extends AudioStreamPlayer {

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
