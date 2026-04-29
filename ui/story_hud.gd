extends CanvasLayer

@export var player_path: NodePath = ^"../PlayerCharacterBodySoulsBase"

@onready var intro_panel: Control = $IntroPanel
@onready var intro_text: Label = $IntroPanel/MarginContainer/VBoxContainer/IntroText
@onready var objective_label: Label = $ObjectivePanel/MarginContainer/VBoxContainer/ObjectiveLabel
@onready var hint_label: Label = $ObjectivePanel/MarginContainer/VBoxContainer/HintLabel
@onready var chapter_label: Label = $ObjectivePanel/MarginContainer/VBoxContainer/ChapterLabel
@onready var progress_label: Label = $ObjectivePanel/MarginContainer/VBoxContainer/ProgressLabel

var _player: Node = null
var _intro_step := 0
var _quest_step := 0
var _interact_count := 0
var _attack_count := 0
var _dodge_count := 0

const INTRO_TEXT := [
	"Chapter I: Embers in the Dark",
	"The castle heart is fading. Relight it before the ruins consume the valley.",
	"Move, survive, and push deeper through the gates.",
]

const QUESTS := [
	{
		"title": "Scout the courtyard",
		"hint": "Interact with 2 world objects (door, lever, ladder, chest).",
		"target": 2,
		"counter": "interact"
	},
	{
		"title": "Master your footing",
		"hint": "Dodge 3 times to prepare for close combat.",
		"target": 3,
		"counter": "dodge"
	},
	{
		"title": "Strike through",
		"hint": "Use 5 light attacks to pressure enemies.",
		"target": 5,
		"counter": "attack"
	},
]

func _ready() -> void:
	_player = get_node_or_null(player_path)
	if _player:
		_connect_if_exists("interact_started", _on_player_interact)
		_connect_if_exists("death_started", _on_player_death)
		_connect_if_exists("dodge_started", _on_player_dodge)
		_connect_if_exists("attack_started", _on_player_attack)

	_intro_step = 0
	_quest_step = 0
	intro_panel.show()
	intro_text.text = INTRO_TEXT[_intro_step]
	_update_quest_ui()

func _connect_if_exists(signal_name: StringName, callback: Callable) -> void:
	if _player.has_signal(signal_name):
		_player.connect(signal_name, callback)

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventScreenTouch and event.pressed:
		_advance_intro()

func _advance_intro() -> void:
	if not intro_panel.visible:
		return
	_intro_step += 1
	if _intro_step < INTRO_TEXT.size():
		intro_text.text = INTRO_TEXT[_intro_step]
	else:
		intro_panel.hide()

func _on_player_interact(_interact_type: String) -> void:
	_interact_count += 1
	_try_advance_quest("interact", _interact_count)

func _on_player_dodge() -> void:
	_dodge_count += 1
	_try_advance_quest("dodge", _dodge_count)

func _on_player_attack() -> void:
	_attack_count += 1
	_try_advance_quest("attack", _attack_count)

func _on_player_death() -> void:
	chapter_label.text = "Chapter I: Embers in the Dark"
	objective_label.text = "You fell before the flame was restored."
	hint_label.text = "Tap to retry, then continue your objectives."

func _try_advance_quest(counter_name: String, value: int) -> void:
	if _quest_step >= QUESTS.size():
		return

	var quest := QUESTS[_quest_step]
	if quest["counter"] != counter_name:
		_update_progress_only()
		return

	if value >= int(quest["target"]):
		_quest_step += 1
		if _quest_step >= QUESTS.size():
			chapter_label.text = "Chapter I Complete"
			objective_label.text = "Path cleared. Enter the inner keep."
			hint_label.text = "Next chapter can add boss encounter + checkpoint story beats."
			progress_label.text = "Progress: 3/3"
			return

	_update_quest_ui()

func _update_progress_only() -> void:
	if _quest_step >= QUESTS.size():
		return
	var quest := QUESTS[_quest_step]
	var current := _current_value_for_counter(String(quest["counter"]))
	progress_label.text = "Progress: %d/%d" % [min(current, int(quest["target"])), int(quest["target"])]

func _update_quest_ui() -> void:
	if _quest_step >= QUESTS.size():
		return
	var quest := QUESTS[_quest_step]
	chapter_label.text = "Chapter I: Embers in the Dark"
	objective_label.text = "Objective: %s" % String(quest["title"])
	hint_label.text = "Hint: %s" % String(quest["hint"])
	_update_progress_only()

func _current_value_for_counter(counter_name: String) -> int:
	match counter_name:
		"interact":
			return _interact_count
		"dodge":
			return _dodge_count
		"attack":
			return _attack_count
		_:
			return 0
