package com.helloworldramen.kingoyster.utilities.worldgen.generation

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World

abstract class GenerationStrategy {

    abstract fun generate(width: Int, height: Int, playerPosition: Position?): World

    protected fun World.fill(entityFactory: () -> Entity) {
        clear()
        Position(width - 1, height - 1).forEach {
            add(entityFactory(), it)
        }
    }

    protected fun Position.isOutOfBounds(world: World): Boolean {
        return x <= 0 || y <= 0 || x >= world.width - 1 || y >= world.height - 1
    }
}