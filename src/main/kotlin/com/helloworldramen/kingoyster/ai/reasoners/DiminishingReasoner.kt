package com.helloworldramen.kingoyster.ai.reasoners

import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiReasoner
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.utilities.MutableWeightedCollection
import com.helloworldramen.kingoyster.utilities.WeightedEntry
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class DiminishingReasoner(private val diminishRate: Double) : GameAiReasoner, ParallelizedOptionScorer {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    override fun prioritize(
        strategyContext: GameAiStrategyContext,
        strategies: List<AiStrategy<GameAiStrategyContext, GameAiOptionContext>>
    ): List<Pair<AiOption<GameAiOptionContext>, Double>> {
        val scoreForOption = generateScoreForOptionParallelized(strategyContext, strategies)
        var remainingScore = 1000.0
        val weightedOptions = MutableWeightedCollection(
            scoreForOption.map {
                val score = remainingScore * diminishRate

                remainingScore -= score
                WeightedEntry(score.roundToInt(), Pair(it.key, it.value))
            }
        )

        val prioritizedOptions = mutableListOf<Pair<GameAiOption, Double>>()

        for (option in scoreForOption) { // The iterable here is just used as an arbitrary counter.
            val nextOption = weightedOptions.remove() ?: break

            prioritizedOptions.add(nextOption)
        }

        return prioritizedOptions
    }
}