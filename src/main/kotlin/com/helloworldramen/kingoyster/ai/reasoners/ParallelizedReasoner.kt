package com.helloworldramen.kingoyster.ai.reasoners

import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.GameAiOption
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class ParallelizedReasoner : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    protected fun mapValuesForOptions(aiContext: GameAiContext, options: List<GameAiOption>): Map<GameAiOption, Double> {
        val valueForOption = mutableMapOf<GameAiOption, Double>()

        runBlocking {
            options.map {
                launch {
                    valueForOption[it] = it.evaluate(aiContext)
                }
            }
        }

        return valueForOption
    }
}