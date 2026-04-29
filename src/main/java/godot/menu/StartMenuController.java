package godot.menu;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Control;
import godot.api.TextureButton;
import godot.core.Callable;
import godot.core.Error;
import godot.core.NodePath;
import godot.core.StringNames;
import godot.global.GD;

@RegisterClass
public class StartMenuController extends Control {

	private static final NodePath PLAY_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/PlayButton");
	private static final NodePath LIBRARY_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/LibraryButton");
	private static final NodePath TUT_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/TUTButton");
	private static final NodePath QUIT_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/QuitButton");

	private static final String PLAY_SCENE_PATH = "res://scene/main.tscn";
	private static final String LIBRARY_SCENE_PATH = "res://scene/Library/library_screen.tscn";

	@RegisterFunction
	@Override
	public void _ready() {
		GD.INSTANCE.print("[StartMenu] _ready");

		TextureButton playButton = (TextureButton) getNodeOrNull(PLAY_BUTTON_PATH);
		TextureButton libraryButton = (TextureButton) getNodeOrNull(LIBRARY_BUTTON_PATH);
		TextureButton tutButton = (TextureButton) getNodeOrNull(TUT_BUTTON_PATH);
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
		if (tutButton != null) {
			tutButton.connect(
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
		Error err = getTree().changeSceneToFile(PLAY_SCENE_PATH);
		GD.INSTANCE.print("[StartMenu] Play pressed -> " + PLAY_SCENE_PATH + " | result: " + err);
	}

	@RegisterFunction
	public void onLibraryPressed() {
		Error err = getTree().changeSceneToFile(LIBRARY_SCENE_PATH);
		GD.INSTANCE.print("[StartMenu] Library pressed -> " + LIBRARY_SCENE_PATH + " | result: " + err);
	}

	@RegisterFunction
	public void onTutPressed() {
		GD.INSTANCE.print("Tutorial button: no scene connected yet.");
	}

	@RegisterFunction
	public void onQuitPressed() {
		getTree().quit();
	}
}
