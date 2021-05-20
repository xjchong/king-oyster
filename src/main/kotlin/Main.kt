import com.helloworldramen.kingoyster.actions.Ascend
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.actions.Open
import com.helloworldramen.kingoyster.actions.Take
import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.GameOver
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.parts.AscendablePart
import com.helloworldramen.kingoyster.parts.ItemPart
import com.helloworldramen.kingoyster.utilities.worldgen.DrunkGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import kotlin.system.exitProcess



class ConsoleGameEngine : EventBusSubscriber {

    private val shouldLogTime: Boolean = true

    init {
        EventBus.register(this, GameOver::class)
    }

    override fun receiveEvent(event: Event) {
        when(event) {
            is GameOver -> {
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

        return when (readLine()) {
            "take" -> {
                val item = world[currentPosition]?.first {
                    it.has(ItemPart::class)
                }

                item?.respondToAction(Take(context, player)) ?: false
            }
            "n" -> {
                player.respondToAction(Move(context, currentPosition.north()))
                        || world[currentPosition.north()]?.firstOrNull { it.respondToAction(Open(context)) } != null
            }
            "e" -> {
                player.respondToAction(Move(context, currentPosition.east()))
                        || world[currentPosition.east()]?.firstOrNull { it.respondToAction(Open(context)) } != null
            }
            "s" -> {
                player.respondToAction(Move(context, currentPosition.south()))
                        || world[currentPosition.south()]?.firstOrNull { it.respondToAction(Open(context)) } != null
            }
            "w" -> {
                player.respondToAction(Move(context, currentPosition.west()))
                        || world[currentPosition.west()]?.firstOrNull { it.respondToAction(Open(context)) } != null
            }
            "up" -> {
                val stairs = world[currentPosition]?.firstOrNull { it.has(AscendablePart::class) }
                stairs?.respondToAction(Ascend(context, player)) == true
            }
            else -> false
        }
    }

}

fun main() {
    val gameEngine = ConsoleGameEngine()

    gameEngine.run()
}