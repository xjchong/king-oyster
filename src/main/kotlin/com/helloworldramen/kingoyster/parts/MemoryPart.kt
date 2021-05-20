package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part
import com.helloworldramen.kingoyster.oyster.Position

class MemoryPart() : Part {

    var worldMemory: Map<Position, List<Entity>?> = mapOf()
        private set

    private constructor(worldMemory: Map<Position, List<Entity>?>): this() {
        this.worldMemory = worldMemory.keys.associateWith { position ->
            worldMemory[position]?.map { it.copy() }
        }
    }

    override fun copy(): Part {
        return MemoryPart(worldMemory)
    }

    fun remember(context: Context, position: Position) {
        worldMemory = worldMemory.toMutableMap().apply {
            val hm = context.world[position]?.filter {
                !it.has(MovementPart::class)
            }?.map {
                it.copy()
            }
            println(hm?.flatMap { it.parts }?.map { it::class })
            this[position] = hm
        }
    }
}