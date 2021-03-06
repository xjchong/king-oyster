package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.visiblePositions


class IsTargetInSightConsideration(whenTrue: Double, whenFalse: Double) : BooleanConsideration(whenTrue, whenFalse) {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        val (context, entity, target) = optionContext
        val targetPosition = context.positionOf(target) ?: return false
        val visiblePositions = entity.visiblePositions()

        return visiblePositions.contains(targetPosition)
    }
}