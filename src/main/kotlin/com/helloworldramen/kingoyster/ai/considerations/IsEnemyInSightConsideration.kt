package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.visiblePositions


object IsEnemyInSightConsideration : BooleanConsideration() {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        val (context, entity, target) = optionContext
        val targetPosition = context.positionOf(target) ?: return false
        val visiblePositions = entity.visiblePositions()

        return visiblePositions.contains(targetPosition)
    }
}