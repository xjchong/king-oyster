package com.helloworldramen.kingoyster.scenes.tileoverlay

import godot.ColorRect
import godot.Node2D
import godot.annotation.RegisterClass
import godot.core.Color
import godot.extensions.getNodeAs

@RegisterClass
class TileOverlayScene: Node2D() {

	private val colorRect: ColorRect by lazy { getNodeAs("ColorRect")!! }

	fun showCanSelect() {
		colorRect.color = Color.yellow
		show()
	}

	fun showSelected() {
		colorRect.color = Color.white
		show()
	}

	fun showNonInteractive() {
		colorRect.color = Color.black
		show()
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/tileoverlay/TileOverlayScene.tscn"
	}
}
