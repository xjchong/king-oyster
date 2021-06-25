package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.BreedOption

class BreedStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "BRD"
    override val considerations: List<GameAiConsideration> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        return listOf(BreedOption(this, strategyContext.asOptionContext()))
    }
}