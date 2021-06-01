package com.helloworldramen.kingoyster.architecture

import com.helloworldramen.kingoyster.parts.isCorporeal
import com.helloworldramen.kingoyster.parts.isPassable


class Context(val world: World) {
    var player = Entity.UNKNOWN
    var level = 1

    fun entitiesAt(position: Position): List<Entity>? {
        return world[position]
    }

    fun positionOf(entity: Entity?): Position? {
        return if (entity == null) {
            null
        } else {
            world[entity]
        }
    }

    fun nearestWhere(position: Position, direction: Direction, predicate: (List<Entity>?) -> Boolean): Position {
        val vector = direction.vector
        val maxVectorMagnitude = if (direction is Direction.East || direction is Direction.West) {
            world.width
        } else world.height

        for (magnitude in 1..maxVectorMagnitude) {
            val nextPosition = position + (vector * magnitude)

            if (predicate(entitiesAt(nextPosition))) return nextPosition
        }

        return position + vector
    }

    fun furthestWhere(position: Position, direction: Direction, predicate: (List<Entity>?) -> Boolean): Position {
        val vector = direction.vector
        val maxVectorMagnitude = if (direction is Direction.East || direction is Direction.West) {
            world.width
        } else world.height

        var farthestPosition = position

        for (magnitude in 1..maxVectorMagnitude) {
            val nextPosition = position + (vector * magnitude)

            if (!predicate(entitiesAt(nextPosition))) break

            farthestPosition = nextPosition
        }

        return farthestPosition
    }

    companion object {
        const val MAX_WORLD_LEVEL = 10

        val UNKNOWN: Context = Context(World(0, 0))
    }
}