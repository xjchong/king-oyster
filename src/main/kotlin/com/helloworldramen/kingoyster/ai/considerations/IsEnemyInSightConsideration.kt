package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.parts.faction
import com.helloworldramen.kingoyster.parts.visiblePositions


object IsEnemyInSightConsideration : BooleanConsideration() {

    override fun isConditionTrue(aiContext: GameAiContext): Boolean {
        val (context, entity) = aiContext
        val visiblePositions = entity.visiblePositions()
        val faction = entity.faction() ?: return false

        return visiblePositions.any { visiblePosition ->
            context.world[visiblePosition]?.any {
                it.faction() ?: faction != faction
            } == true
        }
    }
}