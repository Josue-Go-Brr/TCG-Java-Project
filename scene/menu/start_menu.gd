extends Control

@onready var play_button = $CenterContainer/ButtonStack/PlayButton
@onready var library_button = $CenterContainer/ButtonStack/LibraryButton
@onready var tut_button = $CenterContainer/ButtonStack/TUTButton
@onready var quit_button = $CenterContainer/ButtonStack/QuitButton

func _ready() -> void:
	print("[StartMenu] ready")

	play_button.pressed.connect(_on_play_pressed)
	library_button.pressed.connect(_on_library_pressed)
	tut_button.pressed.connect(_on_tut_pressed)
	quit_button.pressed.connect(_on_quit_pressed)

func _on_play_pressed() -> void:
	print("Play pressed")
	get_tree().change_scene_to_file("res://scene/main.tscn")

func _on_library_pressed() -> void:
	print("Library pressed")
	get_tree().change_scene_to_file("res://scene/Library/library_screen.tscn")

func _on_tut_pressed() -> void:
	print("Tutorial pressed")
	get_tree().change_scene_to_file("res://scene/tutorial.tscn")

func _on_quit_pressed() -> void:
	print("Quit pressed")
	get_tree().quit()
