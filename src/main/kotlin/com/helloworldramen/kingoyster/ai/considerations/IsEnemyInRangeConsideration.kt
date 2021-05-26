package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.parts.AttackablePart
import com.helloworldramen.kingoyster.parts.FactionPart

object IsEnemyInRangeConsideration : BooleanConsideration() {

    override fun isConditionTrue(aiContext: GameAiContext): Boolean {
        val (context, entity) = aiContext
        val entityPosition = context.world[entity] ?: return false
        val entityFaction = entity.find<FactionPart>()?.faction

        return entityPosition.neighbors().any { neighbor ->
            context.world[neighbor]?.any {
                it.has<AttackablePart>() &&
                        it.find<FactionPart>()?.faction != entityFaction
            } == true
        }
    }
}