import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.actions.Move
import com.helloworldramen.kingoyster.entities.factories.ActorFactory
import com.helloworldramen.kingoyster.game.GameContext
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.utilities.WorldGenerator


fun parseInput(context: GameContext, inputEntity: GameEntity): Action? {
    val currentPosition = context.world[inputEntity] ?: return null
    println(inputEntity.nextUpdateTime)

    return when (readLine()) {
        "north" -> Move(inputEntity, currentPosition.north())
        "east" -> Move(inputEntity, currentPosition.east())
        "south" -> Move(inputEntity, currentPosition.south())
        "west" -> Move(inputEntity, currentPosition.west())
        else -> null
    }
}

fun main() {
    val world = WorldGenerator.generate(80, 24)
    val context = GameContext(world)

    world.add(ActorFactory.player(), 5, 5)

    while (true) {
        WorldConsoleView.display(context.world)
        val player = world.update(context) ?: break
        var action = parseInput(context, player)

        while (action == null) {
            action = parseInput(context, player)
        }

        player.respondToAction(context, action)
    }
}