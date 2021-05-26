package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.parts.FactionPart
import com.helloworldramen.kingoyster.parts.SensoryPart


object IsEnemyInSightConsideration : BooleanConsideration() {

    override fun isConditionTrue(aiContext: GameAiContext): Boolean {
        val (context, entity) = aiContext
        val visiblePositions = entity.find(SensoryPart::class)?.visiblePositions ?: return false
        val faction = entity.find(FactionPart::class)?.faction ?: return false

        return visiblePositions.any { visiblePosition ->
            context.world[visiblePosition]?.any {
                it.find(FactionPart::class)?.faction ?: faction != faction
            } == true
        }
    }
}