package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.utilities.ShadowCasting

class SensoryPart(
    var visionRange: Int
) : Part {
    var visiblePositions: List<Position> = listOf()
        private set

    private constructor(visionRange: Int, visiblePositions: List<Position>): this(visionRange) {
        this.visiblePositions = visiblePositions.toList()
    }

    override fun copy(): Part {
        return SensoryPart(visionRange, visiblePositions)
    }

    override fun update(context: Context, partOwner: Entity) {
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
}