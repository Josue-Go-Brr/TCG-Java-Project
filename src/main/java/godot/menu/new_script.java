package godot.menu;

import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.api.Control;
import godot.api.TextureButton;
import godot.core.NodePath;

@RegisterClass
public class i  extends Control {

	private static final NodePath PLAY_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/PlayButton");

	private static final NodePath LIBRARY_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/LibraryButton");

	private static final NodePath TUT_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/TUTButton");

	private static final NodePath QUIT_BUTTON_PATH =
			new NodePath("CenterContainer/ButtonStack/QuitButton");

	private static final String PLAY_SCENE_PATH =
			"res://scene/Board/terrain_screen.tscn";

	private static final String LIBRARY_SCENE_PATH =
			"res://scene/Library/library_screen.tscn";

	private static final String TUTORIAL_SCENE_PATH =
			"res://scene/tutorial.tscn";

	@RegisterFunction
	@Override
	public void _ready() {
		System.out.println("[StartMenu] _ready called");

		TextureButton playButton = (TextureButton) getNodeOrNull(PLAY_BUTTON_PATH);
		TextureButton libraryButton = (TextureButton) getNodeOrNull(LIBRARY_BUTTON_PATH);
		TextureButton tutButton = (TextureButton) getNodeOrNull(TUT_BUTTON_PATH);
		TextureButton quitButton = (TextureButton) getNodeOrNull(QUIT_BUTTON_PATH);

		if (playButton != null) {
			playButton.connect("pressed", this, "onPlayPressed");
			System.out.println("[StartMenu] PlayButton connected");
		} else {
			System.out.println("[StartMenu] PlayButton not found");
		}

		if (libraryButton != null) {
			libraryButton.connect("pressed", this, "onLibraryPressed");
			System.out.println("[StartMenu] LibraryButton connected");
		} else {
			System.out.println("[StartMenu] LibraryButton not found");
		}

		if (tutButton != null) {
			tutButton.connect("pressed", this, "onTutPressed");
			System.out.println("[StartMenu] TUTButton connected");
		} else {
			System.out.println("[StartMenu] TUTButton not found");
		}

		if (quitButton != null) {
			quitButton.connect("pressed", this, "onQuitPressed");
			System.out.println("[StartMenu] QuitButton connected");
		} else {
			System.out.println("[StartMenu] QuitButton not found");
		}
	}

	@RegisterFunction
	public void onPlayPressed() {
		System.out.println("[StartMenu] Play pressed");
		getTree().changeSceneToFile(PLAY_SCENE_PATH);
	}

	@RegisterFunction
	public void onLibraryPressed() {
		System.out.println("[StartMenu] Library pressed");
		getTree().changeSceneToFile(LIBRARY_SCENE_PATH);
	}

	@RegisterFunction
	public void onTutPressed() {
		System.out.println("[StartMenu] Tutorial pressed");
		getTree().changeSceneToFile(TUTORIAL_SCENE_PATH);
	}

	@RegisterFunction
	public void onQuitPressed() {
		System.out.println("[StartMenu] Quit pressed");
		getTree().quit();
	}
}
