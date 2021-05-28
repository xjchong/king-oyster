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
	private var lastEntity: Entity? = null
	private var lastEntityTime: Double? = null

	override fun receiveEvent(event: Event) {
		when (event) {
			is AscendEvent -> {
				with(context) {
					player.update(this, world)
					worldScene.bind(this)
				}
			}
			is GameOverEvent -> getTree()?.changeScene(MainMenuScene.PATH)
		}
	}

	@RegisterFunction
	override fun _ready() {
		val world = World(17, 17)
		val player = WorldGenerator.repopulate(world, DungeonGenerationStrategy)

		EventBus.register(this, AscendEvent::class, GameOverEvent::class)

		context = Context(world)
		context.player = player

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
		parseInput(event)
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

	private fun parseInput(event: InputEvent) {
		val world = context.world
		val player = context.player
		val currentPosition = world[player] ?: return

		when {
			event.isActionPressed("ui_up", true) -> performDirectionActions(currentPosition.north())
			event.isActionPressed("ui_right", true) -> performDirectionActions(currentPosition.east())
			event.isActionPressed("ui_down", true) -> performDirectionActions(currentPosition.south())
			event.isActionPressed("ui_left", true) -> performDirectionActions(currentPosition.west())
			event.isActionPressed("ui_accept") -> {
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
			event.isActionPressed("ui_cancel", true) -> player.idle(world)
		}
	}

	private fun getNearbyInteractivePositions(position: Position): List<Position> {
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

	private fun performDirectionActions(position: Position) {
		val player = context.player

		if (player.respondToAction(Move(context, player, position))
			|| context.world.respondToActions(position,
				Open(context, player),
				Attack(context, player)
			) != null
		) return

		// If we didn't successfully perform a direction action, indicate the failure with a bump animation.
		worldScene.animateBump(player, position)
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
	}
}
