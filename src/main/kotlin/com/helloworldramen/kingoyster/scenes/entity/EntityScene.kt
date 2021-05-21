package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.PortalPart
import godot.ColorRect
import godot.Label
import godot.Node2D
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs

@RegisterClass
class EntityScene : Node2D() {

	private val colorRect: ColorRect by lazy { getNodeAs("MarginContainer/ColorRect")!! }
	private val label: Label by lazy { getNodeAs("MarginContainer/Label")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }

	private var context: Context = Context.UNKNOWN()
	private var entity: Entity = Entity.UNKNOWN()

	@RegisterFunction
	override fun _process(delta: Double) {
		if (entity.canChangeAppearance) setAppearance()
		if (entity.canChangePosition) setPosition()
	}

	fun bind(context: Context, entity: Entity) {
		this.context = context
		this.entity = entity

		setAppearance()
		setPosition(shouldAnimate = false)
	}

	private fun setPosition(shouldAnimate: Boolean = true) {
		val worldPosition = context.world[entity]

		if (worldPosition == null) {
			visible = false
			return
		}

		visible = true
		zIndex = (100 + context.world[worldPosition]!!.indexOf(entity)).toLong()

		if (shouldAnimate) {
			tween.run {
				interpolateProperty(this@EntityScene, NodePath("position"),
					initialVal = position, finalVal = calculateNodePosition(worldPosition),
					0.1, easeType = Tween.EASE_IN
				)
				start()
			}
		} else {
			position = calculateNodePosition(worldPosition)
		}
	}

	private fun setAppearance() {
		label.text = when(entity.name) {
			"player" -> "@"
			"wall" -> "#"
			"door" -> {
				if (entity.find(PortalPart::class)?.isOpen == true) "'" else "+"
			}
			"slime" -> "s"
			"stairs" -> "<"
			"coin" -> "$"
			else -> "?"
		}
	}

	private fun calculateNodePosition(worldPosition: Position): Vector2 {
		return Vector2(worldPosition.x * 32, worldPosition.y * 32)
	}

	val Entity.canChangeAppearance: Boolean
		get() = has(PortalPart::class)

	val Entity.canChangePosition: Boolean
		get() = has(MovementPart::class) || has(ItemPart::class)

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/entity/EntityScene.tscn"
	}
}
