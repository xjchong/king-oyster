package com.helloworldramen.kingoyster.utilities.worldgen.generation

import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.entities.FeatureFactory

class DrunkGenerationStrategy(private val clearPercentage: Double = DEFAULT_CLEAR_PERCENTAGE) : GenerationStrategy() {

    override fun generate(width: Int, height: Int, playerPosition: Position?): World {
        return World(width, height).apply {
            fill { FeatureFactory.wall() }
            drunkWalk(this, clearPercentage, Position(width / 2, height / 2))
        }
    }

    private fun drunkWalk(world: World, clearPercentage: Double, startingPosition: Position) {
        var currentPosition = startingPosition
        var clearCount = 1
        var currentWalkLength = 0
        val maxWalkLength = world.width * world.height
        val worldArea = world.width * world.height
        val vectors = listOf(
            Position(0, 1), Position(0, -1),
            Position(1, 0), Position(-1, 0)
        )

        world.removeAll(currentPosition)

        while (clearCount / worldArea.toDouble() < clearPercentage) {
            val nextPosition = currentPosition.withRelative(vectors.random())
            if (nextPosition.isOutOfBounds(world)) continue

            currentPosition = nextPosition
            currentWalkLength++

            if (world[currentPosition]?.isNullOrEmpty() == true) {
                continue
            } else {
                world.removeAll(currentPosition)
                clearCount++
            }

            if (currentWalkLength >= maxWalkLength) {
                currentPosition = startingPosition
                currentWalkLength = 0
            }
        }
    }

    companion object {
        private const val DEFAULT_CLEAR_PERCENTAGE = 0.45
    }
}