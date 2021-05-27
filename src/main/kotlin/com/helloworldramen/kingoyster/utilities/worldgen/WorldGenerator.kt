package com.helloworldramen.kingoyster.utilities.worldgen

import com.helloworldramen.kingoyster.entities.ActorFactory
import com.helloworldramen.kingoyster.entities.FeatureFactory
import com.helloworldramen.kingoyster.entities.ItemFactory
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.World
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

object WorldGenerator {

    fun repopulate(world: World, strategy: WorldGenerationStrategy, existingPlayer: Entity? = null,
                   playerPosition: Position? = null): Entity {
        val player = existingPlayer ?: ActorFactory.player()

        world.clear()
        world.apply {
            Position(width - 1, height - 1).forEach {
                add(FeatureFactory.wall(), it)
            }
        }

        strategy.generate(world)
        placeFeatures(world)
        placePlayer(world, player, playerPosition)
        placeEnemies(world)
        placeItems(world)

        return player
    }

    private fun placePlayer(world: World, player: Entity, position: Position? = null) {
        if (position == null || world[position]?.isNotEmpty() != false) {
            world.randomEmptyPosition()?.let {
                world.add(player, it)
            }
        } else {
            world.add(player, position)
        }
    }

    private fun placeEnemies(world: World) {
        world.placeWithDensity(0.014) {
            ActorFactory.slime()
        }
        world.placeWithDensity(0.006) {
            ActorFactory.goblin()
        }
        world.placeWithRange(1, 1) {
            ActorFactory.ghost()
        }
    }

    private fun placeItems(world: World) {
        world.placeWithDensity(0.02) { ItemFactory.coin(1, 100) }
    }

    private fun placeFeatures(world: World) {
        world.randomEmptyPosition()?.let {
            world.add(FeatureFactory.stairs(), it)
        }
    }

    private fun World.placeWithDensity(percentage: Double, entityFn: () -> Entity) {
        repeat((width * height * percentage).roundToInt()) {
            randomEmptyPosition()?.let {
                add(entityFn(), it)
            }
        }
    }

    private fun World.placeWithRange(minCount: Int, maxCount: Int, entityFn: () -> Entity) {
        val count = Random.nextInt(minCount..maxCount)

        repeat(count) {
            randomEmptyPosition()?.let {
                add(entityFn(), it)
            }
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