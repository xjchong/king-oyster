package com.helloworldramen.kingoyster.scenes.toasttext

import godot.Label
import godot.Node2D
import godot.Position2D
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import kotlin.math.min
import kotlin.random.Random

@RegisterClass
class ToastTextScene : Node2D() {

	private val label: Label by lazy { getNodeAs("Label")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private var velocity = Vector2(Random.nextInt(-15, 15), -150)

	@RegisterFunction
	override fun _ready() {
		tween.interpolateProperty(this, NodePath("scale"),
			initialVal = Vector2.ZERO,
			finalVal = Vector2.ONE,
			duration = 0.1, Tween.TRANS_QUART, Tween.EASE_OUT)

		tween.interpolateProperty(this, NodePath("scale"),
			initialVal = Vector2.ONE,
			finalVal = Vector2(0.8, 0.8),
			duration = 0.4, Tween.TRANS_QUART, Tween.EASE_OUT, 0.3)

		tween.interpolateProperty(this, NodePath("modulate:a"),
			initialVal = modulate.a,
			finalVal = 0.0,
			duration = 0.1, Tween.TRANS_LINEAR, Tween.EASE_OUT, delay = 0.25)

		tween.tweenAllCompleted.connect(this, ::onTweenComplete)

		tween.start()
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		velocity = Vector2(velocity.x, min(0.0, velocity.y + (700 * delta)))
		position += velocity * delta
	}

	@RegisterFunction
	fun onTweenComplete() {
		queueFree()
	}

	fun bind(text: String) {
		label.text = text
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/toasttext/ToastTextScene.tscn"
	}
}
