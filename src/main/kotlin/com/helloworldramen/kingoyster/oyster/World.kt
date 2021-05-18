package com.helloworldramen.kingoyster.oyster

import com.helloworldramen.kingoyster.models.Position
import java.util.*

class World<C : Context>(val width: Int, val height: Int) {

    private val entitiesForPosition: MutableMap<Position, MutableList<Entity<C>>> =
        mutableMapOf<Position, MutableList<Entity<C>>>().apply {
            Position(width - 1, height - 1).forEach {
                this[it] = mutableListOf()
            }
        }
    private val positionForEntity: MutableMap<Entity<C>, Position> = mutableMapOf()
    private val nextUpdateTimeForEntity: MutableMap<Entity<C>, Int> = mutableMapOf()

    private val allEntities: MutableList<Entity<C>> = mutableListOf()
    private val updateableEntities: PriorityQueue<Entity<C>> = PriorityQueue { o1, o2 ->
        (o2?.nextUpdateTime ?: 0) - (o1?.nextUpdateTime ?: 0)
    }

    private val currentTime: Int
        get() = updateableEntities.peek()?.nextUpdateTime ?: 0

    operator fun get(position: Position): List<Entity<C>>? = entitiesForPosition[position]

    operator fun get(x: Int, y: Int): List<Entity<C>>? = entitiesForPosition[Position(x, y)]

    operator fun get(entity: Entity<C>): Position? = positionForEntity[entity]

    fun add(entity: Entity<C>, position: Position? = null): Boolean {
        if (allEntities.contains(entity)) return false

        entity.nextUpdateTime = currentTime
        allEntities.add(entity)

        if (entity.behaviors.isNotEmpty() || entity.requiresInput) {
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

    fun add(entity: Entity<C>, x: Int, y: Int): Boolean = add(entity, Position(x, y))

    fun move(entity: Entity<C>, position: Position): Boolean {
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

    fun remove(entity: Entity<C>): Boolean {
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

    fun update(context: C): Entity<C>? {
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
            normalizeTime()
        } while (updateableEntities.firstOrNull()?.requiresInput == false)

        return updateableEntities.firstOrNull()?.apply {
            executeBehaviors(context)
        }
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