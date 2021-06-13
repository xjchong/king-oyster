package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.SensoryPart


class KnowsPlayerPositionConsideration(whenTrue: Double, whenFalse: Double) : BooleanConsideration(whenTrue, whenFalse) {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        val (context, entity) = optionContext
        val sensoryPart = entity.find<SensoryPart>() ?: return false
        val currentPlayerPosition = context.positionOf(context.player) ?: return false

        return sensoryPart.playerPosition == currentPlayerPosition
                || sensoryPart.visiblePositions.contains(currentPlayerPosition)
    }
}