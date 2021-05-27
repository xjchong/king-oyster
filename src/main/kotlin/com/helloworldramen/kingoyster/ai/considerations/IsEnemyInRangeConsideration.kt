package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.parts.AttackablePart
import com.helloworldramen.kingoyster.parts.FactionPart
import com.helloworldramen.kingoyster.parts.faction

object IsEnemyInRangeConsideration : BooleanConsideration() {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        val (context, entity, target) = optionContext

        if (target == null) return false

        val entityPosition = context.world[entity] ?: return false
        val targetPosition = context.world[target] ?: return false

        return entityPosition.distanceFrom(targetPosition) <= 1.0
    }
}