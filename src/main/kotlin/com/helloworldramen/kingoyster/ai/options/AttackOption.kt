package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.TelegraphActions
import com.helloworldramen.kingoyster.actions.WeaponAttack
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext
import com.helloworldramen.kingoyster.parts.Telegraph
import com.helloworldramen.kingoyster.parts.TelegraphPart
import com.helloworldramen.kingoyster.parts.TelegraphPayload

class AttackOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "atk"

    override fun execute(): Boolean {
        val (context, entity, _, _, direction) = optionContext

        if (direction == null) return false

        val attackOption = WeaponAttack(context, entity, direction)

        return entity.respondToAction(attackOption)
    }
}