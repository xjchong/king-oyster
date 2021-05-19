package com.helloworldramen.kingoyster.consoleviews

import com.helloworldramen.kingoyster.entities.Door
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.entities.Player
import com.helloworldramen.kingoyster.entities.Stairs
import com.helloworldramen.kingoyster.entities.Wall
import com.helloworldramen.kingoyster.parts.Physical
import com.helloworldramen.kingoyster.parts.Portal

object WorldConsoleView {

    fun display(world: World) {
        Position(world.width - 1, world.height - 1).forEach {
            if (it.x == 0) println()

            world[it]?.lastOrNull()?.let { entity ->
                print(entity.appearance())
            } ?: run {
                print('.')
            }
        }
        println()
    }

    private fun Entity.appearance(): Char {
        return when (this) {
            is Player -> '@'
            is Wall -> '#'
            is Stairs -> '<'
            is Door -> {
                if (this.find(Portal::class)?.isOpen == false) '+' else '\''
            }
            else -> '?'
        }
    }
}