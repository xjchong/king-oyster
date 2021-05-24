package com.helloworldramen.kingoyster.oyster

import java.util.*

class World(val width: Int, val height: Int) {

    val currentTime: Double
        get() = updateableEntities.peek()?.nextUpdateTime ?: 0.0

    private val entitiesForPosition: MutableMap<Position, MutableList<Entity>> =
        mutableMapOf<Position, MutableList<Entity>>().apply {
            Position(width - 1, height - 1).forEach {
                this[it] = mutableListOf()
            }
        }
    private val positionForEntity: MutableMap<Entity, Position> = mutableMapOf()
    private val nextUpdateTimeForEntity: MutableMap<Entity, Double> = mutableMapOf()
    private val updateNumberForEntity: MutableMap<Entity, Long> = mutableMapOf()
    private val allEntities: MutableList<Entity> = mutableListOf()

    private val updateComparator = compareBy<Entity> {
        it.nextUpdateTime
    }.thenBy {
        updateNumberForEntity[it] ?: 0L
    }
    private val updateableEntities: PriorityQueue<Entity> = PriorityQueue(updateComparator)

    private var nextUpdateNumber: Long = 0 // Use this to keep track of the number of updates. It should be normalized periodically.

    operator fun get(position: Position): List<Entity>? = entitiesForPosition[position]

    operator fun get(x: Int, y: Int): List<Entity>? = entitiesForPosition[Position(x, y)]

    operator fun get(entity: Entity): Position? = positionForEntity[entity]

    fun add(entity: Entity, position: Position? = null): Boolean {
        if (allEntities.contains(entity)) return false

        allEntities.add(entity)

        if (entity.timeFactor > 0.0) {
            entity.nextUpdateTime = currentTime
            updateableEntities.add(entity)
            nextUpdateTimeForEntity[entity] = entity.nextUpdateTime
            updateNumberForEntity[entity] = nextUpdateNumber++
        }

        if (position != null) {
            entitiesForPosition[position]?.let {
                it.add(entity)
                positionForEntity[entity] = position
            }
        }

        return true
    }

    fun move(entity: Entity, position: Position): Boolean {
        val currentPosition = positionForEntity[entity] ?: return false
        if (currentPosition == position) return true
        if (entitiesForPosition[currentPosition]?.remove(entity) != true) return false

        return if (entitiesForPosition[position]?.add(entity) == true) {
            positionForEntity[entity] = position
            true
        } else {
            entitiesForPosition[currentPosition]?.add(entity)
            false
        }
    }

    fun remove(entity: Entity): Boolean {
        val currentPosition = positionForEntity[entity] ?: return false
        if (entitiesForPosition[currentPosition]?.remove(entity) != true) return false

        positionForEntity.remove(entity)?.let {
            entitiesForPosition[it]?.remove(entity)
        }
        updateableEntities.remove(entity)
        nextUpdateTimeForEntity.remove(entity)
        updateNumberForEntity.remove(entity)
        allEntities.remove(entity)

        return true
    }

    fun removeAll(position: Position): List<Entity>? {
        val entities = entitiesForPosition[position]?.toList() ?: return null
        val removedEntities = mutableListOf<Entity>()

        for (entity in entities) {
            if (remove(entity)) removedEntities.add(entity)
        }

        return removedEntities
    }

    fun clear() {
        entitiesForPosition.values.forEach { it.clear() }
        positionForEntity.clear()
        updateableEntities.clear()
        nextUpdateTimeForEntity.clear()
        updateNumberForEntity.clear()
        allEntities.clear()
        nextUpdateNumber = 0
    }

    fun update(context: Context): Entity? {
        do {
            val entity = updateableEntities.poll() ?: return null

            if (entity.nextUpdateTime != nextUpdateTimeForEntity[entity]) {
                nextUpdateTimeForEntity[entity] = entity.nextUpdateTime
                updateableEntities.add(entity)
                continue
            }

            if (entity.nextUpdateTime > NORMALIZE_TIME_THRESHOLD) {
                normalizeTime()
            }

            entity.update(context)

            // If after updating the entity, its time didn't change, then assume it is waiting.
            if (entity.nextUpdateTime == nextUpdateTimeForEntity[entity]) {
                entity.nextUpdateTime += Entity.BASE_TIME_STEP * entity.timeFactor
            }

            updateableEntities.add(entity)
            nextUpdateTimeForEntity[entity] = entity.nextUpdateTime
        } while (updateableEntities.firstOrNull()?.requiresInput == false)

        return updateableEntities.firstOrNull()?.apply {
            update(context)
        }
    }

    fun respondToActions(position: Position, vararg actions: Action): Entity? {
        val entities = get(position) ?: return null

        for (action in actions) {
            return entities.lastOrNull { it.respondToAction(action) } ?: continue
        }

        return null
    }

    private fun normalizeTime() {
        updateableEntities.firstOrNull()?.let { firstEntity ->
            val normalizer = firstEntity.nextUpdateTime

            updateableEntities.forEach {
                it.nextUpdateTime -= normalizer
                nextUpdateTimeForEntity[it] = it.nextUpdateTime
            }
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