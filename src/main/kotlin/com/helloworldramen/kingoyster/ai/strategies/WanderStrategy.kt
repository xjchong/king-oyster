package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.*
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.MoveOption
import com.helloworldramen.kingoyster.parts.canPass

class WanderStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "WDR"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, entity) = strategyContext
        val currentPosition = context.positionOf(entity) ?: return listOf()
        val passableNeighbors = currentPosition.neighbors().filter { neighbor ->
            context.entitiesAt(neighbor).all { entity.canPass(it) }
        }

        return passableNeighbors.shuffled().map {
            MoveOption(this, strategyContext.withPosition(it))
        }
    }
}