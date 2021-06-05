package com.helloworldramen.kingoyster.worldgen.generation

import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World

object EmptyGenerationStrategy : GenerationStrategy() {

    override fun generate(width: Int, height: Int, playerPosition: Position?): World {
        return World(width, height)
    }
}