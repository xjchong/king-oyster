package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiCurve
import com.helloworldramen.kingoyster.parts.isAllyOf
import com.helloworldramen.kingoyster.parts.visiblePositions

class VisibleAlliesConsideration(override val curve: AiCurve) : GameAiConsideration() {

    override val minValue: Double = 0.0
    override val maxValue: Double = 8.0

    override fun evaluate(optionContext: GameAiOptionContext): Double {
        val (context, entity) = optionContext

        return entity.visiblePositions().filter { visiblePosition ->
            context.entitiesAt(visiblePosition)?.any { otherEntity ->
                entity != otherEntity && entity.isAllyOf(otherEntity)
            } == true
        }.size.toDouble()
    }
}