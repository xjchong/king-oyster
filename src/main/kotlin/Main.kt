import com.helloworldramen.kingoyster.actions.Ascend
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.GameOver
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.parts.Ascendable
import com.helloworldramen.kingoyster.utilities.worldgen.DrunkGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import kotlin.system.exitProcess



class ConsoleGameEngine : EventBusSubscriber {

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
        val world = World(80, 24)
        WorldGenerator.repopulate(world, DungeonGenerationStrategy)
        val context = Context(world)

        while (true) {
            WorldConsoleView.display(context.world)
            val player = world.update(context) ?: break

            while (true) {
                if (parseInput(context, player)) break
            }
        }
    }

    private fun parseInput(context: Context, player: Entity): Boolean {
        val currentPosition = context.world[player] ?: return false

        return when (readLine()) {
            "north" -> player.respondToAction(Move(context, currentPosition.north()))
            "east" -> player.respondToAction(Move(context, currentPosition.east()))
            "south" -> player.respondToAction(Move(context, currentPosition.south()))
            "west" -> player.respondToAction(Move(context, currentPosition.west()))
            "ascend" -> {
                val stairs = context.world[currentPosition]?.firstOrNull { it.has(Ascendable::class) }
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