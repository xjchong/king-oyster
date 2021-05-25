package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.curves.LinearCurve

abstract class BooleanConsideration : GameAiConsideration(LinearCurve()) {

    override val minValue: Double = 0.0
    override val maxValue: Double = 1.0

    override fun evaluate(aiContext: GameAiContext): Double {
        return if (isConditionTrue(aiContext)) 1.0 else 0.0
    }

    protected abstract fun isConditionTrue(aiContext: GameAiContext): Boolean
}