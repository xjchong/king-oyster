package com.helloworldramen.kingoyster.architecture

class World(val width: Int, val height: Int) {

    val currentTime: Double
        get() = next()?.time ?: 0.0

    private val entitiesForPosition: MutableMap<Position, MutableList<Entity>> =
        mutableMapOf<Position, MutableList<Entity>>().apply {
            Position(width - 1, height - 1).forEach {
                this[it] = mutableListOf()
            }
        }
    private val positionForEntity: MutableMap<Entity, Position> = mutableMapOf()
    private val allEntities: MutableList<Entity> = mutableListOf()
    private val updateableEntities: MutableList<Entity> = mutableListOf()

    operator fun get(position: Position): List<Entity>? = entitiesForPosition[position]
    operator fun get(x: Int, y: Int): List<Entity>? = entitiesForPosition[Position(x, y)]
    operator fun get(entity: Entity): Position? = positionForEntity[entity]

    fun add(entity: Entity, position: Position? = null): Boolean {
        if (allEntities.contains(entity)) return false

        allEntities.add(entity)

        if (entity.timeFactor > 0.0) {
            entity.time = currentTime
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
        allEntities.clear()
    }

    fun next(): Entity? {
        val nextEntity = updateableEntities.minByOrNull {
            it.time
        } ?: return null

        if (nextEntity.time > NORMALIZE_TIME_THRESHOLD) {
            normalizeTime()
        }

        return nextEntity
    }

    fun respondToActions(position: Position, vararg actions: Action): Entity? {
        val entities = get(position) ?: return null

        for (action in actions) {
            return entities.lastOrNull { it.respondToAction(action) } ?: continue
        }

        return null
    }

    private fun normalizeTime() {
        val normalizer = next()?.time ?: return

        updateableEntities.forEach {
            it.time -= normalizer
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