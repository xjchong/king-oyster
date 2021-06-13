package com.helloworldramen.kingoyster.worldgen

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.parts.AscendablePart
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.isPassable
import com.helloworldramen.kingoyster.utilities.WeightedCollection
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

    private const val MAX_CREATION_ATTEMPTS = 500
    private const val MIN_FLOOR_PERCENT = 0.3

    data class WorldKit(
        val worldFlavor: WorldFlavor,
        val topologyStrategy: TopologyStrategy,
        val populationStrategy: PopulationStrategy,
        val width: Int = DEFAULT_WORLD_WIDTH,
        val height: Int = DEFAULT_WORLD_HEIGHT
    )

    private val DEFAULT_KIT: WorldKit = WorldKit(
       WorldFlavor.DEFAULT, EmptyTopology, EmptyPopulation
    )

    private val KITS_FOR_LEVEL: Map<Int, WeightedCollection<WorldKit>> = mapOf(
        1 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.CRAMPED, SmorgasbordPopulation(0.5)),
        ),
        2 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.STANDARD, SmorgasbordPopulation(0.5)),
            70 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.CRAMPED, SmorgasbordPopulation(0.5)),
            20 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.OPEN, SmorgasbordPopulation(0.5)),
            20 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.5)),
        ),
        3 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.6)),
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.STANDARD, SmorgasbordPopulation(0.6)),
            80 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.CRAMPED, SmorgasbordPopulation(0.5)),
            60 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.OPEN, SmorgasbordPopulation(0.6)),
        ),
        4 to WeightedCollection(
            90 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.STANDARD, SmorgasbordPopulation(0.7)),
            90 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.7)),
            80 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.OPEN, SmorgasbordPopulation(0.7)),
            30 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.CRAMPED, SmorgasbordPopulation(0.7)),
            20 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.MANY_PILLARS, SmorgasbordPopulation(0.7)),
            10 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SlimesPopulation()),
        ),
        5 to WeightedCollection(
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.STANDARD, SmorgasbordPopulation(0.7)),
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.MANY_PILLARS, SmorgasbordPopulation(0.7)),
            100 to WorldKit(WorldFlavor.DRY_GRASS, DungeonTopology.OPEN, SmorgasbordPopulation(0.7)),
            80 to WorldKit(WorldFlavor.DRY_GRASS, DrunkTopology(), SmorgasbordPopulation(0.7)),

            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.STANDARD, SmorgasbordPopulation(0.8)),
            90 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.BIG_ROOMS, SmorgasbordPopulation(0.8)),
            60 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MANY_ROOMS, SmorgasbordPopulation(0.8)),
            30 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MAZE, SmorgasbordPopulation(0.8)),
            10 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SlimesPopulation())
        ),
        6 to WeightedCollection(
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MANY_ROOMS, SmorgasbordPopulation(0.8)),
            90 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.BIG_ROOMS, SmorgasbordPopulation(0.8)),
            40 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MAZE, SmorgasbordPopulation(0.8)),
            10 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SlimesPopulation()),
        ),
        7 to WeightedCollection(
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MANY_ROOMS, SmorgasbordPopulation(0.9)),
            90 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.BIG_ROOMS, SmorgasbordPopulation(0.9)),
            50 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MAZE, SmorgasbordPopulation(0.9)),
            40 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.OPEN, SmorgasbordPopulation(0.9)),
            10 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SlimesPopulation()),
        ),
        8 to WeightedCollection(
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MANY_ROOMS, SmorgasbordPopulation(0.9)),
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.BIG_ROOMS, SmorgasbordPopulation(0.9)),
            60 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.OPEN, SmorgasbordPopulation(0.9)),
            50 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MAZE, SmorgasbordPopulation(0.9)),
            10 to WorldKit(WorldFlavor.LUSH_GRASS, DrunkTopology(), SlimesPopulation()),
        ),
        9 to WeightedCollection(
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.BIG_ROOMS, SmorgasbordPopulation()),
            90 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.OPEN, SmorgasbordPopulation()),
            20 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.MANY_PILLARS, SmorgasbordPopulation()),
        ),
        10 to WeightedCollection(
            100 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.ONLY_PILLARS, SmorgasbordPopulation(1.2)),
            30 to WorldKit(WorldFlavor.LUSH_GRASS, DungeonTopology.COURTYARD, SmorgasbordPopulation(1.5)),
        )
    )

    fun create(level: Int, player: Entity, playerPosition: Position?): Pair<World, WorldFlavor> {
        repeat(MAX_CREATION_ATTEMPTS) {
            val (worldFlavor, topologyStrategy, populationStrategy, width, height) = KITS_FOR_LEVEL[level]?.sample() ?: DEFAULT_KIT
            val (world, flavor) = topologyStrategy.terraform(worldFlavor, width, height, playerPosition)

            world.addPlayer(player, playerPosition)
            populationStrategy.populate(world, player)

            if (world.isValid(player)) return Pair(world, flavor)
        }

        return Pair(World(DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT), WorldFlavor.DRY_GRASS)
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

    private fun World.isValid(player: Entity): Boolean {
        // The player must exist in the world.
        if (get(player) == null) return false

        // An exit (stairs) must exist in the world.
        if (entities.none { it.has<AscendablePart>() }) return false

        val floorCount = Position(width - 1, height - 1).map { position ->
            if (get(position)?.all { it.isPassable() || it.has<CombatPart>() } == true) 1 else 0
        }.sum()

        // There should be at least some ground.
        if (floorCount < (width * height) * MIN_FLOOR_PERCENT) return false

        return true
    }
}