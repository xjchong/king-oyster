package com.helloworldramen.kingoyster.consoleviews

import com.helloworldramen.kingoyster.entities.attributes.Appearance
import com.helloworldramen.kingoyster.game.GameWorld
import com.helloworldramen.kingoyster.models.Position

object WorldConsoleView {

    fun display(world: GameWorld) {
        Position(world.width - 1, world.height - 1).forEach {
            if (it.x == 0) println()

            world[it]?.lastOrNull()?.findAttribute(Appearance::class)?.let { appearance ->
                print(appearance.ascii)
            } ?: run {
                print('.')
            }
        }
        println()
    }
}