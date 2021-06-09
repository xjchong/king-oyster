package com.helloworldramen.kingoyster.scenes.tileoverlay

import godot.AnimationPlayer
import godot.ColorRect
import godot.Node2D
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.NodePath
import godot.extensions.getNodeAs

@RegisterClass
class TileOverlayScene: Node2D() {

	private val colorRect: ColorRect by lazy { getNodeAs("ColorRect")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }

	@RegisterFunction
	override fun _ready() {
		tween.tweenAllCompleted.connect(this, ::onTweenAllCompleted)
	}

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

	fun showTelegraph() {
		animationPlayer.play("telegraph")
		show()
	}

	fun showFlash(color: Color) {
		tween.interpolateProperty(colorRect, NodePath("color"),
			initialVal = Color(color.r, color.g, color.b, 0.0),
			finalVal = color,
			duration = 0.03
		)
		tween.interpolateProperty(colorRect, NodePath("color"),
			initialVal = color,
			finalVal = Color(color.r, color.g, color.b, 0.0),
			duration = 0.2,
			transType = Tween.TRANS_SINE,
			easeType = Tween.EASE_IN,
			delay = 0.08
		)
		show()
		tween.start()
	}

	@RegisterFunction
	fun onTweenAllCompleted() {
		hide()
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/tileoverlay/TileOverlayScene.tscn"
	}
}
