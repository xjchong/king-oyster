package com.helloworldramen.kingoyster.scenes.mainmenu

import com.helloworldramen.kingoyster.scenes.world.WorldScene
import godot.InputEvent
import godot.Label
import godot.MarginContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs
import godot.global.GD

@RegisterClass
class MainMenuScene : MarginContainer() {

	private val startSelector: Label by lazy { getNodeAs("$ITEMS_VBOX_PATH/StartItem/StartSelector")!! }
	private val optionsSelector: Label by lazy { getNodeAs("$ITEMS_VBOX_PATH/OptionsItem/OptionsSelector")!! }
	private val exitSelector: Label by lazy { getNodeAs("$ITEMS_VBOX_PATH/ExitItem/ExitSelector")!! }

	private val selectors: List<Label> by lazy { listOf(startSelector, optionsSelector, exitSelector) }
	var selectedIndex = 0
		set(value) {
			setSelection(value)
			field = value
		}

	@RegisterFunction
	override fun _ready() {
		selectedIndex = 0
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		when {
			event.isActionPressed("ui_down", true) -> {
				selectedIndex = (selectedIndex + 1) % selectors.size
			}
			event.isActionPressed("ui_up", true) -> {
				selectedIndex = (selectedIndex + selectors.size - 1) % selectors.size
			}
			event.isActionPressed("ui_accept") -> handleItemEntered()
		}
	}

	private fun setSelection(selectedIndex: Int) {
		selectors.forEachIndexed { index, label ->
			label.text = when (index) {
				selectedIndex -> ">"
				else -> ""
			}
		}
	}

	private fun handleItemEntered() {
		when (selectedIndex) {
			0 -> getTree()?.changeScene(WorldScene.PATH)
			1 -> GD.print("TODO: Implement options scene.")
			2 -> getTree()?.quit()
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/mainmenu/MainMenuScene.tscn"
		const val ITEMS_VBOX_PATH = "CenterContainer/VBox/ItemsMarginContainer/ItemsVBox"
	}
}
