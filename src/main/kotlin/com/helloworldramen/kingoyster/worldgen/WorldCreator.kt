package com.helloworldramen.kingoyster.worldgen

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.worldgen.metadata.FloorFlavor
import com.helloworldramen.kingoyster.worldgen.metadata.WorldFlavor
import com.helloworldramen.kingoyster.worldgen.topology.DrunkTopology
import com.helloworldramen.kingoyster.worldgen.topology.DungeonTopology
import com.helloworldramen.kingoyster.worldgen.topology.EmptyTopology
import com.helloworldramen.kingoyster.worldgen.topology.TopologyStrategy
import com.helloworldramen.kingoyster.worldgen.population.strategies.EmptyPopulation
import com.helloworldramen.kingoyster.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.worldgen.population.strategies.SlimesPopulation
import com.helloworldramen.kingoyster.worldgen.population.strategies.SmorgasbordPopulation

object WorldCreator {

    const val DEFAULT_WORLD_WIDTH = 17
    const val DEFAULT_WORLD_HEIGHT = 17

    data class WorldKit(
        val worldFlavor: WorldFlavor,
        val topologyStrategy: TopologyStrategy,
        val populationStrategy: PopulationStrategy,
        val width: Int = DEFAULT_WORLD_WIDTH,
        val height: Int = DEFAULT_WORLD_HEIGHT
    )

    private val DEFAULT_KIT: WorldKit = WorldKit(
       WorldFlavor.DRY_GRASS, EmptyTopology, EmptyPopulation
    )

    private val KITS_FOR_LEVEL: Map<Int, WeightedCollection<WorldKit>> = mapOf(
        1 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology(), SmorgasbordPopulation(0.5)),
        ),
        2 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology(), SmorgasbordPopulation(0.5)),
            20 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.5)),
        ),
        3 to WeightedCollection(
            80 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology(), SmorgasbordPopulation(0.6)),
            100 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.6)),
        ),
        4 to WeightedCollection(
            90 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology(), SmorgasbordPopulation(0.7)),
            100 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.7)),
            30 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SlimesPopulation()),

            90 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology(), SmorgasbordPopulation(0.8)),
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SmorgasbordPopulation(0.8)),
            30 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SlimesPopulation())
        ),
        5 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology(), SmorgasbordPopulation(0.7)),
            100 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.7)),

            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology(), SmorgasbordPopulation(0.8)),
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SmorgasbordPopulation(0.8)),
        ),
        6 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology(), SmorgasbordPopulation(0.7)),
            20 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.7)),
            10 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SlimesPopulation()),

            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology(), SmorgasbordPopulation(0.8)),
            20 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SmorgasbordPopulation(0.8)),
            10 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SlimesPopulation()),
        ),
        7 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology(), SmorgasbordPopulation(0.8)),
            20 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.8)),

            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology(), SmorgasbordPopulation(0.9)),
            20 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SmorgasbordPopulation(0.9)),
        ),
        8 to WeightedCollection(
            30 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology(), SmorgasbordPopulation(0.9)),
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SmorgasbordPopulation(0.9)),
            10 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SlimesPopulation())
        ),
        9 to WeightedCollection(
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology(), SmorgasbordPopulation()),
            20 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SmorgasbordPopulation()),
        ),
        10 to WeightedCollection(
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SmorgasbordPopulation()),
        )
    )

    fun create(level: Int, player: Entity, playerPosition: Position?): Pair<World, WorldFlavor> {
        val (worldFlavor, topologyStrategy, populationStrategy, width, height) = KITS_FOR_LEVEL[level]?.sample() ?: DEFAULT_KIT

        val (world, flavor) = topologyStrategy.terraform(worldFlavor, width, height, playerPosition)

        world.addPlayer(player, playerPosition)
        populationStrategy.populate(world, player)

        return Pair(world, flavor)
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