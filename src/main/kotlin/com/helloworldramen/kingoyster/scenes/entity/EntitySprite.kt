package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.OpenablePart
import com.helloworldramen.kingoyster.utilities.EntityAppearanceDirectory
import godot.AnimatedSprite
import godot.Label
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
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

		val (ascii, color, sprite, offset) = EntityAppearanceDirectory[entity]

		if (sprite != null) {
			// Play the sprite first to get the right frame count.
			animatedSprite.play(sprite)
			val frameCount = animatedSprite.frames?.getFrameCount(sprite) ?: 1
			animatedSprite.frame = Random.nextLong(0, frameCount)

			animatedSprite.position += offset
			animatedSprite.visible = true
		}

		label.text = ascii.toString()
		label.set("custom_colors/font_color", color)
		label.visible = !animatedSprite.visible
	}
}
