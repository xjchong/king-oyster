package com.helloworldramen.kingoyster.architecture

import com.helloworldramen.kingoyster.parts.*

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

    fun findDropPosition(position: Position): Position {
        if (!position.isOccupied()) return position

        val adjacentNeighbors = position.neighborsShuffled()
        val unoccupiedAdjacentNeighbor = adjacentNeighbors.firstOrNull {
            !it.isOccupied()
        }

        if (unoccupiedAdjacentNeighbor != null) return unoccupiedAdjacentNeighbor

        val diagonalNeighbors = position.diagonalNeighbors().shuffled()
        val unoccupiedDiagonalNeighbor = diagonalNeighbors.firstOrNull {
            !it.isOccupied()
        }

        if (unoccupiedDiagonalNeighbor != null) return unoccupiedDiagonalNeighbor

        return (listOf(position) + adjacentNeighbors + diagonalNeighbors).random()
    }

    private fun Position.isOccupied(): Boolean {
        return entitiesAt(this)?.any { entity ->
            entity.has<WeaponPart>() || entity.has<ItemPart>() || entity.has<AscendablePart>()
                    || (!entity.isPassable() && !entity.has<MovementPart>())
        } != false
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