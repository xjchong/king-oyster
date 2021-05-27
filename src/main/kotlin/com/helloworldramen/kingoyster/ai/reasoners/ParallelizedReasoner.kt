package com.helloworldramen.kingoyster.ai.reasoners

import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class ParallelizedReasoner : CoroutineScope {

    final override val coroutineContext: CoroutineContext = Dispatchers.Default

    protected fun generateScoreForOptionParallelized(
        strategyContext: GameAiStrategyContext,
        strategies: List<GameAiStrategy>
    ): Map<GameAiOption, Double> {
        val scoreForOptionForStrategy = mutableMapOf<GameAiStrategy, Map<GameAiOption, Double>>()

        runBlocking {
            strategies.map { strategy ->
                launch {
                    scoreForOptionForStrategy[strategy] = strategy.evaluateOptions(strategyContext)
                }
            }
        }

        val scoreForOption = mutableMapOf<GameAiOption, Double>()

        scoreForOptionForStrategy.values.forEach { strategyScoreForOption ->
            strategyScoreForOption.forEach { (option, score) -> scoreForOption[option] = score }
        }

        return scoreForOption
    }
}