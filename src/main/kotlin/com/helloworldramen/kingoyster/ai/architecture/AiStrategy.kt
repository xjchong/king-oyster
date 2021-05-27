package com.helloworldramen.kingoyster.ai.architecture

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class AiStrategy<C : AiStrategyContext, O : AiOptionContext>(private val considerations: List<AiConsideration<O>>) :
    CoroutineScope {

    final override val coroutineContext: CoroutineContext = Dispatchers.Default

    protected abstract fun listOptions(strategyContext: C): List<AiOption<O>>

    suspend fun evaluateOptions(strategyContext: C): Map<AiOption<O>, Double> {
        val options = listOptions(strategyContext)
        val scoreForOption = options.associateWith { 1.0 }.toMutableMap()
        val modificationFactor = 1.0 - (1.0 / considerations.size)

        if (considerations.isEmpty()) return scoreForOption

        options.map { option ->
            coroutineScope {
                launch {
                    var aggregateValue = 1.0

                    for (consideration in considerations) {
                        val value = consideration.normalizedEvaluation(option.optionContext)

                        // Early return if a consideration yields 0.0 value (aggregate will be 0)
                        if (value == 0.0) {
                            aggregateValue = 0.0
                            break
                        }

                        // Add compensation factor based on total number of considerations.
                        // This is to compensate against many considerations rapidly
                        // bringing down the final consideration score.
                        val catchUpFactor = (1.0 - value) * modificationFactor
                        val finalValue = value + (catchUpFactor * value)

                        aggregateValue *= finalValue
                    }

                    scoreForOption[option] = aggregateValue
                }
            }
        }.joinAll()

        return scoreForOption
    }
}