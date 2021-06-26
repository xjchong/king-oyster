package com.helloworldramen.kingoyster.scenes.entity

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.extensions.isVisibleToPlayer
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.combat.statuseffects.BurnStatusEffect
import com.helloworldramen.kingoyster.parts.combat.statuseffects.ColdStatusEffect
import com.helloworldramen.kingoyster.parts.combat.statuseffects.PoisonStatusEffect
import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect
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
	private val healthScene: HealthScene by lazy { getNodeAs("AppearanceNode2D/HealthScene")!! }
	private val durabilityLabel: Label by lazy { getNodeAs("AppearanceNode2D/DurabilityLabel")!! }
	private val statusNode: Node2D by lazy { getNodeAs("AppearanceNode2D/StatusNode")!! }
	private val statusLabel: RichTextLabel by lazy { getNodeAs("AppearanceNode2D/StatusNode/StatusLabel")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }
	private val flashAnimator: AnimationPlayer by lazy { getNodeAs("FlashAnimator")!! }

	private val packedToastTextScene = GD.load<PackedScene>(ToastTextScene.PATH)

	private var context: Context = Context.UNKNOWN
	private var entity: Entity = Entity.UNKNOWN
	private val worldPosition: Position?
		get() = context.positionOf(entity)

	private val eventQueue: Queue<Event> = ArrayDeque()
	private var isProcessingEvents: Boolean = false

	private var isTweening: Boolean = false
	private var shouldUpdateStatus: Boolean = false

	val isAnimating: Boolean
		get() = isTweening || animationPlayer.isPlaying()

	override fun receiveEvent(event: Event) {
		eventQueue.offer(event)
		return
	}

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this,
			DamageEntityEvent::class,
			DamageWeaponEvent::class,
			DeathEvent::class,
			DefaultAttackEvent::class,
			DropItemEvent::class,
			DropWeaponEvent::class,
			HealEvent::class,
			MoveEvent::class,
			OpenEvent::class,
			PlayerToastEvent::class,
			StatusAppliedEvent::class,
			StatusExpiredEvent::class,
			TakeItemEvent::class,
			TakeWeaponEvent::class,
			TelegraphEvent::class,
			ThrowWeaponEvent::class,
			TriggerTrapEvent::class,
			UseItemEvent::class,
			WeaponAttackEvent::class,
		)
		tween.tweenAllCompleted.connect(this, ::onAllTweenCompleted)
		animationPlayer.animationFinished.connect(this, ::onAnimationFinished)
		appearance.visible = false
	}

	@RegisterFunction
	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		if (shouldUpdateStatus) {
			updateStatusLabel()
		}

		processEventQueue()

		if (!isAnimating && !isProcessingEvents) {
			appearance.visible = context.player.visiblePositions().contains(worldPosition)

			if (eventQueue.isEmpty()) {
				// Sometimes the position should be updated.
				worldPosition?.let { worldPosition ->
					if (position != calculateNodePosition(worldPosition)) {
						setPosition(false)
					}
				}
			}
		}
	}

	private fun processEventQueue() {
		if (isProcessingEvents || isAnimating || eventQueue.isEmpty()) return

		isProcessingEvents = true

		while (true) {
			val event = eventQueue.poll()

			if (event == null) {
				isProcessingEvents = false
				return
			}

			when (event) {
				is DamageEntityEvent -> {
					if (event.target == entity) {
						animateOnDamage(event.amount)
					}
				}
				is DamageWeaponEvent -> {
					if (event.owner == entity) {
						if (event.isBroken) {
							toast("-${event.weapon.name} break!", Color.orange, ToastTextScene.LONG_REVERSE_CONFIG)
						} else if (event.weapon.durability() == 1) {
							toast("${event.weapon.name} weak!", Color.khaki, ToastTextScene.LONG_CONFIG)
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
				is DefaultAttackEvent -> {
					if (event.attacker == entity) {
						animateBump(event.direction)
					}
				}
				is DropItemEvent -> {
					if (event.item == entity) {
						setPosition(false)
					} else if (event.dropper == entity) {
						animateDrop(event.item)
					}
				}
				is DropWeaponEvent -> {
					if (event.weapon == entity) {
						setPosition(false)
					} else if (event.dropper == entity) {
						animateDrop(event.weapon)
					}
				}
				is HealEvent -> {
					if (event.target == entity) {
						animateOnHeal(event.amount)
					}
				}
				is MoveEvent -> {
					if (event.entity == entity) {
						setPosition()
					}
				}
				is OpenEvent -> {
					if (event.openable == entity) {
						animateOpen()
					}
				}
				is PlayerToastEvent -> {
					if (entity.isPlayer) {
						toast(event.message, event.color, ToastTextScene.LONG_CONFIG)
					}
				}
				is StatusAppliedEvent -> {
					if (event.owner == entity) {
						shouldUpdateStatus = true
					}
				}
				is StatusExpiredEvent -> {
					if (event.owner == entity) {
						updateStatusLabel()
						shouldUpdateStatus = false
					}
				}
				is TakeItemEvent -> {
					if (event.item == entity) {
						animatePulse()
						setPosition(false)
					} else if (event.taker == entity) {
						animateTake(event.item)
					}
				}
				is TakeWeaponEvent -> {
					if (event.weapon == entity) {
						animatePulse()
						setPosition(false)
					} else if (event.taker == entity) {
						animateTake(event.weapon)
					}
				}
				is TelegraphEvent -> {
					if (event.entity == entity) {
						animateTelegraph(event.telegraphs.isNotEmpty())
					}
				}
				is ThrowWeaponEvent -> {
					if (event.weapon == entity) {
						animateThrown(event)
					} else if (event.thrower == entity) {
						animateDrop(event.weapon)
					}
				}
				is TriggerTrapEvent -> {
					if (event.trap == entity) {
						if (event.trap.trapUses() == 0) {
							setPosition(false)
						}
					}
				}
				is UseItemEvent -> {
					if (event.user == entity) {
						val item = event.item
						toast("used ${item.name}", Color.lightgray, ToastTextScene.LONG_CONFIG)

						if (item.itemUses() <= 0) {
							toast("-${item.name}", Color.gray, ToastTextScene.LONG_REVERSE_CONFIG)
						}
					}
				}
				is WeaponAttackEvent -> {
					if (event.attacker == entity) {
						animateBump(event.direction)
					}
				}
			}
		}
	}

	fun bind(context: Context, entity: Entity) {
		this.context = context
		this.entity = entity

		val entitySpriteOffset = entity.find<AppearancePart>()?.offset?.let { Vector2(it.first, it.second) } ?: Vector2.ZERO

		entitySprite.bind(entity)
		healthScene.bind(entity)
		healthScene.position = Vector2(0, -6) + entitySpriteOffset
		statusNode.position = entitySpriteOffset
		updateStatusLabel()
		updateDurabilityLabel()
		setPosition(shouldAnimate = false)
		resetAppearance()
	}

	fun animateBump(direction: Direction) {
		if (animationPlayer.isPlaying()) return

		when(direction) {
			Direction.North -> animationPlayer.play("bump_north")
			Direction.East -> animationPlayer.play("bump_east")
			Direction.South -> animationPlayer.play("bump_south")
			Direction.West -> animationPlayer.play("bump_west")
		}
	}

	fun animatePulse() {
		animationPlayer.play("pulse")
	}

	fun animateOnDamage(amount: Int) {
		toast(amount.toString(), Color.white, ToastTextScene.SHORT_CONFIG)

		if (entity.health() > 0) {
			animationPlayer.play("on_damage")
		}
	}

	fun animateOnHeal(amount: Int) {
		animationPlayer.play("on_heal")
		toast(amount.toString(), Color.mediumseagreen, ToastTextScene.MEDIUM_CONFIG)
	}

	fun animateOnBreak() {
		flashAnimator.stop()
		resetAppearance()
		animationPlayer.play("on_break")
	}

	fun animateOnDeath() {
		flashAnimator.stop()
		resetAppearance()
		animationPlayer.play("on_death")
	}

	fun animateDrop(dropped: Entity) {
		toast("-${dropped.name}", Color.gray, ToastTextScene.LONG_REVERSE_CONFIG)
	}

	fun animateOpen() {
		animationPlayer.play("on_open")
	}

	fun animateTake(taken: Entity) {
		toast("+${taken.name}", Color.lightgray, ToastTextScene.LONG_CONFIG)
	}

	fun animateTelegraph(isActive: Boolean) {
		if (isActive) {
			if (!flashAnimator.isPlaying()) flashAnimator.play("flash")
		} else {
			flashAnimator.stop()
			resetAppearance()
		}
	}

	fun animateThrown(event: ThrowWeaponEvent) {
		isTweening = true
		position = calculateNodePosition(event.from)
		appearance.visible = true

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

	@RegisterFunction
	fun onAnimationFinished(animation: String) {
		if (animation == "pulse") {
			resetAppearance()
		}
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
		when {
			entity.has<WeaponPart>() -> {
				durabilityLabel.visible = true
				durabilityLabel.text = entity.durability().toString()
			}
			entity.has<ItemPart>() -> {
				durabilityLabel.visible = true
				durabilityLabel.text = entity.itemUses().toString()
			}
			else -> {
				durabilityLabel.visible = false
				durabilityLabel.text = ""
			}
		}
	}

	private fun updateStatusLabel() {
		val statusEffects = entity.find<CombatPart>()?.statusEffects

		if (statusEffects.isNullOrEmpty()) {
			statusLabel.hide()
			return
		}

		var text = " "

		statusEffects.forEach {
			text += it.representation()
		}

		statusLabel.bbcodeText = text
		statusLabel.show()
	}

	private fun StatusEffect.representation(): String {
		return when (this) {
			is ColdStatusEffect -> "[color=#35dbd6]cold"
			is BurnStatusEffect -> "[color=#fc5203]burn"
			is PoisonStatusEffect -> "[color=#834acf]sick"
			else -> "?"
		} + " [$turnsRemaining][/color]"
	}

	private fun resetAppearance() {
		appearance.scale = Vector2(1, 1)
		appearance.zIndex = 0
		appearance.position = Vector2.ZERO
		appearance.modulate = Color(1, 1, 1, 1)
		entitySprite.modulate = Color(1, 1, 1, 1)
		healthScene.modulate = Color(1, 1, 1, 0.5)
		durabilityLabel.modulate = Color(1, 1, 1, 1)
	}

	private fun calculateNodePosition(worldPosition: Position): Vector2 {
		val defaultPosition = Vector2(worldPosition.x * WorldScene.TILE_SIZE, worldPosition.y * WorldScene.TILE_SIZE)
		val zIndexOffset = context.entitiesAt(worldPosition).indexOf(entity) / 10.0

		return defaultPosition + Vector2(0, zIndexOffset)
	}

	private fun calculateWorldPosition(nodePosition: Vector2): Position {
		return Position(
			(nodePosition.x / WorldScene.TILE_SIZE).roundToInt(),
			(nodePosition.y / WorldScene.TILE_SIZE).roundToInt())
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/entity/EntityScene.tscn"
	}
}
