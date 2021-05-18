import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.entities.factories.ActorFactory
import com.helloworldramen.kingoyster.models.GameContext
import com.helloworldramen.kingoyster.utilities.WorldGenerator

fun main() {
    val world = WorldGenerator.generate(80, 24)
    val context = GameContext(world)
    val player = ActorFactory.player()

    world.add(player, 5, 5)

    WorldConsoleView.display(context.world)
}