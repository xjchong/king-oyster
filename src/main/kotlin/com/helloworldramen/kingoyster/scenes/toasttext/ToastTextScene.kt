package com.helloworldramen.kingoyster.scenes.toasttext

import godot.Label
import godot.Node2D
import godot.Position2D
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import java.util.*
import kotlin.math.min
import kotlin.random.Random

@RegisterClass
class ToastTextScene : Node2D() {

	private val label: Label by lazy { getNodeAs("Label")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private var velocity = Vector2.ZERO
	private var configuration = SHORT_CONFIG

	@RegisterFunction
	override fun _ready() {
		when (configuration) {
			SHORT_CONFIG -> {
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
			}
			MEDIUM_CONFIG -> {
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
					duration = 0.2, Tween.TRANS_LINEAR, Tween.EASE_OUT, delay = 0.4)
			}
			LONG_CONFIG, LONG_REVERSE_CONFIG -> {
				tween.interpolateProperty(this, NodePath("modulate:a"),
					initialVal = modulate.a,
					finalVal = 0.0,
					duration = 0.4, Tween.TRANS_LINEAR, Tween.EASE_OUT, delay = 0.8)
			}
		}

		tween.tweenAllCompleted.connect(this, ::onTweenComplete)

		tween.start()
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		velocity = when (configuration) {
			SHORT_CONFIG -> Vector2(velocity.x, min(0.0, velocity.y + (700 * delta)))
			MEDIUM_CONFIG -> Vector2(velocity.x, min(0.0, velocity.y + (400 * delta)))
			LONG_CONFIG -> Vector2(velocity.x, (velocity.y + (300 * delta)).coerceAtMost(-8.0))
			LONG_REVERSE_CONFIG -> Vector2(velocity.x, (velocity.y - (300 * delta)).coerceAtLeast(8.0))
			else -> Vector2.ZERO
		}

		position += velocity * delta
	}

	@RegisterFunction
	fun onTweenComplete() {
		queueFree()
	}

	fun bind(text: String, color: Color = Color.white, configuration: String = SHORT_CONFIG) {
		this.configuration = configuration
		label.text = text
		label.set("custom_colors/font_color", color)

		when (configuration) {
			SHORT_CONFIG -> {
				position = Vector2.ZERO
				velocity = Vector2(Random.nextInt(-15, 15), -150)
			}
			MEDIUM_CONFIG -> {
				position = Vector2.ZERO
				velocity = Vector2(Random.nextInt(-10, 10), -100)
			}
			LONG_CONFIG -> {
				modulate.a = 1.0
				position = Vector2(0, -16)
				velocity = Vector2(0, -50)
				scale = Vector2(0.9, 0.9)
			}
			LONG_REVERSE_CONFIG -> {
				modulate.a = 1.0
				position = Vector2(0, 16)
				velocity = Vector2(0, 50)
				scale = Vector2(0.9, 0.9)
			}
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/toasttext/ToastTextScene.tscn"
		const val SHORT_CONFIG = "short"
		const val MEDIUM_CONFIG = "medium"
		const val LONG_CONFIG = "long"
		const val LONG_REVERSE_CONFIG = "long_reverse"
	}
}
