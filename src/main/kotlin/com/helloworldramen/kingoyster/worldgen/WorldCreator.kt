package com.helloworldramen.kingoyster.worldgen

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.worldgen.topology.DrunkTopologyStrategy
import com.helloworldramen.kingoyster.worldgen.topology.DungeonTopologyStrategy
import com.helloworldramen.kingoyster.worldgen.topology.EmptyTopologyStrategy
import com.helloworldramen.kingoyster.worldgen.topology.TopologyStrategy
import com.helloworldramen.kingoyster.worldgen.population.strategies.EmptyPopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.strategies.SlimesPopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.strategies.SmorgasbordPopulationStrategy

object WorldCreator {

    const val DEFAULT_WORLD_WIDTH = 17
    const val DEFAULT_WORLD_HEIGHT = 17

    data class WorldCreationKit(
        val topologyStrategy: TopologyStrategy,
        val populationStrategy: PopulationStrategy,
        val width: Int = DEFAULT_WORLD_WIDTH,
        val height: Int = DEFAULT_WORLD_HEIGHT
    )

    private val DEFAULT_KIT: WorldCreationKit = WorldCreationKit(
       EmptyTopologyStrategy, EmptyPopulationStrategy
    )

    private val KITS_FOR_LEVEL: Map<Int, WeightedCollection<WorldCreationKit>> = mapOf(
        1 to WeightedCollection(
            100 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.5)),
        ),
        2 to WeightedCollection(
            100 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.5)),
            20 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy(0.5)),
        ),
        3 to WeightedCollection(
            80 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.6)),
            100 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy(0.6)),
        ),
        4 to WeightedCollection(
            90 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.7)),
            100 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy(0.7)),
            30 to WorldCreationKit(DrunkTopologyStrategy(), SlimesPopulationStrategy())
        ),
        5 to WeightedCollection(
            100 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.8)),
            100 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy(0.8)),
        ),
        6 to WeightedCollection(
            100 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.8)),
            20 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy(0.8)),
            10 to WorldCreationKit(DrunkTopologyStrategy(), SlimesPopulationStrategy())
        ),
        7 to WeightedCollection(
            100 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.9)),
            20 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy(0.9)),
        ),
        8 to WeightedCollection(
            30 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy(0.9)),
            100 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy(0.9)),
            10 to WorldCreationKit(DrunkTopologyStrategy(), SlimesPopulationStrategy())
        ),
        9 to WeightedCollection(
            100 to WorldCreationKit(DungeonTopologyStrategy(), SmorgasbordPopulationStrategy()),
            20 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy()),
        ),
        10 to WeightedCollection(
            100 to WorldCreationKit(DrunkTopologyStrategy(), SmorgasbordPopulationStrategy()),
        )
    )

    fun create(level: Int, player: Entity, playerPosition: Position?): World {
        val (topologyStrategy, populationStrategy, width, height) = KITS_FOR_LEVEL[level]?.sample() ?: DEFAULT_KIT

        return topologyStrategy.generate(width, height, playerPosition).apply {
            addPlayer(player, playerPosition)
            populationStrategy.populate(this, player)
        }
    }

    private fun World.addPlayer(player: Entity, playerPosition: Position?) {
        if (playerPosition != null && get(playerPosition)?.isEmpty() == true) {
            add(player, playerPosition)
        } else {
            val randomEmptyPosition = Position(width, height)
                .map { it }
                .shuffled()
                .firstOrNull { get(it)?.isEmpty() == true }

            add(player, randomEmptyPosition)
        }
    }
}