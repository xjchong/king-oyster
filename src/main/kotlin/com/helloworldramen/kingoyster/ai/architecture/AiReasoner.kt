package com.helloworldramen.kingoyster.ai.architecture

interface AiReasoner<C : AiStrategyContext, O : AiOptionContext> {

    fun prioritize(strategyContext: C, strategies: List<AiStrategy<C, O>>): List<Pair<AiOption<O>, Double>>
}