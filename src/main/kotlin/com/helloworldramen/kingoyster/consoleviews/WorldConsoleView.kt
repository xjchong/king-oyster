package com.helloworldramen.kingoyster.consoleviews

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.DeathEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.parts.*

object WorldConsoleView : EventBusSubscriber {

    init {
        EventBus.register(this,
            DamageEvent::class,
            DeathEvent::class,
            GameOverEvent::class
        )
    }

    override fun receiveEvent(event: Event) {
        when (event) {
            is DamageEvent -> {
                val (_, target, value) = event
                println("The ${target.name} takes $value damage.")
            }
            is DeathEvent -> {
                println("The ${event.entity.name} dies.")
            }
            is GameOverEvent -> {
                if (event.isVictory) println("Victory!") else println("Game over...")
            }
        }
    }

    fun display(world: World, player: Entity? = null) {
        val visiblePositions = player?.visiblePositions() ?: listOf()
        val worldMemory = player?.find(MemoryPart::class)

        Position(world.width - 1, world.height - 1).forEach {
            if (it.x == 0) println()

            val appearance = when {
                player == null || visiblePositions.contains(it) -> {
                    world[it]?.lastOrNull()?.appearance() ?: "."
                }
                worldMemory?.get(it) != null -> {
                    (worldMemory[it]?.lastOrNull()?.appearance() ?: ".").color(ANSIColor.BG_GREEN)
                }
                else -> {
                    (world[it]?.lastOrNull()?.appearance() ?: ".").color(ANSIColor.BG_MAGENTA)
                }
            }

            print(appearance)
        }
        println()

        player?.let {
            displayEntityStatus(it)
        }
    }

    private fun displayEntityStatus(entity: Entity) {
//        println("Gil: ${entity.find(InventoryPart::class)?.money ?: 0}")
    }

    private fun Entity.appearance(): String {
        return when (name) {
            "player" -> "@".color(ANSIColor.YELLOW)
            "slime" -> "s".color(ANSIColor.BRIGHT_GREEN)
            "wall" -> "#"
            "stairs" -> "<"
            "coin" -> "$".color(ANSIColor.YELLOW)
            "door" -> {
                (if (this.find(DoorPart::class)?.isOpen == false) "+" else "'").color(ANSIColor.BLUE)
            }
            else -> "?"
        }
    }
}