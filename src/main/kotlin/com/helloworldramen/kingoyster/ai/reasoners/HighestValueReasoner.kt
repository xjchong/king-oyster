package com.helloworldramen.kingoyster.ai.reasoners

import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.GameAiReasoner
import com.helloworldramen.kingoyster.ai.architecture.AiOption

object HighestValueReasoner : ParallelizedReasoner(), GameAiReasoner {

    override fun prioritize(
        aiContext: GameAiContext,
        options: List<AiOption<GameAiContext>>
    ): List<AiOption<GameAiContext>> {
        val valueForOption = mapValuesForOptions(aiContext, options)

        return options.sortedByDescending { valueForOption[it] ?: 0.0 }
    }
}