package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.entities.isPlayer
import com.helloworldramen.kingoyster.entities.isVisibleToPlayer
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.scenes.health.HealthScene
import com.helloworldramen.kingoyster.scenes.toasttext.ToastTextScene
import com.helloworldramen.kingoyster.scenes.world.WorldScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD
import java.util.*
import kotlin.math.roundToInt

@RegisterClass
class EntityScene : Node2D(), EventBusSubscriber {

	private val appearance: Node2D by lazy { getNodeAs("AppearanceNode2D")!! }
	private val entitySprite: EntitySprite by lazy { getNodeAs("AppearanceNode2D/EntitySprite")!! }
	private val healthScene: HealthScene by lazy { getNodeAs("AppearanceNode2D/EntitySprite/HealthScene")!! }
	private val durabilityLabel: Label by lazy { getNodeAs("AppearanceNode2D/EntitySprite/DurabilityLabel")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }

	private val packedToastTextScene = GD.load<PackedScene>(ToastTextScene.PATH)

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
			DamageWeaponEvent::class,
			DeathEvent::class,
			DropWeaponEvent::class,
			EquipWeaponEvent::class,
			MoveEvent::class,
			PlayerToastEvent::class,
			TakeEvent::class,
			ThrowWeaponEvent::class,
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
			appearance.visible = context.player.visiblePositions().contains(worldPosition)

			// Sometimes the position should be updated.
			context.positionOf(entity)?.let { worldPosition ->
				if (position != calculateNodePosition(worldPosition)) {
					setPosition(false)
				}
			}
		}
	}

	private fun processEventQueue() {
		if (isProcessingEvent || isAnimating || eventQueue.isEmpty()) return

		isProcessingEvent = true

		val event = eventQueue.poll()

		if (event == null) {
			isProcessingEvent = false
			return
		}

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
			is DamageWeaponEvent -> {
				if (event.owner == entity) {
					if (event.isBroken) {
						toast("-${event.weapon.name} break!", Color.orange, ToastTextScene.LONG_REVERSE_CONFIG)
					}
				} else if (event.weapon == entity) {
					updateDurabilityLabel()
					if (event.owner == null && event.isBroken) {
						toast("break!", Color.orange, ToastTextScene.SHORT_CONFIG)
						animateOnBreak()
					}
				}
			}
			is DeathEvent -> {
				if (event.entity == entity) {
					animateOnDeath()
				}
			}
			is DropWeaponEvent -> {
				if (event.weapon == entity) {
					setPosition(false)
				} else if (event.dropper == entity) {
					animateDropWeapon(event.weapon)
				}
			}
			is EquipWeaponEvent -> {
				if (event.weapon == entity) {
					setPosition(false)
				} else if (event.equipper == entity) {
					animateEquipWeapon(event.weapon)
				}
			}
			is MoveEvent -> {
				if (event.entity == entity) {
					setPosition()
				}
			}
			is PlayerToastEvent -> {
				if (entity.isPlayer) {
					toast(event.message, event.color, ToastTextScene.LONG_CONFIG)
				}
			}
			is TakeEvent -> {
				if (event.taken == entity) {
					animatePulse()
				}
			}
			is ThrowWeaponEvent -> {
				if (event.weapon == entity) {
					animateThrown(event)
				} else if (event.thrower == entity) {
					animateDropWeapon(event.weapon)
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
		updateDurabilityLabel()
		setPosition(shouldAnimate = false)

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
		toast(amount.toString(), Color.white, ToastTextScene.SHORT_CONFIG)

		if (entity.health() > 0) {
			animationPlayer.play("on_hit")
		}
	}

	fun animateOnBreak() {
		animationPlayer.play("on_break")
	}

	fun animateOnDeath() {
		animationPlayer.play("on_death")
	}

	fun animateDropWeapon(weapon: Entity) {
		toast("-${weapon.name}", Color.gray, ToastTextScene.LONG_REVERSE_CONFIG)
	}

	fun animateEquipWeapon(weapon: Entity) {
		toast("+${weapon.name}", Color.lightgray, ToastTextScene.LONG_CONFIG)
	}

	fun animateThrown(event: ThrowWeaponEvent) {
		isTweening = true
		position = calculateNodePosition(event.from)
		appearance.visible = true
		zIndex = 75

		val throwDuration = 0.15

		tween.interpolateProperty(this, NodePath("position"),
			initialVal = position,
			finalVal = calculateNodePosition(event.to),
			duration = throwDuration, transType = Tween.TRANS_CUBIC, easeType = Tween.EASE_IN
		)
		tween.start()
	}

	@RegisterFunction
	fun onAllTweenCompleted() {
		isTweening = false
	}

	fun toast(text: String, color: Color, configuration: String) {
		if (!calculateWorldPosition(position).isVisibleToPlayer(context)) return

		packedToastTextScene?.instanceAs<ToastTextScene>()?.let {
			it.bind(text, color, configuration)
			addChild(it)
		}
	}

	private fun setPosition(shouldAnimate: Boolean = true) {
		if (isAnimating) return

		val worldPosition = context.positionOf(entity)

		if (worldPosition == null) {
			appearance.visible = false
			return
		}

		zIndex = getZIndexFor(worldPosition)

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

	private fun updateDurabilityLabel() {
		durabilityLabel.visible = entity.has<WeaponPart>()
		durabilityLabel.text = entity.durability().toString()
	}

	private fun calculateNodePosition(worldPosition: Position): Vector2 {
		return Vector2(worldPosition.x * WorldScene.TILE_SIZE, worldPosition.y * WorldScene.TILE_SIZE)
	}

	private fun calculateWorldPosition(nodePosition: Vector2): Position {
		return Position(
			(nodePosition.x / WorldScene.TILE_SIZE).roundToInt(),
			(nodePosition.y / WorldScene.TILE_SIZE).roundToInt())
	}

	private fun getZIndexFor(worldPosition: Position): Long {
		val baseZIndex =  when {
			entity.has<MovementPart>() -> 100
			else -> 50
		}.toLong()

		return baseZIndex + context.entitiesAt(worldPosition)!!.indexOf(entity).toLong()
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/entity/EntityScene.tscn"
	}
}
