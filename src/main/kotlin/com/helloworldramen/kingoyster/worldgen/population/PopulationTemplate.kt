package com.helloworldramen.kingoyster.worldgen.population

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.utilities.WeightedEntry
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

open class PopulationTemplate private constructor(
    open val minDensity: Double? = null,
    open val maxDensity: Double? = null,
    open val minCount: Int? = null,
    open val maxCount: Int? = null,
    open val rules: WeightedCollection<PopulationRule> = WeightedCollection()
) {

    constructor(vararg weightsToRules: Pair<Int, PopulationRule>) : this(
        rules = WeightedCollection(weightsToRules.map {
            WeightedEntry(it)
        })
    )

    constructor(rule: PopulationRule): this(
        rules = WeightedCollection(1 to rule)
    )

    fun execute(world: World, player: Entity) {
        val area = world.width * world.height
        val minCountByDensity = (area * (minDensity?.coerceAtLeast(0.0) ?: 0.0)).roundToInt()
        val maxCountByDensity =
            (area * (maxDensity?.coerceAtLeast(0.0) ?: 0.0)).roundToInt().coerceAtLeast(minCountByDensity)
        val minCountDiscrete = minCount?.coerceAtLeast(0) ?: minCountByDensity
        val maxCountDiscrete = (maxCount?.coerceAtLeast(0) ?: maxCountByDensity).coerceAtLeast(minCountDiscrete)
        val count = Random.nextInt(minCountDiscrete..maxCountDiscrete)

        repeat(count) {
            rules.sample()?.let { rule ->
                world.randomPositionWhere { rule.predicate(world, it, player) }?.let { position ->
                    rule.entityFactoryFn()?.let { entity ->
                        world.add(entity, position)
                    }
                }
            }
        }
    }

    fun withDensity(density: Double): PopulationTemplate {
        return PopulationTemplate(density, density, minCount, maxCount, rules)
    }

    fun withDensity(minDensity: Double, maxDensity: Double): PopulationTemplate {
        return PopulationTemplate(minDensity, maxDensity, minCount, maxCount, rules)
    }

    fun withCount(count: Int): PopulationTemplate {
        return PopulationTemplate(minDensity, maxDensity, count, count, rules)
    }

    fun withCount(count: Double): PopulationTemplate {
        return withCount(count.roundToInt())
    }

    fun withCount(minCount: Int, maxCount: Int): PopulationTemplate {
        return PopulationTemplate(minDensity, maxDensity, minCount, maxCount, rules)
    }

    fun withCount(minCount: Double, maxCount: Double): PopulationTemplate {
        return withCount(minCount.roundToInt(), maxCount.roundToInt())
    }

    private fun World.randomPositionWhere(predicate: (Position) -> Boolean): Position? {
        val allPositions = Position(width, height).map { it }.shuffled()

        return allPositions.firstOrNull(predicate)
    }
}