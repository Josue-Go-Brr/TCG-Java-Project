package godot;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.AudioStreamPlayer;

@RegisterClass
public class Loop  extends AudioStreamPlayer{

	@RegisterFunction
	public void _on_intro_delirious_finished(){
		this.play();
	}

}
