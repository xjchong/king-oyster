package com.helloworldramen.kingoyster.consoleviews

import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.parts.*

object WorldConsoleView {

    fun display(world: World, player: Entity) {
        val visiblePositions = player.find(SensoryPart::class)?.visiblePositions ?: listOf()
        val worldMemory = player.find(MemoryPart::class)?.worldMemory ?: mapOf()
        println(worldMemory)

        Position(world.width - 1, world.height - 1).forEach {
            if (it.x == 0) println()

            val appearance = when {
                visiblePositions.contains(it) -> {
                    world[it]?.lastOrNull()?.appearance() ?: "."
                }
                worldMemory[it] != null -> {
                    (worldMemory[it]?.lastOrNull()?.appearance() ?: ".").color(ANSIColor.BG_GREEN)
                }
                else -> {
                    (world[it]?.lastOrNull()?.appearance() ?: ".").color(ANSIColor.BG_MAGENTA)
                }
            }

            print(appearance)
        }
        println()
        displayEntityStatus(player)
    }

    private fun displayEntityStatus(entity: Entity) {
        println("Gil: ${entity.find(InventoryPart::class)?.money ?: 0}")
    }

    private fun Entity.appearance(): String {
        return when (name) {
            "player" -> "@".color(ANSIColor.YELLOW)
            "wall" -> "#"
            "stairs" -> "<"
            "coin" -> "$".color(ANSIColor.YELLOW)
            "door" -> {
                (if (this.find(PortalPart::class)?.isOpen == false) "+" else "'").color(ANSIColor.BLUE)
            }
            else -> "?"
        }
    }
}