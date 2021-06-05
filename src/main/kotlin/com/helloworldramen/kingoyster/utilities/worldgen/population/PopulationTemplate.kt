package com.helloworldramen.kingoyster.utilities.worldgen.population

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

open class PopulationTemplate(
    protected open val minDensity: Double? = null,
    protected open val maxDensity: Double? = null,
    protected open val minCount: Int? = null,
    protected open val maxCount: Int? = null,
    protected open val rules: WeightedCollection<PopulationRule> = WeightedCollection()
) {
    constructor(density: Double, rules: WeightedCollection<PopulationRule>)
            : this(density, density, null, null, rules)

    constructor(minCount: Int, maxCount: Int, rules: WeightedCollection<PopulationRule>)
            : this(null, null, minCount, maxCount, rules)

    fun execute(world: World, player: Entity) {
        val area = world.width * world.height
        val minCountByDensity = (area * (minDensity?.coerceAtLeast(0.0) ?: 0.0)).roundToInt()
        val maxCountByDensity = (area * (maxDensity?.coerceAtLeast(0.0) ?: 1.0)).roundToInt().coerceAtLeast(minCountByDensity)
        val minCountDiscrete = minCount?.coerceAtLeast(0) ?: minCountByDensity
        val maxCountDiscrete = (maxCount?.coerceAtLeast(0) ?: maxCountByDensity).coerceAtLeast(minCountDiscrete)
        val count = Random.nextInt(minCountDiscrete..maxCountDiscrete)

        repeat(count) {
            rules.sample()?.let { rule ->
                world.randomPositionWhere { rule.predicate(world, it, player) }?.let { position ->
                    world.add(rule.entityFactory(), position)
                }
            }
        }
    }

    private fun World.randomPositionWhere(predicate: (Position) -> Boolean): Position? {
        val allPositions = Position(width, height).map { it }.shuffled()

        return allPositions.firstOrNull(predicate)
    }
}