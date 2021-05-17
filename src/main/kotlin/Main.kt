import com.helloworldramen.kingoyster.attributes.AppearanceInfo
import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.entities.FeatureFactory
import com.helloworldramen.kingoyster.models.GameContext
import com.helloworldramen.kingoyster.utilities.WorldGenerator

fun main() {
    val world = WorldGenerator.generate(80, 24)
    val context = GameContext(world)
    val wall = FeatureFactory.wall()

    WorldConsoleView.display(context.world)
}