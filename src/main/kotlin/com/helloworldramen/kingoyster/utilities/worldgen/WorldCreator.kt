package com.helloworldramen.kingoyster.utilities.worldgen

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import com.helloworldramen.kingoyster.utilities.WeightedEntry
import com.helloworldramen.kingoyster.utilities.worldgen.generation.DrunkGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.generation.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.generation.EmptyGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.generation.GenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.population.strategies.EmptyPopulationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.population.PopulationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.population.strategies.SmorgasbordPopulationStrategy

object WorldCreator {

    const val DEFAULT_WORLD_WIDTH = 17
    const val DEFAULT_WORLD_HEIGHT = 17

    data class WorldCreationKit(
        val generationStrategy: GenerationStrategy,
        val populationStrategy: PopulationStrategy,
        val width: Int = DEFAULT_WORLD_WIDTH,
        val height: Int = DEFAULT_WORLD_HEIGHT
    )

    private val DEFAULT_KIT: WorldCreationKit = WorldCreationKit(
       EmptyGenerationStrategy, EmptyPopulationStrategy
    )

    private val KITS_FOR_LEVEL: Map<Int, WeightedCollection<WorldCreationKit>> = mapOf(
        1 to WeightedCollection(
            WeightedEntry(100, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        2 to WeightedCollection(
            WeightedEntry(100, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(20, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        3 to WeightedCollection(
            WeightedEntry(80, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(100, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        4 to WeightedCollection(
            WeightedEntry(90, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(100, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        5 to WeightedCollection(
            WeightedEntry(100, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(100, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        6 to WeightedCollection(
            WeightedEntry(100, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(20, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        7 to WeightedCollection(
            WeightedEntry(100, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(20, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        8 to WeightedCollection(
            WeightedEntry(30, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(100, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        9 to WeightedCollection(
            WeightedEntry(100, WorldCreationKit(DungeonGenerationStrategy(), SmorgasbordPopulationStrategy())),
            WeightedEntry(20, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        ),
        10 to WeightedCollection(
            WeightedEntry(100, WorldCreationKit(DrunkGenerationStrategy(), SmorgasbordPopulationStrategy())),
        )
    )

    fun create(level: Int, player: Entity, playerPosition: Position?): World {
        val (generationStrategy, populationStrategy, width, height) = KITS_FOR_LEVEL[level]?.sample() ?: DEFAULT_KIT

        return generationStrategy.generate(width, height, playerPosition).apply {
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