package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.AttackEvent
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.DeathEvent
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.scenes.toasttext.ToastTextScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD

@RegisterClass
class EntityScene : Node2D(), EventBusSubscriber {

	private val backgroundRect: ColorRect by lazy { getNodeAs("BackgroundRect")!! }
	private val entitySprite: EntitySprite by lazy { getNodeAs("EntitySprite")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }

	private val toastTextScene = GD.load<PackedScene>(ToastTextScene.PATH)

	private var context: Context = Context.UNKNOWN
	private var entity: Entity = Entity.UNKNOWN

	private var isTweening: Boolean = false

	val isAnimating: Boolean
		get() = isTweening || animationPlayer.isPlaying()

	override fun receiveEvent(event: Event) {
		when (event) {
			is AttackEvent -> {
				if (event.attacker == entity) {
					context.world[event.target]?.let {
						animateBump(it)
					}
				}
			}
			is DamageEvent -> {
				if (event.target == entity) {
					animateOnHit(event.value)
				}
			}
			is DeathEvent -> {
				if (event.entity == entity) {
					animateOnDeath()
				}
			}
		}
	}

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this, AttackEvent::class, DamageEvent::class, DeathEvent::class)
		tween.tweenAllCompleted.connect(this, ::onAllTweenCompleted)
	}

	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val worldPosition = context.world[entity]

		if (entity.canChangePosition && worldPosition != null) {
			if (position != calculateNodePosition(worldPosition)) {
				setPosition()
			}
		}

		visible = context.player.visiblePositions().contains(worldPosition)
	}

	fun bind(context: Context, entity: Entity) {
		this.context = context
		this.entity = entity

		entitySprite.bind(entity)
		setPosition(shouldAnimate = false)

		backgroundRect.visible = entity.name != "wall"
		entitySprite.visible = entity.name != "wall"
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

	fun animateOnHit(amount: Int) {
		toastTextScene?.instanceAs<ToastTextScene>()?.let {
			it.bind(amount.toString())
			addChild(it)
		}

		if (entity.find(HealthPart::class)?.health ?: 0 > 0) {
			animationPlayer.play("on_hit")
		}
	}

	fun animateOnDeath() {
		animationPlayer.play("on_death")
	}

	@RegisterFunction
	fun onAllTweenCompleted() {
		isTweening = false
	}

	private fun setPosition(shouldAnimate: Boolean = true) {
		if (isAnimating) return

		val worldPosition = context.world[entity]
		val baseZIndex = when {
			entity.has<MovementPart>() -> 100
			else -> 50
		}

		if (worldPosition == null) {
			visible = false
			return
		}

		visible = true
		zIndex = (baseZIndex + context.world[worldPosition]!!.indexOf(entity)).toLong()

		if (shouldAnimate) {
			isTweening = true
			tween.interpolateProperty(this, NodePath("position"),
				initialVal = position, finalVal = calculateNodePosition(worldPosition),
				0.06, transType = Tween.TRANS_QUAD, easeType = Tween.EASE_IN)
			tween.start()
		} else {
			position = calculateNodePosition(worldPosition)
		}
	}

	private fun calculateNodePosition(worldPosition: Position): Vector2 {
		return Vector2(worldPosition.x * 32, worldPosition.y * 32)
	}

	private val Entity.canChangePosition: Boolean
		get() = has<MovementPart>() || has<ItemPart>()

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/entity/EntityScene.tscn"
	}
}
