package com.helloworldramen.kingoyster.architecture

class Context(var world: World, var player: Entity = Entity.UNKNOWN, var level: Int = 1) {

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

    fun straightPathUntil(position: Position, direction: Direction, predicate: (Position) -> Boolean): List<Position> {
        return straightPathWhere(true, position, direction, predicate)
    }

    fun straightPathWhile(position: Position, direction: Direction, predicate: (Position) -> Boolean): List<Position> {
        return straightPathWhere(false, position, direction, predicate)
    }

    private fun straightPathWhere(isInclusive: Boolean, position: Position, direction: Direction,
                                  predicate: (Position) -> Boolean): List<Position> {
        val vector = direction.vector
        val maxVectorMagnitude = if (direction is Direction.East || direction is Direction.West) {
            world.width
        } else world.height

        val path = mutableListOf<Position>()

        for (magnitude in 0..maxVectorMagnitude) {
            val nextPosition = position + (vector * magnitude)

            if (isInclusive) {
                path.add(nextPosition)
                if (predicate(nextPosition)) break
            } else {
                if (!predicate(nextPosition)) break
                path.add(nextPosition)
            }
        }

        return path
    }

    companion object {
        const val MAX_WORLD_LEVEL = 10

        val UNKNOWN: Context = Context(World(0, 0))
    }
}