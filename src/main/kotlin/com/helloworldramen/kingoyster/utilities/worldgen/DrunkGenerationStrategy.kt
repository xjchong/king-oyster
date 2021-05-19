package com.helloworldramen.kingoyster.utilities.worldgen

import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.World

object DrunkGenerationStrategy : WorldGenerationStrategy {

    override fun generate(world: World) {
        drunkWalk(world, 0.45, Position(world.width / 2, world.height / 2))
    }

    private fun drunkWalk(world: World, clearPercentage: Double, startingPosition: Position) {
        var currentPosition = startingPosition
        var clearCount = 1
        var currentWalkLength = 0
        val maxWalkLength = world.width * world.height
        val worldArea = world.width * world.height
        val vectors = listOf(
            Position(0, 1), Position(0, -1),
            Position(1, 0), Position(1, 0), Position(1, 0),
            Position(1, 0), Position(1, 0), Position(1, 0),
            Position(-1, 0), Position(-1, 0), Position(-1, 0),
            Position(-1, 0), Position(-1, 0), Position(-1, 0)
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

    private fun Position.isOutOfBounds(world: World): Boolean {
        return x <= 0 || y <= 0 || x >= world.width - 1 || y >= world.height - 1
    }
}