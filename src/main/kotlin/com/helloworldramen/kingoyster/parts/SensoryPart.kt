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

    override fun update(context: Context, partOwner: Entity) {
        val world = context.world
        val currentPosition = world[partOwner] ?: return
        val nextVisiblePositions: MutableList<Position> = mutableListOf()

        ShadowCasting.computeFOV(currentPosition.x, currentPosition.y, visionRange,
            isBlocking = { x, y ->
                world[x, y]?.any { it.find(PhysicalPart::class)?.doesBlockVision == true } ?: false
            }, markVisible = { x, y ->
                nextVisiblePositions.add(Position(x, y))
            })

        visiblePositions = nextVisiblePositions
    }
}