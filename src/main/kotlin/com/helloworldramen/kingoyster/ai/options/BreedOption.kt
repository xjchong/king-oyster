package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Breed
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext

class BreedOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "brd"

    override fun execute(): Boolean {
        val (context, entity) = optionContext

        return entity.respondToAction(Breed(context, entity))
    }
}