package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiCurve
import com.helloworldramen.kingoyster.ai.curves.LinearCurve

class ConstantConsideration(value: Double) : GameAiConsideration() {

    override val curve: AiCurve = LinearCurve(value, value)
    override val minValue: Double = 0.0
    override val maxValue: Double = 1.0

    override fun evaluate(optionContext: GameAiOptionContext): Double {
        return 1.0
    }
}