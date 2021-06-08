package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.utilities.ShadowCasting

class SensoryPart(
    var visionRange: Int
) : Part {
    var visiblePositions: List<Position> = listOf()
        private set
    var isOmniscient: Boolean = false // FOR DEBUG ONLY! :)

    private constructor(visionRange: Int, visiblePositions: List<Position>): this(visionRange) {
        this.visiblePositions = visiblePositions.toList()
    }

    override fun copy(): Part {
        return SensoryPart(visionRange, visiblePositions)
    }

    override fun update(context: Context, partOwner: Entity) {
        if (isOmniscient) {
            updateAsOmniscient(context)
            return
        }

        val world = context.world
        val currentPosition = world[partOwner] ?: return
        val nextVisiblePositions: MutableList<Position> = mutableListOf()
        val memoryPart = partOwner.find(MemoryPart::class)

        ShadowCasting.computeFOV(currentPosition.x, currentPosition.y, visionRange,
            isBlocking = { x, y ->
                world[x, y]?.any { it.find(PhysicalPart::class)?.doesBlockVision == true } ?: false
            }, markVisible = { x, y ->
                val visiblePosition = Position(x, y)
                nextVisiblePositions.add(visiblePosition)
                memoryPart?.remember(context, visiblePosition)
            })

        visiblePositions = nextVisiblePositions
    }

    private fun updateAsOmniscient(context: Context) {
        val world = context.world

        if (visiblePositions.size >= world.width * world.height) return

        visiblePositions = Position(world.width - 1, world.height - 1).map { it }
    }
}

fun Entity.visiblePositions(): List<Position> = find<SensoryPart>()?.visiblePositions ?: listOf()
