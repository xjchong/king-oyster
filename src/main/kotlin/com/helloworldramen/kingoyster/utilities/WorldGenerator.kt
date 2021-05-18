package com.helloworldramen.kingoyster.utilities

import com.helloworldramen.kingoyster.entities.factories.FeatureFactory
import com.helloworldramen.kingoyster.game.GameContext
import com.helloworldramen.kingoyster.game.GameWorld
import com.helloworldramen.kingoyster.models.Position
import com.helloworldramen.kingoyster.oyster.World

object WorldGenerator {

    fun generate(width: Int, height: Int): GameWorld {
        return GameWorld(width, height).apply {
            Position(width - 1, height - 1).forEach {
                if (it.y == 0 || it.x == 0 || it.x == width - 1 || it.y == height - 1) {
                    add(FeatureFactory.wall(), it)
                }
            }
        }
    }
}