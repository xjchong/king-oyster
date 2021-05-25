package com.helloworldramen.kingoyster.ai.reasoners

import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.architecture.AiReasoner

object HighestValueReasoner : AiReasoner<GameAiContext> {

    override fun prioritize(aiContext: GameAiContext, options: List<AiOption<GameAiContext>>): List<AiOption<GameAiContext>> {
        return options.sortedByDescending { it.evaluate(aiContext) }
    }
}