package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiCurve
import com.helloworldramen.kingoyster.parts.health
import com.helloworldramen.kingoyster.parts.maxHealth

class OwnHealthConsideration(override val curve: AiCurve) : GameAiConsideration() {

    override val minValue: Double = 0.0
    override val maxValue: Double = 1.0

    override fun evaluate(optionContext: GameAiOptionContext): Double {
        with(optionContext.entity) {
            return health() / maxHealth().coerceAtLeast(1).toDouble()
        }
    }
}