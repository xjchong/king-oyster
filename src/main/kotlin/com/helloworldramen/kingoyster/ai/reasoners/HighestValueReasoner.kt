package com.helloworldramen.kingoyster.ai.reasoners

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.GameAiReasoner
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object HighestValueReasoner : ParallelizedOptionScorer, GameAiReasoner {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    override fun prioritize(
        strategyContext: GameAiStrategyContext,
        strategies: List<AiStrategy<GameAiStrategyContext, GameAiOptionContext>>
    ): List<Pair<AiOption<GameAiOptionContext>, Double>> {
        val scoreForOption = generateScoreForOptionParallelized(strategyContext, strategies)

        return scoreForOption.keys.sortedByDescending {
            scoreForOption[it]
        }.map {
            Pair(it, scoreForOption[it] ?: 0.0)
        }
    }

}