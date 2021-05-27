package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext

class IdleOption(optionContext: GameAiOptionContext) : GameAiOption(optionContext) {

    override fun execute(): Boolean {
        val (context, entity) = optionContext

        return entity.idle(context.world)
    }
}