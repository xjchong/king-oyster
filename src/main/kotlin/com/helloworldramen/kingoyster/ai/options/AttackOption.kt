package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Attack
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext

class AttackOption(optionContext: GameAiOptionContext) : GameAiOption(optionContext) {

    override fun execute(): Boolean {
        val (context, entity, target) = optionContext

        if (target == null) return false

        return target.respondToAction(Attack(context, entity))
    }
}