package com.helloworldramen.kingoyster.architecture

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DamagePositionEvent
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.MovementPart

class Context(var world: World, var player: Entity = Entity.UNKNOWN, var level: Int = 1) {

    fun entitiesAt(position: Position): List<Entity> {
        return world[position] ?: listOf()
    }

    fun positionOf(entity: Entity?): Position? {
        return if (entity == null) {
            null
        } else {
            world[entity]
        }
    }

    /**
     * Applies the action to all entities at the position.
     */
    fun applyAction(position: Position, action: Action): Boolean {
        val entities = entitiesAt(position)
        var didRespond = false

        entities.forEach {
            didRespond = if (it.respondToAction(action)) true else didRespond
        }

        when (action) {
            is Damage -> EventBus.post(DamagePositionEvent(
                source = action.actor,
                position = position,
                amount = action.amount,
                damageType = action.damageType,
                elementType = action.elementType
            ))
        }

        return didRespond
    }

    /**
     * Applies the action to all entities at each position.
     */
    fun applyAction(positions: List<Position>, action: Action): Boolean {
        var didRespond = false

        positions.forEach { position ->
            didRespond = if (applyAction(position, action)) true else didRespond
        }

        return didRespond
    }

    /**
     * Attempts each action on the entities at the position, one entity at a time.
     * Stops trying actions at the first successful response.
     */
    fun tryActions(position: Position, vararg actions: Action): Entity? {
        val entities = entitiesAt(position)

        for (entity in entities) {
            if (actions.any { it.actor != entity && entity.respondToAction(it) }) {
                return entity
            }
        }

        return null
    }

    fun findDropPosition(position: Position): Position {
        if (!position.isOccupied()) return position

        val adjacentNeighbors = position.neighborsShuffled().filter { !it.isOccupied() }
        val unoccupiedAdjacentNeighbor = adjacentNeighbors.firstOrNull()

        if (unoccupiedAdjacentNeighbor != null) return unoccupiedAdjacentNeighbor

        val diagonalNeighbors = position.diagonalNeighbors().shuffled().filter { !it.isOccupied() }
        val unoccupiedDiagonalNeighbor = diagonalNeighbors.firstOrNull()

        if (unoccupiedDiagonalNeighbor != null) return unoccupiedDiagonalNeighbor

        return (listOf(position) + adjacentNeighbors + diagonalNeighbors).random()
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

    private fun Position.isOccupied(): Boolean {
        return entitiesAt(this).any { entity ->
            entity.has<WeaponPart>() || entity.has<ItemPart>() || entity.has<AscendablePart>()
                    || (!entity.isPassable() && !entity.has<MovementPart>())
        }
    }

    companion object {
        const val MAX_WORLD_LEVEL = 10

        val UNKNOWN: Context = Context(World(0, 0))
    }
}