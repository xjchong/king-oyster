package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.visiblePositions

class IsEnemyInSightConsideration(whenTrue: Double, whenFalse: Double) : BooleanConsideration(whenTrue, whenFalse) {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        val (context, entity) = optionContext
        val visiblePositions = entity.visiblePositions()

        return visiblePositions.any { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }
    }
}