package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.PortalPart
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.global.GD

@RegisterClass
class EntityScene : Node2D() {

	private val entitySprite: EntitySprite by lazy { getNodeAs("EntitySprite")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }

	private var context: Context = Context.UNKNOWN()
	private var entity: Entity = Entity.UNKNOWN()

	@RegisterFunction
	override fun _process(delta: Double) {
		if (entity.canChangePosition) setPosition()
	}

	fun bind(context: Context, entity: Entity) {
		this.context = context
		this.entity = entity

		entitySprite.bind(entity)
		setPosition(shouldAnimate = false)
	}

	private fun setPosition(shouldAnimate: Boolean = true) {
		val worldPosition = context.world[entity]
		val baseZIndex = when {
			entity.has(MovementPart::class) -> 100
			else -> 50
		}

		if (worldPosition == null) {
			visible = false
			return
		}

		visible = true
		zIndex = (baseZIndex + context.world[worldPosition]!!.indexOf(entity)).toLong()

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

	private fun calculateNodePosition(worldPosition: Position): Vector2 {
		return Vector2(worldPosition.x * 32, worldPosition.y * 32)
	}

	private val Entity.canChangePosition: Boolean
		get() = has(MovementPart::class) || has(ItemPart::class)

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/entity/EntityScene.tscn"
	}
}
