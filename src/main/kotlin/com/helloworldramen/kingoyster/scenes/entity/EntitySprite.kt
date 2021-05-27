package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.DoorPart
import godot.AnimatedSprite
import godot.Label
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.Vector2
import godot.extensions.getNodeAs

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

		val (text, color)= when(entity?.name) {
			"player" -> {
				animatedSprite.visible = true
				animatedSprite.play("knight")
				Pair("@", Color.yellow)
			}
			"wall" -> Pair("#", Color.white)
			"door" -> {
				Pair(
					if (entity?.find(DoorPart::class)?.isOpen == true) "'" else "+",
					Color.orange
				)
			}
			"ghost" -> {
				Pair("G", Color.darkblue)
			}
			"goblin" -> {
				Pair("g", Color.darkred)
			}
			"slime" -> {
				animatedSprite.visible = true
				animatedSprite.position += Vector2(0, -1)
				animatedSprite.play("blue_slime")
				Pair("s", Color.lightgreen)
			}
			"stairs" -> Pair("<", Color.white)
			"coin" -> Pair("$", Color.cyan)
			null -> Pair(" ", Color.white)
			else -> Pair("?", Color.red)
		}

		label.text = text
		label.set("custom_colors/font_color", color)
		label.visible = !animatedSprite.visible
	}

	private val Entity.canChangeAppearance: Boolean
		get() = has(DoorPart::class)
}
