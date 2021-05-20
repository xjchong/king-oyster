import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.oyster.*
import com.helloworldramen.kingoyster.parts.AscendablePart
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import kotlin.system.exitProcess



class ConsoleGameEngine : EventBusSubscriber {

    init {
        EventBus.register(this, GameOverEvent::class)
    }

    override fun receiveEvent(event: Event) {
        when(event) {
            is GameOverEvent -> {
                println("GAME OVER")
                EventBus.unregister(this)
                exitProcess(0)
            }
        }
    }

    fun run() {
        val world = World(19, 19)
        WorldGenerator.repopulate(world, DungeonGenerationStrategy)
        val context = Context(world)

        while (true) {
            val startTime = System.nanoTime()
            val player = world.update(context) ?: break
            println((System.nanoTime() - startTime) / 1000000.0)
            WorldConsoleView.display(context.world, player)

            while (true) {
                if (parseInput(context, player)) break
            }
        }
    }

    private fun parseInput(context: Context, player: Entity): Boolean {
        val world = context.world
        val currentPosition = world[player] ?: return false

        fun performDirectionActions(position: Position): Boolean {
            return player.respondToAction(Move(context, position)) ||
                    world[position].tryActions(
                        Open(context), Attack(context, player)
                    )
        }

        return when (readLine()) {
            "take" -> {
                val item = world[currentPosition]?.last {
                    it.has(ItemPart::class)
                }

                item?.respondToAction(Take(context, player)) ?: false
            }
            "n" -> performDirectionActions(currentPosition.north())
            "e" -> performDirectionActions(currentPosition.east())
            "s" -> performDirectionActions(currentPosition.south())
            "w" -> performDirectionActions(currentPosition.west())
            "up" -> {
                val stairs = world[currentPosition]?.firstOrNull { it.has(AscendablePart::class) }
                stairs?.respondToAction(Ascend(context, player)) == true
            }
            "quit" -> exitProcess(2)
            else -> false
        }
    }

    private fun List<Entity>?.tryActions(vararg actions: Action): Boolean {
        if (this.isNullOrEmpty()) return false

        return actions.firstOrNull { action ->
            lastOrNull { entity ->
                entity.respondToAction(action)
            } != null
        } != null
    }

}

fun main() {
    val gameEngine = ConsoleGameEngine()

    gameEngine.run()
}