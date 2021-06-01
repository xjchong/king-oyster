package com.helloworldramen.kingoyster.scenes.game

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.ai.Ai
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.entities.isPlayer
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.AscendEvent
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.eventaudio.EventAudio
import com.helloworldramen.kingoyster.scenes.listmenu.ListMenuScene
import com.helloworldramen.kingoyster.scenes.mainmenu.MainMenuScene
import com.helloworldramen.kingoyster.scenes.screenshake.ScreenShake
import com.helloworldramen.kingoyster.scenes.tileselection.TileSelectionScene
import com.helloworldramen.kingoyster.scenes.world.WorldScene
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import godot.Input
import godot.InputEvent
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs
import java.util.*
import kotlin.random.Random

@RegisterClass
class GameScene : Node2D(), EventBusSubscriber {

	private val eventAudio: EventAudio by lazy { getNodeAs("EventAudio")!! }
	private val worldScene: WorldScene by lazy { getNodeAs("WorldScene")!! }
	private val screenShake: ScreenShake by lazy { getNodeAs("Camera2D/ScreenShake")!! }
	private val tileSelectionScene: TileSelectionScene by lazy { getNodeAs("TileSelectionScene")!! }
	private val listMenuScene: ListMenuScene by lazy { getNodeAs("UIScenesBucket/ListMenuScene")!! }
	private var playerScene: EntityScene? = null

	private var context: Context = Context.UNKNOWN

	private val inputQueue: Queue<InputEvent> = ArrayDeque()

	override fun receiveEvent(event: Event) {
		when (event) {
			is AscendEvent -> {
				with(context) {
					player.update(this, world)
					worldScene.bind(this)
				}
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
			is GameOverEvent -> getTree()?.changeScene(MainMenuScene.PATH)
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

		tileSelectionScene.bind(world.width, world.height)
		tileSelectionScene.pauseMode = PAUSE_MODE_PROCESS
		tileSelectionScene.signalTilesSelected.connect(this, ::onTilesSelected)

		listMenuScene.pauseMode = PAUSE_MODE_PROCESS
		listMenuScene.hide()

		eventAudio.bind(context)
		eventAudio.pauseMode = PAUSE_MODE_PROCESS
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		updateNextEntity()
		inputQueue.poll()?.let { parseInput(it) }
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (inputQueue.size <= MAX_INPUT_QUEUE_SIZE) {
			inputQueue.offer(event)
		}
	}

	@RegisterFunction
	fun onTilesSelected(selectionReason: String) {
		getTree()?.paused = false

		when (selectionReason) {
			SELECTION_REASON_INTERACT -> {
				tileSelectionScene.selection.firstOrNull()?.let {
					performInteractiveActions(it)
				}
			}
		}
	}

	private fun updateNextEntity() {
		context.world.next()?.let { entity ->
			entity.update(context, context.world)

			if (entity.name != "player") {
				Ai.actForEntity(context, entity)
			}
		}
	}

	private fun parseInput(event: InputEvent) {
		val world = context.world
		val player = context.player
		val currentPosition = world[player] ?: return

		when {
			Input.isActionPressed("left_modifier") -> {
				val direction = event.pressedDirection()
				when {
					Input.isActionPressed("ui_accept") -> {
						if (direction == null) {
							// Show weapon skill guide.
						} else performWeaponDirectionSkill(direction)
					}
					Input.isActionPressed("ui_cancel") -> {
						if (direction == null) {
							// Show throw guide.
						} else throwWeapon(direction)
					}
				}
			}
			event.isActionPressed("ui_up", true) -> performDirectionActions(currentPosition.north())
			event.isActionPressed("ui_right", true) -> performDirectionActions(currentPosition.east())
			event.isActionPressed("ui_down", true) -> performDirectionActions(currentPosition.south())
			event.isActionPressed("ui_left", true) -> performDirectionActions(currentPosition.west())
			event.isActionPressed("ui_cancel", true) -> player.idle(world)
			event.isActionPressed("ui_accept") -> performNearbyInteractiveActions()
			event.isActionPressed("ui_select") -> {
				getTree()?.paused = true
				val testTitles = mutableListOf<String>()

				repeat(Random.nextInt(1, 10)) {
					testTitles.add("Test $it")
				}

				listMenuScene.bind("Test Titles", testTitles) {
					when {
						it < 0 -> println("cancelled")
						else -> println("picked test title: ${testTitles[it]}")
					}

					listMenuScene.hide()
					getTree()?.paused = false
				}

				listMenuScene.show()
			}
		}
	}

	private fun performNearbyInteractiveActions() {
		val currentPosition = context.positionOf(context.player) ?: return
		val interactivePositions = getNearbyInteractivePositions(currentPosition)

		when (interactivePositions.size) {
			0 -> return
			1 -> performInteractiveActions(interactivePositions.first())
			else -> {
				getTree()?.paused = true
				tileSelectionScene.startTileSelection(
					WorldScene.SELECTION_REASON_INTERACT,
					interactivePositions.map { listOf(it) })
			}
		}
	}

	private fun getNearbyInteractivePositions(position: Position): List<Position> {
		val nearbyPositions = listOf(position) + position.neighbors()

		return nearbyPositions.filter { nearbyPosition ->
			context.entitiesAt(nearbyPosition)?.any {
				when (nearbyPosition) {
					position -> it.has<ItemPart>() || it.has<AscendablePart>() || it.has<WeaponPart>()
					else -> it.has<DoorPart>() || (it.has<CombatPart>() && it.isEnemyOf(context.player))
				}
			} == true
		}
	}

	private fun throwWeapon(direction: Direction) {
		val player = context.player

		player.respondToAction(ThrowWeapon(context, player, direction))
	}

	private fun performWeaponDirectionSkill(direction: Direction) {
		val player = context.player
		val currentPosition = context.positionOf(player) ?: return
		val furthestPassablePosition = context.straightPathWhile(currentPosition, direction) { position ->
			val entities = context.entitiesAt(position)

			entities != null && (entities.all { it.isPassable() } || entities.contains(player))
		}.lastOrNull() ?: return

		player.respondToAction(Move(context, player, furthestPassablePosition, MoveType.Charge))
	}

	private fun performDirectionActions(position: Position) {
		val player = context.player

		if (player.time > context.world.currentTime // Don't read any direction input when not player's turn.
			|| player.respondToAction(Move(context, player, position))
			|| context.world.respondToActions(position,
				Open(context, player),
				WeaponAttack(context, player)
			) != null
		) return

		// If we didn't successfully perform a direction action, indicate the failure with a bump animation.
		worldScene.animateBump(player, position)
	}

	private fun performInteractiveActions(position: Position) {
		with(context) {
			world.respondToActions(position,
				WeaponAttack(this, player),
				Take(this, player),
				EquipAsWeapon(this, player),
				Open(this, player),
				Close(this, player),
				Ascend(this, player)
			)
		}
	}

	private fun InputEvent.pressedDirection(): Direction? {
		return when {
			isActionPressed("ui_up") -> Direction.North
			isActionPressed("ui_right") -> Direction.East
			isActionPressed("ui_down") -> Direction.South
			isActionPressed("ui_left") -> Direction.West
			else -> null
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/game/GameScene.tscn"

		private const val SELECTION_REASON_INTERACT = "interact"
		private const val MAX_INPUT_QUEUE_SIZE = 2
	}
}
