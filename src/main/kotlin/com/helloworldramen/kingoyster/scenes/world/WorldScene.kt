package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.oyster.*
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.memory.MemoryScene
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.global.GD

@RegisterClass
class WorldScene : Node2D() {

	private val tileMap: TileMap by lazy { getNodeAs("TileMap")!! }

	var context: Context = Context.UNKNOWN()
	private var currentlyBoundLevel = 0
	private var player: Entity? = null
	private var sceneForEntity: MutableMap<Entity, EntityScene> = mutableMapOf()

	@RegisterFunction
	override fun _ready() {
		val world = World(17, 17)
		WorldGenerator.repopulate(world, DungeonGenerationStrategy)
		context = Context(world)
		player = world.update(context)
		context.player = player

		bind(context)
		currentlyBoundLevel = context.level
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		player?.let { parseInput(event, context, it) }
		if (currentlyBoundLevel != context.level) {
			bind(context)
			currentlyBoundLevel = context.level
		}
	}

	private fun parseInput(inputEvent: InputEvent, context: Context, player: Entity) {
		val world = context.world
		val currentPosition = world[player] ?: return

		fun performDirectionActions(position: Position): Boolean {
			return if (player.respondToAction(Move(context, player, position))) {
				true
			} else if (world[position].tryActions(Open(context, player)) != null) {
				true
			} else {
				sceneForEntity[player]?.animateBump(position)
				world[position].tryActions(
					Attack(context, player)
				) != null
			}
		}

		fun performStandingActions(): Boolean {
			val entity = world[currentPosition].tryActions(
				Take(context, player),
				Ascend(context, player)
			)

			sceneForEntity[entity]?.animatePulse()

			return entity != null
		}

		val isValidInput = when {
			inputEvent.isActionPressed("ui_up", true) -> {
				performDirectionActions(currentPosition.north())
			}
			inputEvent.isActionPressed("ui_right", true) -> {
				performDirectionActions(currentPosition.east())
			}
			inputEvent.isActionPressed("ui_down", true) -> {
				performDirectionActions(currentPosition.south())
			}
			inputEvent.isActionPressed("ui_left", true) -> {
				performDirectionActions(currentPosition.west())
			}
			inputEvent.isActionPressed("ui_accept") -> {
				performStandingActions()
			}
			else -> false
		}

		if (isValidInput) {
			this.player = world.update(context)
		}
	}

	fun bind(context: Context) {
		val world = context.world

		// Clear all the children.
		sceneForEntity.values.forEach {
			removeChild(it)
			it.queueFree()
		}

		sceneForEntity = mutableMapOf()
		tileMap.clear()
		tileMap.setOuterBorder(world)

		Position(world.width - 1, world.height - 1).forEach { position ->
			val floorScene = GD.load<PackedScene>(EntityScene.PATH)?.instance() as? EntityScene
			val memoryScene = GD.load<PackedScene>(MemoryScene.PATH)?.instance() as? MemoryScene

			// Set up the floor at this position.
			floorScene?.let {
				addChild(it)
				it.position = Vector2(position.x * 32, position.y * 32)
			}

			// Set up the memory at this position.
			memoryScene?.let {
				player?.let { player ->
					addChild(it)
					it.bind(player, position)
				}
				it.position = Vector2(position.x * 32, position.y * 32)
				it.zIndex = 1000
			}

			// Add the entities for this position.
			world[position]?.forEach { entity ->
				val entityScene = GD.load<PackedScene>(EntityScene.PATH)?.instance() as? EntityScene

				if (entityScene != null) {
					sceneForEntity[entity] = entityScene
					addChild(entityScene)
					entityScene.bind(context, entity)

					if (entity.name == "wall") {
						tileMap.setCell(position.x.toLong(), position.y.toLong(), 0)
					}
				}
			}
		}

		tileMap.updateBitmaskRegion(
			start = Vector2(-TILE_SIZE, -TILE_SIZE),
			end = Vector2(world.width * TILE_SIZE, world.height * TILE_SIZE))
	}

	private fun List<Entity>?.tryActions(vararg actions: Action): Entity? {
		if (this.isNullOrEmpty()) return null

		for (action in actions) {
			return lastOrNull { it.respondToAction(action) } ?: continue
		}

		return null
	}

	private fun TileMap.setOuterBorder(world: World) {
		for (x in (-1..world.width)) {
			setCell(x.toLong(), (-1).toLong(), 0)
			setCell(x.toLong(), world.height.toLong(), 0)
		}

		for (y in (0 until world.height)) {
			setCell((-1).toLong(), y.toLong(), 0)
			setCell(world.width.toLong(), y.toLong(), 0)
		}
	}

	companion object {
		const val TILE_SIZE = 32
	}
}
