package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.DoorPart
import godot.AnimatedSprite
import godot.Label
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.Vector2
import godot.extensions.getNodeAs
import kotlin.random.Random

@RegisterClass
class EntitySprite : Node2D() {

	private val label: Label by lazy { getNodeAs("Label")!! }
	private val animatedSprite: AnimatedSprite by lazy { getNodeAs("AnimatedSprite")!! }

	private var entity: Entity? = null

	@RegisterFunction
	override fun _process(delta: Double) {
		if (entity?.canChangeAppearance == true) updateAppearance()
	}

	fun bind(entity: Entity?) {
		this.entity = entity

		updateAppearance()
	}

	private fun updateAppearance() {
		animatedSprite.visible = false
		animatedSprite.position = Vector2(16, 16)

		val (text, color, sprite)= when(entity?.name) {
			"player" -> Triple("@", Color.yellow, "knight")
			"wall" -> {
				animatedSprite.position += Vector2(0, 8)
				Triple("#", Color.white, "grass_stone_wall")
			}
			"dagger" -> Triple("|", Color.gray, "dagger")
			"door" -> {
				Triple(
					if (entity?.find(DoorPart::class)?.isOpen == true) "'" else "+",
					Color.orange, null
				)
			}
			"ghost" -> Triple("G", Color.darkblue, "ghost")
			"goblin" -> Triple("g", Color.darkred, "goblin")
			"greatsword" -> Triple("|", Color.lightblue, "greatsword")
			"longsword" -> Triple("|", Color.white, "longsword")
			"medicine" -> Triple("!", Color.mediumseagreen, null)
			"slime" -> {
				animatedSprite.position -= Vector2(0, 2)
				Triple("s", Color.lightgreen, "blue_slime")
			}
			"stairs" -> Triple("<", Color.white, "stone_stairs_up")
			null -> Triple(" ", Color.white, null)
			else -> Triple("?", Color.red, null)
		}

		if (sprite != null) {
			// Play the sprite first to get the right frame count.
			animatedSprite.play(sprite)

			val frameCount = animatedSprite.frames?.getFrameCount(sprite) ?: 1
			animatedSprite.frame = Random.nextLong(0, frameCount)
			animatedSprite.visible = true
		}

		label.text = text
		label.set("custom_colors/font_color", color)
		label.visible = !animatedSprite.visible
	}

	private val Entity.canChangeAppearance: Boolean
		get() = has(DoorPart::class)
}
