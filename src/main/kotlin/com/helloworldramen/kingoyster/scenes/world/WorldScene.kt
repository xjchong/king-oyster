package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.memory.MemoryScene
import godot.*
import godot.annotation.RegisterClass
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD

@RegisterClass
class WorldScene : Node2D() {

	private val tileMap: TileMap by lazy { getNodeAs("TileMap")!! }
	private val entityScenesBucket: Node2D by lazy { getNodeAs("EntityScenesBucket")!! }
	private val memoryScenesBucket: Node2D by lazy { getNodeAs("MemoryScenesBucket")!! }

	private val packedMemoryScene = GD.load<PackedScene>(MemoryScene.PATH)
	private val packedEntityScene = GD.load<PackedScene>(EntityScene.PATH)

	fun bind(context: Context) {
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

		tileMap.clear()
		tileMap.setOuterBorder(world)

		Position(world.width - 1, world.height - 1).forEach { position ->
			// Add the entities for this position.
			world[position]?.forEach { entity ->
				packedEntityScene?.instanceAs<EntityScene>()?.let {
					entityScenesBucket.addChild(it)
					it.bind(context, entity)

					if (entity.name == "wall") {
						tileMap.setCell(position.x.toLong(), position.y.toLong(), 1)
					}
				}
			}

			// Setup the memory for this position.
			packedMemoryScene?.instanceAs<MemoryScene>()?.let { memoryScene ->
				memoryScenesBucket.addChild(memoryScene)
				memoryScene.bind(context.player, position)
				memoryScene.position = Vector2(position.x * 32, position.y * 32)
				memoryScene.zIndex = 1000
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
