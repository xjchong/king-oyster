package com.helloworldramen.kingoyster.scenes.gameover

import com.helloworldramen.kingoyster.scenes.mainmenu.MainMenuScene
import godot.ColorRect
import godot.InputEvent
import godot.Label
import godot.MarginContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class GameOverScene: MarginContainer() {

	private val colorRect: ColorRect by lazy { getNodeAs("ColorRect")!! }
	private val titleLabel: Label by lazy { getNodeAs("CenterContainer/VBoxContainer/TitleLabel")!! }
	private val subtitleLabel: Label by lazy { getNodeAs("CenterContainer/VBoxContainer/SubtitleLabel")!! }

	@RegisterFunction
	override fun _ready() {
		hide()
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (!visible) return

		acceptEvent()

		if (event.isActionPressed("ui_accept")) {
			getTree()?.changeScene(MainMenuScene.PATH)
		}
	}

	fun showWin() {
		show()
		titleLabel.text = "You Win!"
		subtitleLabel.text = "Nicely done..."
	}

	fun showLose() {
		show()
		titleLabel.text = "You Died!"
		subtitleLabel.text = "Better luck next time..."
	}
}
