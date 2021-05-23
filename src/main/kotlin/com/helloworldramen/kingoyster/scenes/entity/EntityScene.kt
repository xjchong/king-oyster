package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.parts.MovementPart
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.global.GD

@RegisterClass
class EntityScene : Node2D(), EventBusSubscriber {

	private val entitySprite: EntitySprite by lazy { getNodeAs("EntitySprite")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }

	private var context: Context = Context.UNKNOWN()
	private var entity: Entity = Entity.UNKNOWN()


	override fun receiveEvent(event: Event) {
		when (event) {
			is DamageEvent -> {
				if (event.target == entity) {
					animateOnHit()
				}
			}
		}
	}

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this, DamageEvent::class)
	}

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

	fun animateBump(position: Position) {
		if (animationPlayer.isPlaying()) return
		val currentPosition = context.world[entity] ?: return

		when(position) {
			currentPosition.north() -> animationPlayer.play("bump_north")
			currentPosition.east() -> animationPlayer.play("bump_east")
			currentPosition.south() -> animationPlayer.play("bump_south")
			currentPosition.west() -> animationPlayer.play("bump_west")
			else -> return
		}
	}

	fun animatePulse() {
		animationPlayer.play("pulse")
	}

	fun animateOnHit() {
		animationPlayer.play("on_hit")
	}

	private fun setPosition(shouldAnimate: Boolean = true) {
		if (animationPlayer.isPlaying()) return

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
