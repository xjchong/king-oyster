package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.curves.LinearCurve

class ConstantConsideration(value: Double) : GameAiConsideration(LinearCurve(value, value)){

    override val minValue: Double = 1.0
    override val maxValue: Double = 1.0

    override fun evaluate(aiContext: GameAiContext): Double {
        return 1.0
    }
}