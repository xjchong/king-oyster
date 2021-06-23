package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.*

class MemoryPart() : Part {

    private var worldMemory: MutableMap<Position, List<Entity>?> = mutableMapOf()

    private constructor(worldMemory: Map<Position, List<Entity>?>): this() {
        this.worldMemory = worldMemory.keys.associateWith { position ->
            worldMemory[position]?.map { it.copy() }
        }.toMutableMap()
    }

    override fun copy(): Part {
        return MemoryPart(worldMemory)
    }

    operator fun get(position: Position): List<Entity>? {
        return worldMemory[position]
    }

    fun clear() {
        worldMemory.clear()
    }

    fun remember(context: Context, position: Position) {
        worldMemory[position] = context.world[position]?.filter {
            !it.has<MovementPart>()
        }?.map {
            it.copy()
        }
    }
}