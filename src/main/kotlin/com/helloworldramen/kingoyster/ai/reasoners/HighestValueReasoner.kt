package com.helloworldramen.kingoyster.ai.reasoners

import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.GameAiReasoner
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import godot.global.GD

object HighestValueReasoner : GameAiReasoner {

    override fun prioritize(
        aiContext: GameAiContext,
        options: List<AiOption<GameAiContext>>
    ): List<AiOption<GameAiContext>> {
        GD.print(options.map { Pair(it::class.simpleName, it.evaluate(aiContext)) })
        return options.sortedByDescending { it.evaluate(aiContext) }
    }
}