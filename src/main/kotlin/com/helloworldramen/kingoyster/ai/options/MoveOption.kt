package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext

class MoveOption(optionContext: GameAiOptionContext) : GameAiOption(optionContext) {

    override fun execute(): Boolean {
        val (context, entity, _, position) = optionContext

        if (position == null) return false

        return entity.respondToAction(Move(context, entity, position))
    }
}