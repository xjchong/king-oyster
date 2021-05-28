package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.ai.Ai
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.AscendEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.parts.DoorPart
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.mainmenu.MainMenuScene
import com.helloworldramen.kingoyster.scenes.memory.MemoryScene
import com.helloworldramen.kingoyster.scenes.tileselection.TileSelectionScene
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD

@RegisterClass
class WorldScene : Node2D(), EventBusSubscriber {

	private val tileMap: TileMap by lazy { getNodeAs("TileMap")!! }
	private val entityScenesBucket: Node2D by lazy { getNodeAs("EntityScenesBucket")!! }
	private val memoryScenesBucket: Node2D by lazy { getNodeAs("MemoryScenesBucket")!! }
	private val tileSelectionScene: TileSelectionScene by lazy { getNodeAs("TileSelectionScene")!! }

	private val packedMemoryScene = GD.load<PackedScene>(MemoryScene.PATH)
	private val packedEntityScene = GD.load<PackedScene>(EntityScene.PATH)

	var context: Context = Context.UNKNOWN
	private var player: Entity? = null
	private var lastEntity: Entity? = null
	private var lastEntityTime: Double? = null
	private val sceneForEntity: MutableMap<Entity, EntityScene> = mutableMapOf()

	override fun receiveEvent(event: Event) {
		when (event) {
			is AscendEvent -> {
				player?.update(context, context.world)
				bind(context)
			}
			is GameOverEvent -> getTree()?.changeScene(MainMenuScene.PATH)
		}
	}

	@RegisterFunction
	override fun _ready() {
		val world = World(17, 17)

		EventBus.register(this, AscendEvent::class, GameOverEvent::class)

		tileSelectionScene.bind(world.width, world.height)
		tileSelectionScene.pauseMode = PauseMode.PAUSE_MODE_PROCESS.id
		tileSelectionScene.signalTilesSelected.connect(this, ::onTilesSelected)

		player = WorldGenerator.repopulate(world, DungeonGenerationStrategy)
		context = Context(world)
		context.player = player!!

		bind(context)
	}

	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		updateNextEntity()
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		player?.let { parseInput(event, context, it) }
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
						position -> it.has<ItemPart>()
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
							SELECTION_REASON_INTERACT,
							interactivePositions.map { listOf(it) })
					}
				}
			}
			inputEvent.isActionPressed("ui_cancel", true) -> player.idle(world)
			inputEvent.isActionPressed("ui_select") -> {
				getTree()?.paused = true
				tileSelectionScene.startTileSelection(
					SELECTION_REASON_TEST,
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

	private fun bind(context: Context) {
		val world = context.world

		// Wipe all the scenes.
		entityScenesBucket.getChildren().forEach {
			entityScenesBucket.removeChild(it as Node)
			it.queueFree()
		}

		memoryScenesBucket.getChildren().forEach {
			memoryScenesBucket.removeChild(it as Node)
			it.queueFree()
		}

		sceneForEntity.clear()
		tileMap.clear()
		tileMap.setOuterBorder(world)

		Position(world.width - 1, world.height - 1).forEach { position ->
			// Add the entities for this position.
			world[position]?.forEach { entity ->
				packedEntityScene?.instanceAs<EntityScene>()?.let {
					sceneForEntity[entity] = it
					entityScenesBucket.addChild(it)
					it.bind(context, entity)

					if (entity.name == "wall") {
						tileMap.setCell(position.x.toLong(), position.y.toLong(), 1)
					}
				}
			}

			// Setup the memory for this position.
			packedMemoryScene?.instanceAs<MemoryScene>()?.let { memoryScene ->
				player?.let { player ->
					memoryScenesBucket.addChild(memoryScene)
					memoryScene.bind(player, position)
					memoryScene.position = Vector2(position.x * 32, position.y * 32)
					memoryScene.zIndex = 1000
				} ?: run {
					memoryScene.queueFree()
				}
			}
		}

		tileMap.updateBitmaskRegion(
			start = Vector2(-TILE_SIZE, -TILE_SIZE),
			end = Vector2(world.width * TILE_SIZE, world.height * TILE_SIZE))
	}

	private fun TileMap.setOuterBorder(world: World) {
		for (x in (-1..world.width)) {
			setCell(x.toLong(), (-1).toLong(), 1)
			setCell(x.toLong(), world.height.toLong(), 1)
		}

		for (y in (0 until world.height)) {
			setCell((-1).toLong(), y.toLong(), 1)
			setCell(world.width.toLong(), y.toLong(), 1)
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/world/WorldScene.tscn"
		const val TILE_SIZE = 32

		const val SELECTION_REASON_INTERACT = "interact"
		const val SELECTION_REASON_TEST = "test"
	}
}
