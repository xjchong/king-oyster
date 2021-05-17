package com.helloworldramen.kingoyster.oyster

import java.util.*

abstract class Engine {

    private val entities: PriorityQueue<Entity> = PriorityQueue { o1, o2 ->
        (o2?.nextUpdateTime ?: 0) - (o1?.nextUpdateTime ?: 0)
    }
    private val nextUpdateTimeForEntity: MutableMap<Entity, Int> = mutableMapOf()
    private val currentTime: Int
        get() = entities.peek()?.nextUpdateTime ?: 0

    fun addEntity(entity: Entity) {
        if (entity.behaviors.isNotEmpty()) {
            entities.add(entity.apply { nextUpdateTime = currentTime })
            nextUpdateTimeForEntity[entity] = entity.nextUpdateTime
        }
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity)
        nextUpdateTimeForEntity.remove(entity)
    }

    fun start(context: Context) {
        while (true) {
            entities.peek()?.let {
                if (it.nextUpdateTime != nextUpdateTimeForEntity[it]) {
                    nextUpdateTimeForEntity[it] = it.nextUpdateTime
                }
            }
            entities.poll()?.let {
                it.executeBehaviors(context)
                entities.add(it)
            }

            if (entities.peek()?.nextUpdateTime ?: 0 > NORMALIZE_TIME_THRESHOLD) {
                normalizeEntityTimes()
            }
        }
    }

    private fun normalizeEntityTimes() {
        entities.firstOrNull()?.nextUpdateTime?.let { normalizer ->
            entities.forEach { it.nextUpdateTime -= normalizer }
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