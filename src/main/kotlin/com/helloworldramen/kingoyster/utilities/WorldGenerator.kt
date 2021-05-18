package com.helloworldramen.kingoyster.utilities

import com.helloworldramen.kingoyster.consoleviews.WorldConsoleView
import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.factories.ActorFactory
import com.helloworldramen.kingoyster.entities.factories.FeatureFactory
import com.helloworldramen.kingoyster.game.GameWorld
import com.helloworldramen.kingoyster.models.Position

object WorldGenerator {

    fun repopulate(world: GameWorld, existingPlayer: GameEntity? = null, playerPosition: Position? = null) {
        val player = existingPlayer ?: ActorFactory.player()

        world.clear()
        world.apply {
            Position(width - 1, height - 1).forEach {
                add(FeatureFactory.wall(), it)
            }
        }

        drunkWalk(world, 0.45, playerPosition ?: Position(world.width / 2, world.height / 2))
        placeFeatures(world)
        placePlayer(world, player, playerPosition)
        placeNonPlayerActors(world)
    }

    private fun placePlayer(world: GameWorld, player: GameEntity, position: Position? = null) {
        if (position == null) {
            world.randomEmptyPosition()?.let {
                world.add(ActorFactory.player(), it)
            }
        } else {
            world.add(player, position)
        }
    }

    private fun placeNonPlayerActors(world: GameWorld) {
    }

    private fun placeFeatures(world: GameWorld) {
        world.randomEmptyPosition()?.let {
            world.add(FeatureFactory.stairsUp(), it)
        }
    }

    private fun drunkWalk(world: GameWorld, clearPercentage: Double, startingPosition: Position) {
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

    private fun GameWorld.randomEmptyPosition(): Position? {
        return randomPositionWhere { get(it)?.isEmpty() == true }
    }

    private fun GameWorld.randomPositionWhere(predicate: (Position) -> Boolean): Position? {
        val allPositions = Position(width, height).map { it }.shuffled()

        return allPositions.firstOrNull(predicate)
    }

    private fun Position.isOutOfBounds(world: GameWorld): Boolean {
        return x <= 0 || y <= 0 || x >= world.width - 1 || y >= world.height - 1
    }

}