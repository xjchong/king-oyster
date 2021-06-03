package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.parts.EquipmentPart
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

	private val tileBucket: YSort by lazy { getNodeAs("TileBucket")!! }

	private val packedMemoryScene = GD.load<PackedScene>(MemoryScene.PATH)
	private val packedEntityScene = GD.load<PackedScene>(EntityScene.PATH)

	private val sceneForEntity: MutableMap<Entity, EntityScene> = mutableMapOf()

	fun bind(context: Context): EntityScene? {
		val world = context.world

		tileBucket.getChildren().forEach {
			tileBucket.removeChild(it as Node)
			it.queueFree()
		}

		sceneForEntity.clear()

		Position(world.width - 1, world.height - 1).forEach { position ->
			// Add the entities for this position.
			world[position]?.forEach { entity ->
				packedEntityScene?.instanceAs<EntityScene>()?.let {
					tileBucket.addChild(it)
					it.bind(context, entity)
					sceneForEntity[entity] = it
				}

				// Also add scenes for entities that are not explicitly visible.
				entity.find<EquipmentPart>()?.weapon?.let { weapon ->
					packedEntityScene?.instanceAs<EntityScene>()?.let {
						tileBucket.addChild(it)
						it.bind(context, weapon)
						sceneForEntity[weapon] = it
					}
				}
			}

			// Setup the memory for this position.
			packedMemoryScene?.instanceAs<MemoryScene>()?.let { memoryScene ->
				tileBucket.addChild(memoryScene)
				memoryScene.bind(context.player, position)
				memoryScene.position = Vector2(position.x * 32, position.y * 32)
			}
		}

		return sceneForEntity[context.player]
	}

	fun animateBump(entity: Entity, position: Position) {
		sceneForEntity[entity]?.animateBump(position)
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/world/WorldScene.tscn"

		const val TILE_SIZE = 32

		const val SELECTION_REASON_INTERACT = "interact"
	}
}
