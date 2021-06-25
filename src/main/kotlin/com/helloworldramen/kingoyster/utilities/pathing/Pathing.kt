package com.helloworldramen.kingoyster.utilities.pathing

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.extensions.asPosition
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.isKillable
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.utilities.AStar

object Pathing {

    fun pathTo(context: Context, entity: Entity, goal: Position): Position? {
        val currentPosition = context.positionOf(entity) ?: return null

        return AStar.getPath(
            currentPosition.asPair(),
            goal.asPair(),
            cost = { _, to: Pair<Int, Int> ->
                val entities = context.entitiesAt(Position(to))

                if (entities == null) {
                    999.0
                } else {
                    if (!entity.isCorporeal()) return@getPath 1.0

                    val impassableEntities = entities.filterNot { it.isPassable() }
                    if (impassableEntities.isEmpty()) return@getPath 1.0

                    val unkillableEntities = impassableEntities.filterNot { it.isKillable() && it.isEnemyOf(entity) }
                    if (unkillableEntities.isEmpty()) return@getPath 20.0

                    val immovableEntities = unkillableEntities.filterNot { it.has<MovementPart>() }
                    if (immovableEntities.isEmpty()) return@getPath 80.0

                    val permanentEntities = immovableEntities.filterNot { it.has<OpenablePart>() }
                    if (permanentEntities.isEmpty()) return@getPath 150.0

                    return@getPath 800.0
                }
            },
            heuristic = AStar.MANHATTAN_HEURISTIC,
            isDeterministic = false
        ).firstOrNull()?.asPosition()
    }

    fun pathAway(context: Context, entity: Entity, avoid: Position): Position? {
        val currentPosition = context.positionOf(entity) ?: return null
        val passableNeighbors = currentPosition.neighborsShuffled().filter { neighbor ->
            context.entitiesAt(neighbor).all { entity.canPass(it) }
        }

        return passableNeighbors.maxByOrNull { it.distanceFrom(avoid) }
    }
}