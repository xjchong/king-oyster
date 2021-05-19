package com.helloworldramen.kingoyster.utilities.worldgen

import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.World
import com.helloworldramen.kingoyster.entities.actors.Player
import com.helloworldramen.kingoyster.entities.features.Stairs
import com.helloworldramen.kingoyster.entities.features.Wall
import com.helloworldramen.kingoyster.entities.items.Coin

object WorldGenerator {

    fun repopulate(world: World, strategy: WorldGenerationStrategy, existingPlayer: Entity? = null, playerPosition: Position? = null) {
        val player = existingPlayer ?: Player()

        world.clear()
        world.apply {
            Position(width - 1, height - 1).forEach {
                add(Wall(), it)
            }
        }

        strategy.generate(world)
        placeFeatures(world)
        placePlayer(world, player, playerPosition)
        placeItems(world)
    }

    private fun placePlayer(world: World, player: Entity, position: Position? = null) {
        if (position == null || world[position]?.isNotEmpty() != false) {
            world.randomEmptyPosition()?.let {
                world.add(Player(), it)
            }
        } else {
            world.add(player, position)
        }
    }

    private fun placeItems(world: World) {
        repeat(10) {
            world.randomEmptyPosition()?.let {
                world.add(Coin.new(1, 100), it)
            }
        }
    }

    private fun placeFeatures(world: World) {
        world.randomEmptyPosition()?.let {
            world.add(Stairs(), it)
        }
    }

    private fun World.randomEmptyPosition(): Position? {
        return randomPositionWhere { get(it)?.isEmpty() == true }
    }

    private fun World.randomPositionWhere(predicate: (Position) -> Boolean): Position? {
        val allPositions = Position(width, height).map { it }.shuffled()

        return allPositions.firstOrNull(predicate)
    }
}