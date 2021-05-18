package com.helloworldramen.kingoyster.consoleviews

import com.helloworldramen.kingoyster.entities.attributes.AppearanceInfo
import com.helloworldramen.kingoyster.models.Position
import com.helloworldramen.kingoyster.oyster.World

object WorldConsoleView {

    fun display(world: World) {
        Position(world.width - 1, world.height - 1).forEach {
            if (it.x == 0) println()

            world[it]?.lastOrNull()?.findAttribute(AppearanceInfo::class)?.let { appearance ->
                print(appearance.ascii)
            } ?: run {
                print('.')
            }
        }
    }
}