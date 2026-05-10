package godot.menu;

import godot.Music_manager;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Control;
import godot.api.TextureButton;
import godot.api.Timer;
import godot.core.Callable;
import godot.core.NodePath;
import godot.core.StringNames;
import godot.global.GD;

@RegisterClass
public class StartMenuController extends Control {
	private static final NodePath PLAY_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/PlayButton");
	private static final NodePath LIBRARY_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/LibraryButton");
	private static final NodePath DECK_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/DeckButton");
	private static final NodePath SET_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/SettingsButton");
	private static final NodePath QUIT_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/QuitButton");

	private static final String PLAY_SCENE_PATH = "res://scene/main.tscn";
	private static final String LIBRARY_SCENE_PATH = "res://scene/Library/library_screen.tscn";
	private static final String DECK_SCENE_PATH = "res://scene/deck/deck_screen.tscn";

	@RegisterFunction
	@Override
	public void _ready() {

		Music_manager music = (Music_manager) getNode("/root/MusicManager");
		if (!music.isPlaying()){
			music.play();
		}
		TextureButton playButton = (TextureButton) getNodeOrNull(PLAY_BUTTON_PATH);
		TextureButton libraryButton = (TextureButton) getNodeOrNull(LIBRARY_BUTTON_PATH);
		TextureButton deckButton = (TextureButton) getNodeOrNull(DECK_BUTTON_PATH);
		TextureButton setButton = (TextureButton) getNodeOrNull(SET_BUTTON_PATH);
		TextureButton quitButton = (TextureButton) getNodeOrNull(QUIT_BUTTON_PATH);

		if (playButton != null) {
			playButton.connect(
					"pressed",
					Callable.create(this, StringNames.toGodotName("onPlayPressed")),
					0
			);
		} else {
			GD.INSTANCE.printErr("[StartMenu] PlayButton not found");
		}
		if (libraryButton != null) {
			libraryButton.connect(
					"pressed",
					Callable.create(this, StringNames.toGodotName("onLibraryPressed")),
					0
			);
		} else {
			GD.INSTANCE.printErr("[StartMenu] LibraryButton not found");
		}
		if (deckButton != null) {
			deckButton.connect(
					"pressed",
					Callable.create(this, StringNames.toGodotName("onDeckPressed")),
					0
			);
		} else {
			GD.INSTANCE.printErr("[StartMenu] DeckButton not found");
		}
		if (setButton != null) {
			setButton.connect(
					"pressed",
					Callable.create(this, StringNames.toGodotName("onTutPressed")),
					0
			);
		} else {
			GD.INSTANCE.printErr("[StartMenu] TUTButton not found");
		}
		if (quitButton != null) {
			quitButton.connect(
					"pressed",
					Callable.create(this, StringNames.toGodotName("onQuitPressed")),
					0
			);
		} else {
			GD.INSTANCE.printErr("[StartMenu] QuitButton not found");
		}
	}

	@RegisterFunction
	public void onPlayPressed() {
		getTree().changeSceneToFile(PLAY_SCENE_PATH);
	}

	@RegisterFunction
	public void onLibraryPressed() {
		getTree().changeSceneToFile(LIBRARY_SCENE_PATH);
	}

	@RegisterFunction
	public void onDeckPressed() {
		getTree().changeSceneToFile(DECK_SCENE_PATH);
	}

	@RegisterFunction
	public void onTutPressed() {
	}

	@RegisterFunction
	public void onQuitPressed() {
		getTree().quit();
	}
	
	@RegisterFunction
	public void _on_music_slider_value_changed(double value){

		Music_manager music = (Music_manager) getNode("/root/MusicManager");
		music.setVolumeDb((float) value);
		
	}
}
