import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.actions.Ascend
import com.helloworldramen.kingoyster.entities.actions.Move
import com.helloworldramen.kingoyster.entities.facets.Ascendable
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.GameOver
import com.helloworldramen.kingoyster.game.GameContext
import com.helloworldramen.kingoyster.game.GameWorld
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.utilities.WorldGenerator
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
        val world = GameWorld(80, 24)
        WorldGenerator.repopulate(world)
        val context = GameContext(world)

        while (true) {
            WorldConsoleView.display(context.world)
            val player = world.update(context) ?: break

            while (true) {
                if (parseInput(context, player)) break
            }
        }
    }

    private fun parseInput(context: GameContext, inputEntity: GameEntity): Boolean {
        val currentPosition = context.world[inputEntity] ?: return false

        return when (readLine()) {
            "north" -> inputEntity.respondToAction(Move(context, currentPosition.north()))
            "east" -> inputEntity.respondToAction(Move(context, currentPosition.east()))
            "south" -> inputEntity.respondToAction(Move(context, currentPosition.south()))
            "west" -> inputEntity.respondToAction(Move(context, currentPosition.west()))
            "ascend" -> {
                val ascendable = context.world[currentPosition]?.firstOrNull { it.hasFacet(Ascendable::class) }
                ascendable?.respondToAction(Ascend(context, inputEntity)) == true
            }
            else -> false
        }
    }

}

fun main() {
    val gameEngine = ConsoleGameEngine()

    gameEngine.run()
}