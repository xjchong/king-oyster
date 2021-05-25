package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.GameAiOption

class IdleOption(vararg considerations: GameAiConsideration) : GameAiOption(considerations.toList()) {

    override fun execute(aiContext: GameAiContext): Boolean {
        val (context, entity) = aiContext

        return entity.idle(context.world)
    }
}