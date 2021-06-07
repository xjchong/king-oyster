package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiCurve
import com.helloworldramen.kingoyster.ai.curves.LinearCurve

abstract class BooleanConsideration(
    whenTrue: Double = 1.0,
    whenFalse: Double = 0.0
) : GameAiConsideration() {

    final override val curve: AiCurve = LinearCurve()
    final override val minValue: Double = 0.0
    final override val maxValue: Double = 1.0

    private val whenTrue: Double = whenTrue.coerceIn(0.0, 1.0)
    private val whenFalse: Double = whenFalse.coerceIn(0.0, 1.0)

    final override fun evaluate(optionContext: GameAiOptionContext): Double {
        return if (isConditionTrue(optionContext)) whenTrue else whenFalse
    }

    protected abstract fun isConditionTrue(optionContext: GameAiOptionContext): Boolean
}