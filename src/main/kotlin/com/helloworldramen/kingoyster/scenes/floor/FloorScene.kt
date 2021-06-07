package com.helloworldramen.kingoyster.scenes.floor

import com.helloworldramen.kingoyster.utilities.WeightedCollection
import godot.AnimatedSprite
import godot.Node2D
import godot.annotation.RegisterClass
import godot.extensions.getNodeAs

@RegisterClass
class FloorScene : Node2D() {

	private val animatedSprite: AnimatedSprite by lazy { getNodeAs("AnimatedSprite")!! }

	private var sprite: String? = null
	private var frameIndex: Long = 0

	fun bind(sprite: String, weightedFrameIndices: WeightedCollection<Long>) {
		this.sprite = sprite
		this.frameIndex = weightedFrameIndices.sample() ?: 0

		updateAppearance()
	}

	private fun updateAppearance() {
		sprite?.let {
			animatedSprite.animation = it
			animatedSprite.frame = frameIndex
			show()
		} ?: run {
			hide()
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/floor/FloorScene.tscn"
	}
}
