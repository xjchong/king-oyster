package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext
import com.helloworldramen.kingoyster.ai.tag

class MoveOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "mov.${optionContext.position.tag()}"

    override fun execute(): Boolean {
        val (context, entity, _, position) = optionContext

        if (position == null) return false

        return entity.respondToAction(Move(context, entity, position))
    }
}