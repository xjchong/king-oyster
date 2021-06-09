package com.helloworldramen.kingoyster.scenes.mainmenu

import com.helloworldramen.kingoyster.scenes.autoload.audio.AudioAutoload
import com.helloworldramen.kingoyster.utilities.BGM
import com.helloworldramen.kingoyster.utilities.SFX
import com.helloworldramen.kingoyster.scenes.game.GameScene
import com.helloworldramen.kingoyster.scenes.optionsmenu.OptionsMenuScene
import com.helloworldramen.kingoyster.utilities.Settings
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class MainMenuScene : MarginContainer() {

	private val audio : AudioAutoload by lazy { getNodeAs("/root/AudioAutoload")!! }
	private val startSelector: Label by lazy { getNodeAs("$ITEMS_VBOX_PATH/StartItem/StartSelector")!! }
	private val optionsSelector: Label by lazy { getNodeAs("$ITEMS_VBOX_PATH/OptionsItem/OptionsSelector")!! }
	private val exitSelector: Label by lazy { getNodeAs("$ITEMS_VBOX_PATH/ExitItem/ExitSelector")!! }
	private val backgroundRect: ColorRect by lazy { getNodeAs("BackgroundRect")!! }

	private val selectors: List<Label> by lazy { listOf(startSelector, optionsSelector, exitSelector) }
	private var selectedIndex = 0
		set(value) {
			setSelection(value)
			field = value
		}

	@RegisterFunction
	override fun _ready() {
		selectedIndex = 0
		audio.playLoop(BGM.MAIN_MENU)
		backgroundRect.color = Settings.BACKGROUND_COLOR
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		when {
			event.isActionPressed("ui_down", true) -> {
				audio.play(SFX.MENU_MOVE)
				selectedIndex = (selectedIndex + 1) % selectors.size
			}
			event.isActionPressed("ui_up", true) -> {
				audio.play(SFX.MENU_MOVE)
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
			0 -> {
				audio.play(SFX.MENU_CONFIRM)
				audio.endLoop()
				getTree()?.changeScene(GameScene.PATH)
			}
			1 -> {
				audio.play(SFX.MENU_SELECT)
				getTree()?.changeScene(OptionsMenuScene.PATH)
			}
			2 -> getTree()?.quit()
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/mainmenu/MainMenuScene.tscn"
		const val ITEMS_VBOX_PATH = "CenterContainer/VBox/ItemsMarginContainer/ItemsVBox"
	}
}
