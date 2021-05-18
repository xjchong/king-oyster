package com.helloworldramen.kingoyster.oyster

import com.helloworldramen.kingoyster.models.Position
import java.util.*

class World(val width: Int, val height: Int) {

    private val entitiesForPosition: MutableMap<Position, MutableList<Entity>> =
        mutableMapOf<Position, MutableList<Entity>>().apply {
            Position(width - 1, height - 1).forEach {
                this[it] = mutableListOf()
            }
        }
    private val positionForEntity: MutableMap<Entity, Position> = mutableMapOf()
    private val nextUpdateTimeForEntity: MutableMap<Entity, Int> = mutableMapOf()

    private val allEntities: MutableList<Entity> = mutableListOf()
    private val updateableEntities: PriorityQueue<Entity> = PriorityQueue { o1, o2 ->
        (o2?.nextUpdateTime ?: 0) - (o1?.nextUpdateTime ?: 0)
    }

    private val currentTime: Int
        get() = updateableEntities.peek()?.nextUpdateTime ?: 0

    operator fun get(position: Position): List<Entity>? = entitiesForPosition[position]

    operator fun get(x: Int, y: Int): List<Entity>? = entitiesForPosition[Position(x, y)]

    operator fun get(entity: Entity): Position? = positionForEntity[entity]

    fun add(entity: Entity, position: Position? = null): Boolean {
        if (allEntities.contains(entity)) return false

        entity.nextUpdateTime = currentTime
        allEntities.add(entity)

        if (entity.behaviors.isNotEmpty()) {
            nextUpdateTimeForEntity[entity] = entity.nextUpdateTime
            updateableEntities.add(entity)
        }

        if (position != null) {
            entitiesForPosition[position]?.let {
                it.add(entity)
                positionForEntity[entity] = position
            }
        }

        return true
    }

    fun add(entity: Entity, x: Int, y: Int): Boolean = add(entity, Position(x, y))

    fun move(entity: Entity, position: Position): Boolean {
        val currentPosition = positionForEntity[entity] ?: return false
        if (currentPosition == position) return true
        if (entitiesForPosition[currentPosition]?.remove(entity) != true) return false

        return add(entity, position)
    }

    fun remove(entity: Entity): Boolean {
        val currentPosition = positionForEntity[entity] ?: return false
        if (entitiesForPosition[currentPosition]?.remove(entity) != true) return false

        positionForEntity.remove(entity)?.let {
            entitiesForPosition[it]?.remove(entity)
        }
        updateableEntities.remove(entity)
        nextUpdateTimeForEntity.remove(entity)
        allEntities.remove(entity)

        return true
    }

    fun update(context: Context): Entity? {
        do {
            val entity = updateableEntities.poll() ?: return null

            if (entity.nextUpdateTime != nextUpdateTimeForEntity[entity]) {
                nextUpdateTimeForEntity[entity] = entity.nextUpdateTime
                updateableEntities.add(entity)
                continue
            }

            entity.executeBehaviors(context)
            updateableEntities.add(entity)
            nextUpdateTimeForEntity[entity] = entity.nextUpdateTime
        } while (updateableEntities.firstOrNull()?.requiresInput == false)

        return updateableEntities.firstOrNull()?.apply {
            executeBehaviors(context)
        }
    }

    private fun normalizeTime() {
        updateableEntities.firstOrNull()?.let { firstEntity ->
            val normalizer = firstEntity.nextUpdateTime
            updateableEntities.forEach { it.nextUpdateTime -= normalizer }
        }
    }

    companion object {
        /**
         * If an entity being processed has a time exceeding this threshold, all the
         * entities in the engine will have their update times normalized to the lowest
         * update time in the queue.
         */
        const val NORMALIZE_TIME_THRESHOLD = 1000000
    }
}