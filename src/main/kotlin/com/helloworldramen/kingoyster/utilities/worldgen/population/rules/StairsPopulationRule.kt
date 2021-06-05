package com.helloworldramen.kingoyster.utilities.worldgen.population.rules

import com.helloworldramen.kingoyster.entities.FeatureFactory
import com.helloworldramen.kingoyster.extensions.randomPositionWhere
import com.helloworldramen.kingoyster.utilities.worldgen.population.PopulationRule

class StairsPopulationRule : PopulationRule(
    { FeatureFactory.stairs() },
    predicate = { world, position, player ->
        val playerPosition = world[player]

        when {
            world[position]?.isEmpty() != true -> false
            playerPosition == null -> true
            else -> {
                val otherEmptyPosition = world.randomPositionWhere { _, entities ->
                    entities.isEmpty()
                }

                val distanceFromPlayer = position.distanceFrom(playerPosition)
                val otherDistanceFromPlayer = otherEmptyPosition?.distanceFrom(playerPosition) ?: 0.0

                otherDistanceFromPlayer < distanceFromPlayer
            }
        }
    }
)
