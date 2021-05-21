package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import godot.Node2D
import godot.PackedScene
import godot.Resource
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.global.GD

@RegisterClass
class WorldScene : Node2D() {

//    val entityScenes: MutableList<EntityScene> = mutableListOf()
	var context: Context = Context.UNKNOWN()

	@RegisterFunction
	override fun _ready() {
		val world = World(19, 19)
		WorldGenerator.repopulate(world, DungeonGenerationStrategy)
		context = Context(world)

		bind(context)
	}

	fun bind(context: Context) {
		val world = context.world

		Position(world.width - 1, world.height - 1).forEach { position ->
			world[position]?.forEach { entity ->
				val entityScene = GD.load<PackedScene>(EntityScene.PATH)?.instance() as? EntityScene

				if (entityScene != null) {
					addChild(entityScene)
					entityScene.bind(context, entity)
				}
			}
		}

	}
}
