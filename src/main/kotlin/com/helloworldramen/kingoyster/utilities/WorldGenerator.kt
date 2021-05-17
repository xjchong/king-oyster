package com.helloworldramen.kingoyster.utilities

import com.helloworldramen.kingoyster.entities.FeatureFactory
import com.helloworldramen.kingoyster.models.Position
import com.helloworldramen.kingoyster.models.World

object WorldGenerator {

    fun generate(width: Int, height: Int): World {
        return World(width, height).apply {
            Position(width - 1, height - 1).forEach {
                if (it.y == 0 || it.x == 0 || it.x == width - 1 || it.y == height - 1) {
                    get(it)?.plusAssign(FeatureFactory.wall())
                }
            }
        }
    }
}