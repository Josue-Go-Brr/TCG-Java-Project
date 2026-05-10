package godot;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.AudioStreamPlayer;

@RegisterClass
public class Music_manager extends AudioStreamPlayer {

	@RegisterFunction
	@Override
	public void _ready() {
		setVolumeDb(-20);
	}

	@RegisterFunction
	public void playMusic(){

		if (!isPlaying()){
			play();
		}
	}

	@RegisterFunction
	public void stopMusic(){
		stop();
	}

}
