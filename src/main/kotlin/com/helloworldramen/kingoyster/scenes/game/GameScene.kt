package com.helloworldramen.kingoyster.scenes.game

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.ai.Ai
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.AscendEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.parts.AscendablePart
import com.helloworldramen.kingoyster.parts.DoorPart
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.mainmenu.MainMenuScene
import com.helloworldramen.kingoyster.scenes.tileselection.TileSelectionScene
import com.helloworldramen.kingoyster.scenes.world.WorldScene
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import godot.InputEvent
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class GameScene : Node2D(), EventBusSubscriber {

	private val worldScene: WorldScene by lazy { getNodeAs("WorldScene")!! }
	private val tileSelectionScene: TileSelectionScene by lazy { getNodeAs("TileSelectionScene")!! }

	private var context: Context = Context.UNKNOWN
	private var player: Entity? = null
	private var lastEntity: Entity? = null
	private var lastEntityTime: Double? = null
	private val sceneForEntity: MutableMap<Entity, EntityScene> = mutableMapOf()

	override fun receiveEvent(event: Event) {
		when (event) {
			is AscendEvent -> {
				player?.update(context, context.world)
				worldScene.bind(context)
			}
			is GameOverEvent -> getTree()?.changeScene(MainMenuScene.PATH)
		}
	}

	@RegisterFunction
	override fun _ready() {
		val world = World(17, 17)

		EventBus.register(this, AscendEvent::class, GameOverEvent::class)

		player = WorldGenerator.repopulate(world, DungeonGenerationStrategy)
		context = Context(world)
		context.player = player!!

		worldScene.bind(context)

		tileSelectionScene.bind(world.width, world.height)
		tileSelectionScene.pauseMode = PauseMode.PAUSE_MODE_PROCESS.id
		tileSelectionScene.signalTilesSelected.connect(this, ::onTilesSelected)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		updateNextEntity()
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		player?.let {
			parseInput(event, context, it)
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
			SELECTION_REASON_TEST -> {
				println("Tiles selected: ${tileSelectionScene.selection}")
			}
		}
	}

	private fun updateNextEntity() {
		val nextEntity = context.world.next()

		if (nextEntity != lastEntity || lastEntity?.time != lastEntityTime) {
			lastEntity = nextEntity
			lastEntityTime = nextEntity?.time

			nextEntity?.let {
				it.update(context, context.world)

				if (it.name != "player") {
					Ai.actForEntity(context, it)
				}
			}
		}
	}

	private fun parseInput(inputEvent: InputEvent, context: Context, player: Entity) {
		val world = context.world
		val currentPosition = world[player] ?: return

		fun performActions(position: Position) {
			when {
				position == currentPosition -> performInteractiveActions(position)
				player.respondToAction(Move(context, player, position)) -> return
				world.respondToActions(position, Open(context, player)) != null -> return
				world.respondToActions(position, Attack(context, player)) != null -> return
				else -> sceneForEntity[player]?.animateBump(position)
			}
		}

		fun getNearbyInteractivePositions(position: Position): List<Position> {
			val nearbyPositions = listOf(position) + position.neighbors()

			return nearbyPositions.filter { nearbyPosition ->
				context.entitiesAt(nearbyPosition)?.any {
					when (nearbyPosition) {
						position -> it.has<ItemPart>() || it.has<AscendablePart>()
						else -> it.has<DoorPart>()
					}
				} == true
			}
		}

		when {
			inputEvent.isActionPressed("ui_up", true) -> performActions(currentPosition.north())
			inputEvent.isActionPressed("ui_right", true) -> performActions(currentPosition.east())
			inputEvent.isActionPressed("ui_down", true) -> performActions(currentPosition.south())
			inputEvent.isActionPressed("ui_left", true) -> performActions(currentPosition.west())
			inputEvent.isActionPressed("ui_accept") -> {
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
			inputEvent.isActionPressed("ui_cancel", true) -> player.idle(world)
			inputEvent.isActionPressed("ui_select") -> {
				getTree()?.paused = true
				tileSelectionScene.startTileSelection(
					WorldScene.SELECTION_REASON_TEST,
					currentPosition.neighbors().map { listOf(it) })
			}
		}
	}

	private fun performInteractiveActions(position: Position) {
		with(context) {
			world.respondToActions(position,
				Take(this, player),
				Open(this, player),
				Close(this, player),
				Ascend(this, player)
			)
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/game/GameScene.tscn"

		const val SELECTION_REASON_INTERACT = "interact"
		const val SELECTION_REASON_TEST = "test"
	}
}
