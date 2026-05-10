package godot.menu;

import godot.Music_manager;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.*;
import godot.core.Vector2;

@RegisterClass
public class Cinematique_intro extends Node2D{

	public VideoStreamPlayer cinematique;
	public TextureButton bouton;
	Music_manager music;

	@RegisterFunction
	@Override
	public void _ready() {
		cinematique = (VideoStreamPlayer) getNode("Cinematique");
		bouton = (TextureButton) getNode("SkipButton");
		music = (Music_manager) getNode("/root/MusicManager");
		music.stopMusic();
	}

	@RegisterFunction
	@Override
	public void _process(double delta) {
		if (bouton.isPressed()){
			music.playMusic();
			getTree().changeSceneToFile("res://scene/menu/start_menu.tscn");
		}
		if (cinematique.isPlaying() != true){
			getTree().changeSceneToFile("res://scene/menu/start_menu.tscn");
		}
	}

}
