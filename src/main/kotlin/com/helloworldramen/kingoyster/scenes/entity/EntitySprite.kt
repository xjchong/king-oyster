package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.AppearancePart
import godot.AnimatedSprite
import godot.Label
import godot.Node2D
import godot.annotation.RegisterClass
import godot.core.Color
import godot.core.Vector2
import godot.extensions.getNodeAs
import kotlin.random.Random

@RegisterClass
class EntitySprite : Node2D() {

	private val label: Label by lazy { getNodeAs("Label")!! }
	private val animatedSprite: AnimatedSprite by lazy { getNodeAs("AnimatedSprite")!! }

	private var entity: Entity? = null

	fun bind(entity: Entity?) {
		this.entity = entity

		updateAppearance()
	}

	private fun updateAppearance() {
		animatedSprite.visible = false
		animatedSprite.position = Vector2(16, 16)

		val appearancePart = entity?.find<AppearancePart>()
		val ascii = appearancePart?.ascii ?: ' '
		val color = appearancePart?.color ?: Color.white
		val sprite = appearancePart?.sprite
		val offset = appearancePart?.offset ?: Vector2.ZERO
		val frameIndex = appearancePart?.frameIndex

		if (sprite != null) {
			// Set the sprite animation first to get the right frame count.
			animatedSprite.animation = sprite

			if (frameIndex != null) {
				animatedSprite.stop()
				animatedSprite.frame = frameIndex
			} else {
				val frameCount = animatedSprite.frames?.getFrameCount(sprite) ?: 1
				animatedSprite.frame = Random.nextLong(0, frameCount)
				animatedSprite.play()
			}

			animatedSprite.position += offset
			animatedSprite.visible = true
		}

		label.text = ascii.toString()
		label.set("custom_colors/font_color", color)
		label.visible = !animatedSprite.visible
	}
}
