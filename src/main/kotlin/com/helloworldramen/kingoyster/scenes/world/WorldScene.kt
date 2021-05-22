package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.oyster.*
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.fog.FogScene
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import godot.InputEvent
import godot.Node
import godot.Node2D
import godot.PackedScene
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2
import godot.extensions.instanceAs
import godot.global.GD

@RegisterClass
class WorldScene : Node2D() {

	var context: Context = Context.UNKNOWN()
	private var currentlyBoundLevel = 0
	private var player: Entity? = null

	@RegisterFunction
	override fun _ready() {
		val world = World(17, 17)
		WorldGenerator.repopulate(world, DungeonGenerationStrategy)
		context = Context(world)

		bind(context)
		currentlyBoundLevel = context.level
		player = world.update(context)
		context.player = player
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
			return player.respondToAction(Move(context, player, position)) ||
					world[position].tryActions(
						Open(context, player), Attack(context, player)
					)
		}

		fun performStandingActions(): Boolean {
			return world[currentPosition].tryActions(
				Take(context, player),
				Ascend(context, player)
			)
		}

		val isValidInput = when {
			inputEvent.isActionPressed("ui_up") -> {
				performDirectionActions(currentPosition.north())
			}
			inputEvent.isActionPressed("ui_right") -> {
				performDirectionActions(currentPosition.east())
			}
			inputEvent.isActionPressed("ui_down") -> {
				performDirectionActions(currentPosition.south())
			}
			inputEvent.isActionPressed("ui_left") -> {
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
		getChildren().forEach {
			removeChild(it as Node)
			it.queueFree()
		}

		Position(world.width - 1, world.height - 1).forEach { position ->
			val floorScene = GD.load<PackedScene>(EntityScene.PATH)?.instance() as? EntityScene
			val fogScene = GD.load<PackedScene>(FogScene.PATH)?.instance() as? FogScene

			// Set up the floor at this position.
			floorScene?.let {
				addChild(it)
				it.position = Vector2(position.x * 32, position.y * 32)
			}

			// Set up the fog at this position.
			fogScene?.let {
				addChild((it))
				it.bind(context, position)
				it.position = Vector2(position.x * 32, position.y * 32)
			}

			// Add the entities for this position.
			world[position]?.forEach { entity ->
				val entityScene = GD.load<PackedScene>(EntityScene.PATH)?.instance() as? EntityScene

				if (entityScene != null) {
					addChild(entityScene)
					entityScene.bind(context, entity)
				}
			}
		}
	}

	private fun List<Entity>?.tryActions(vararg actions: Action): Boolean {
		if (this.isNullOrEmpty()) return false

		return actions.firstOrNull { action ->
			lastOrNull { entity ->
				entity.respondToAction(action)
			} != null
		} != null
	}
}
