extends CanvasLayer

@export var joystick_deadzone := 0.2

@onready var joystick_touch_area: Control = $Controls/Left/JoystickTouchArea
@onready var joystick_knob: Control = $Controls/Left/JoystickTouchArea/JoystickKnob

@onready var attack_button: Button = $Controls/Right/AttackButton
@onready var jump_button: Button = $Controls/Right/JumpButton
@onready var dodge_button: Button = $Controls/Right/DodgeButton
@onready var interact_button: Button = $Controls/Right/InteractButton

var _touch_id := -1
var _joystick_origin := Vector2.ZERO
var _joystick_radius := 0.0
var _joystick_vector := Vector2.ZERO

func _ready() -> void:
	visible = true
	_joystick_origin = joystick_touch_area.size * 0.5
	_joystick_radius = min(joystick_touch_area.size.x, joystick_touch_area.size.y) * 0.45
	joystick_knob.position = _joystick_origin - (joystick_knob.size * 0.5)
	_wire_button(attack_button, "use_weapon_light")
	_wire_button(jump_button, "jump")
	_wire_button(dodge_button, "dodge_dash")
	_wire_button(interact_button, "interact")

func _wire_button(button: Button, action_name: StringName) -> void:
	button.button_down.connect(func() -> void: Input.action_press(action_name))
	button.button_up.connect(func() -> void: Input.action_release(action_name))

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventScreenTouch:
		if event.pressed and _touch_id == -1 and joystick_touch_area.get_global_rect().has_point(event.position):
			_touch_id = event.index
			_set_joystick_from_global(event.position)
		elif not event.pressed and event.index == _touch_id:
			_touch_id = -1
			_reset_joystick()
	elif event is InputEventScreenDrag and event.index == _touch_id:
		_set_joystick_from_global(event.position)

func _process(_delta: float) -> void:
	Input.action_release("move_left")
	Input.action_release("move_right")
	Input.action_release("move_up")
	Input.action_release("move_down")

	if _joystick_vector.length() < joystick_deadzone:
		return

	if _joystick_vector.x < -joystick_deadzone:
		Input.action_press("move_left", abs(_joystick_vector.x))
	elif _joystick_vector.x > joystick_deadzone:
		Input.action_press("move_right", _joystick_vector.x)

	if _joystick_vector.y < -joystick_deadzone:
		Input.action_press("move_up", abs(_joystick_vector.y))
	elif _joystick_vector.y > joystick_deadzone:
		Input.action_press("move_down", _joystick_vector.y)

func _set_joystick_from_global(global_pos: Vector2) -> void:
	var local := joystick_touch_area.get_global_transform_with_canvas().affine_inverse() * global_pos
	var offset := local - _joystick_origin
	if offset.length() > _joystick_radius:
		offset = offset.normalized() * _joystick_radius
	_joystick_vector = offset / _joystick_radius
	joystick_knob.position = (_joystick_origin + offset) - (joystick_knob.size * 0.5)

func _reset_joystick() -> void:
	_joystick_vector = Vector2.ZERO
	joystick_knob.position = _joystick_origin - (joystick_knob.size * 0.5)
