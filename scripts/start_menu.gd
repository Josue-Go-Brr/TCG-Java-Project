extends Control

@onready var btn_play: TextureButton = $CenterContainer/ButtonStack/BtnPlay
@onready var btn_library: TextureButton = $CenterContainer/ButtonStack/BtnLibrary
@onready var btn_tutorial: TextureButton = $CenterContainer/ButtonStack/BtnTutorial
@onready var btn_quit: TextureButton = $CenterContainer/ButtonStack/BtnQuit

const SCENE_PLAY := "res://scene/main.tscn"
const SCENE_LIBRARY := "res://scene/Library/library_screen.tscn"


func _ready() -> void:
	btn_play.pressed.connect(_on_play_pressed)
	btn_library.pressed.connect(_on_library_pressed)
	btn_tutorial.pressed.connect(_on_tutorial_pressed)
	btn_quit.pressed.connect(_on_quit_pressed)


func _on_play_pressed() -> void:
	get_tree().change_scene_to_file(SCENE_PLAY)


func _on_library_pressed() -> void:
	get_tree().change_scene_to_file(SCENE_LIBRARY)


func _on_tutorial_pressed() -> void:
	# Placeholder by request: tutorial is not connected yet.
	print("Tutorial button clicked: no tutorial scene connected yet.")


func _on_quit_pressed() -> void:
	get_tree().quit()
