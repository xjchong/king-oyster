package com.helloworldramen.kingoyster.consoleviews

import com.helloworldramen.kingoyster.entities.features.Door
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.entities.actors.Player
import com.helloworldramen.kingoyster.entities.features.Stairs
import com.helloworldramen.kingoyster.entities.features.Wall
import com.helloworldramen.kingoyster.entities.items.Coin
import com.helloworldramen.kingoyster.parts.InventoryPart
import com.helloworldramen.kingoyster.parts.PortalPart
import com.helloworldramen.kingoyster.parts.SensoryPart

object WorldConsoleView {

    fun display(world: World, player: Entity) {
        val visiblePositions = player.find(SensoryPart::class)?.visiblePositions ?: listOf()

        Position(world.width - 1, world.height - 1).forEach {
            if (it.x == 0) println()

            val appearance = world[it]?.lastOrNull()?.appearance() ?: "."

            print(if (visiblePositions.contains(it)) appearance else appearance.color(ANSIColor.BG_MAGENTA))
        }
        println()
        displayEntityStatus(player)
    }

    private fun displayEntityStatus(entity: Entity) {
        println("Gil: ${entity.find(InventoryPart::class)?.money ?: 0}")
    }

    private fun Entity.appearance(): String {
        return when (this) {
            is Player -> "@".color(ANSIColor.YELLOW)
            is Wall -> "#"
            is Stairs -> "<"
            is Coin -> "$".color(ANSIColor.YELLOW)
            is Door -> {
                (if (this.find(PortalPart::class)?.isOpen == false) "+" else "'").color(ANSIColor.BLUE)
            }
            else -> ""
        }
    }
}