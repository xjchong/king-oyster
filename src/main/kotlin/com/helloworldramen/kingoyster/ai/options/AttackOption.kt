package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.WeaponAttack
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext

class AttackOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "atk"

    override fun execute(): Boolean {
        val (context, entity, target) = optionContext

        if (target == null) return false

        return target.respondToAction(WeaponAttack(context, entity))
    }
}