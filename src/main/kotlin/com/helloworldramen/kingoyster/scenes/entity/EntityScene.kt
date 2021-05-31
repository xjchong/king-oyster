package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.scenes.health.HealthScene
import com.helloworldramen.kingoyster.scenes.toasttext.ToastTextScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD
import java.util.*

@RegisterClass
class EntityScene : Node2D(), EventBusSubscriber {

	private val backgroundRect: ColorRect by lazy { getNodeAs("BackgroundRect")!! }
	private val entitySprite: EntitySprite by lazy { getNodeAs("EntitySprite")!! }
	private val healthScene: HealthScene by lazy { getNodeAs("EntitySprite/HealthScene")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }

	private val toastTextScene = GD.load<PackedScene>(ToastTextScene.PATH)

	private var context: Context = Context.UNKNOWN
	private var entity: Entity = Entity.UNKNOWN
	private val worldPosition: Position?
		get() = context.positionOf(entity)

	private val eventQueue: Queue<Event> = ArrayDeque()
	private var isProcessingEvent: Boolean = false

	private var isTweening: Boolean = false

	val isAnimating: Boolean
		get() = isTweening || animationPlayer.isPlaying()

	override fun receiveEvent(event: Event) {
		eventQueue.offer(event)
		return
	}

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this,
			WeaponAttackEvent::class,
			DamageEvent::class,
			DeathEvent::class,
			MoveEvent::class,
			TakeEvent::class
		)
		tween.tweenAllCompleted.connect(this, ::onAllTweenCompleted)
	}

	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		processEventQueue()

		if (!isAnimating && !isProcessingEvent && worldPosition != null) {
			visible = context.player.visiblePositions().contains(worldPosition)
		}
	}

	private fun processEventQueue() {
		if (isProcessingEvent || isAnimating || eventQueue.isEmpty()) return

		val event = eventQueue.poll() ?: return

		isProcessingEvent = true

		when (event) {
			is WeaponAttackEvent -> {
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
			is MoveEvent -> {
				if (event.entity == entity) {
					setPosition()
				}
			}
			is TakeEvent -> {
				if (event.taken == entity) {
					animatePulse()
				}
			}
		}

		isProcessingEvent = false
	}

	fun bind(context: Context, entity: Entity) {
		this.context = context
		this.entity = entity

		entitySprite.bind(entity)
		healthScene.bind(entity)
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

		if (entity.health() > 0) {
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

		val worldPosition = context.positionOf(entity)
		val baseZIndex = when {
			entity.has<MovementPart>() -> 100
			else -> 50
		}

		if (worldPosition == null) {
			visible = false
			return
		}

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

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/entity/EntityScene.tscn"
	}
}
