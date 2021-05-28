package com.helloworldramen.kingoyster.scenes.tileoverlay

import godot.AnimationPlayer
import godot.ColorRect
import godot.Node2D
import godot.annotation.RegisterClass
import godot.core.Color
import godot.extensions.getNodeAs

@RegisterClass
class TileOverlayScene: Node2D() {

	private val colorRect: ColorRect by lazy { getNodeAs("ColorRect")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }

	fun showCanSelect() {
		animationPlayer.play("can_select")
		show()
	}

	fun showSelected() {
		animationPlayer.play("selected")
		show()
	}

	fun showNonInteractive() {
		animationPlayer.play("non_interactive")
		show()
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/tileoverlay/TileOverlayScene.tscn"
	}
}
