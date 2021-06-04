package com.helloworldramen.kingoyster.scenes.game

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.ai.Ai
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.extensions.isPlayer
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.AscendEvent
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.eventbus.events.PlayerToastEvent
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.eventaudio.EventAudio
import com.helloworldramen.kingoyster.scenes.listmenu.ListMenuScene
import com.helloworldramen.kingoyster.scenes.mainmenu.MainMenuScene
import com.helloworldramen.kingoyster.scenes.screenshake.ScreenShake
import com.helloworldramen.kingoyster.scenes.toasttext.ToastTextScene
import com.helloworldramen.kingoyster.scenes.world.WorldScene
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import godot.Input
import godot.InputEvent
import godot.Label
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.extensions.getNodeAs
import java.util.*

@RegisterClass
class GameScene : Node2D(), EventBusSubscriber {

	private val eventAudio: EventAudio by lazy { getNodeAs("EventAudio")!! }
	private val worldScene: WorldScene by lazy { getNodeAs("WorldScene")!! }
	private val screenShake: ScreenShake by lazy { getNodeAs("Camera2D/ScreenShake")!! }
	private val listMenuScene: ListMenuScene by lazy { getNodeAs("UIScenesBucket/ListMenuScene")!! }
	private var playerScene: EntityScene? = null

	private val floorLabel: Label by lazy { getNodeAs("HUDLayer/FloorLabel")!! }

	private var context: Context = Context.UNKNOWN

	private val inputQueue: Queue<InputEvent> = ArrayDeque()
	private var isUpdating: Boolean = false
	private var doesWorldNeedFadeIn: Boolean = true

	override fun receiveEvent(event: Event) {
		when (event) {
			is AscendEvent -> {
				updateFloorLabel()
				worldScene.bind(context)
				doesWorldNeedFadeIn = true
			}
			is DamageEvent -> {
				if (event.source.isPlayer) {
					if (event.target.health() <= 0) {
						screenShake.startMediumShake()
					} else {
						screenShake.startSmallShake()
					}
				}
			}
			is GameOverEvent -> {
				getTree()?.changeScene(MainMenuScene.PATH)
			}
		}
	}

	@RegisterFunction
	override fun _ready() {
		val world = World(17, 17)
		val player = WorldGenerator.repopulate(world, DungeonGenerationStrategy)

		EventBus.register(this, AscendEvent::class, GameOverEvent::class, DamageEvent::class)

		context = Context(world)
		context.player = player

		playerScene = worldScene.bind(context)

		listMenuScene.pauseMode = PAUSE_MODE_PROCESS
		listMenuScene.hide()

		eventAudio.bind(context)
		eventAudio.pauseMode = PAUSE_MODE_PROCESS

		updateFloorLabel()
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		if (doesWorldNeedFadeIn) {
			doesWorldNeedFadeIn = false
			worldScene.fadeIn()
		}

		context.player.find<SensoryPart>()?.update(context, context.player)
		inputQueue.poll()?.let { parseInput(it) }
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (inputQueue.size <= MAX_INPUT_QUEUE_SIZE) {
			inputQueue.offer(event)
		}
	}

	private fun updateNonPlayerEntities() {
		if (isUpdating) return else isUpdating = true

		while (context.player.health() > 0) {
			val nextEntity = context.world.next() ?: break

			nextEntity.update(context, context.world)

			if (context.world.next() != nextEntity) continue

			if (nextEntity.isPlayer) {
				break
			} else {
				Ai.actForEntity(context, nextEntity)
			}
		}

		isUpdating = false
	}

	private fun parseInput(event: InputEvent) {
		val world = context.world
		val player = context.player
		val hasWeapon = player.equippedWeaponPart() != null

		when {
			Input.isActionPressed("left_modifier") -> {
				when {
					event.isDirectionPressed() -> performMovementDirectionSkill(event.direction())
					event.isActionPressed("ui_accept") -> println("ABILITY")
					event.isActionPressed("ui_cancel") -> println("EXAMINE")
					event.isActionPressed("secondary") -> println("ACCESSORY")
					event.isActionPressed("tertiary") -> println("ITEM")
				}
			}
			Input.isActionPressed("secondary") && event.isDirectionPressed() -> {
				when {
					!hasWeapon -> EventBus.post(PlayerToastEvent("No weapon", Color.lightgray))
					else -> performThrow(event.direction())
				}
			}
			Input.isActionPressed("tertiary") && event.isDirectionPressed() -> {
				when {
					!hasWeapon -> EventBus.post(PlayerToastEvent("No weapon", Color.lightgray))
					else -> println("WEAPON SKILL")
				}
			}
			event.isActionPressed("menu") -> println("MENU")
			event.isActionPressed("ui_cancel", true) -> player.idle(world) // TODO: Make this a guard action.
			event.isActionPressed("ui_accept") -> performStandingActions()
			event.isDirectionPressed(true) -> performDirectionActions(event.direction(true))
			else -> return
		}

		updateNonPlayerEntities()
	}

	private fun performThrow(direction: Direction?) {
		if (direction == null) return

		val player = context.player

		player.respondToAction(ThrowWeapon(context, player, direction))
	}

	private fun performMovementDirectionSkill(direction: Direction?) {
		if (direction == null) return

		val player = context.player
		val currentPosition = context.positionOf(player) ?: return
		val furthestPassablePosition = context.straightPathWhile(currentPosition, direction) { position ->
			val entities = context.entitiesAt(position)

			entities != null && (entities.all { it.isPassable() } || entities.contains(player))
		}.lastOrNull() ?: return

		player.respondToAction(Move(context, player, furthestPassablePosition, MoveType.Charge))
	}

	private fun performDirectionActions(direction: Direction?) {
		if (direction == null) return

		val player = context.player
		val currentPosition = context.positionOf(player) ?: return
		val actionPosition = direction.vector + currentPosition

		if (player.time > context.world.currentTime // Don't read any direction input when not player's turn.
			|| player.respondToAction(Move(context, player, actionPosition))
			|| context.world.respondToActions(actionPosition,
				Open(context, player),
				WeaponAttack(context, player)
			) != null
		) return

		// If we didn't successfully perform a direction action, indicate the failure with a bump animation.
		worldScene.animateBump(player, actionPosition)
	}

	private fun performStandingActions() {
		with(context) {
			val currentPosition = positionOf(player)

			when {
				currentPosition == null -> return
				world.respondToActions(currentPosition, Take(this, player)) != null -> return
				world.respondToActions(currentPosition, Ascend(this, player)) != null -> {
					worldScene.fadeOut()
				}
				else -> playerScene?.toast("?", Color.lightgray, ToastTextScene.SHORT_CONFIG)
			}
		}
	}

	private fun InputEvent.direction(allowEcho: Boolean = false): Direction? {
		return when {
			isActionPressed("ui_up", allowEcho) -> Direction.North
			isActionPressed("ui_right", allowEcho) -> Direction.East
			isActionPressed("ui_down", allowEcho) -> Direction.South
			isActionPressed("ui_left", allowEcho) -> Direction.West
			else -> null
		}
	}

	private fun InputEvent.isDirectionPressed(allowEcho: Boolean = false): Boolean {
		return direction(allowEcho) != null
	}

	private fun updateFloorLabel() {
		floorLabel.text = "Floor ${context.level}"
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/game/GameScene.tscn"

		private const val MAX_INPUT_QUEUE_SIZE = 2
	}
}
