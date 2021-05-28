package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiCurve
import com.helloworldramen.kingoyster.ai.curves.LinearCurve

abstract class BooleanConsideration : GameAiConsideration() {

    final override val curve: AiCurve = LinearCurve()
    final override val minValue: Double = 0.0
    final override val maxValue: Double = 1.0

    final override fun evaluate(optionContext: GameAiOptionContext): Double {
        return if (isConditionTrue(optionContext)) 1.0 else 0.0
    }

    protected abstract fun isConditionTrue(optionContext: GameAiOptionContext): Boolean
}