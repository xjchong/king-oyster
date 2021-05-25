package com.helloworldramen.kingoyster.ai.architecture


abstract class AiConsideration<C : AiContext>(curve: AiCurve) {

    protected abstract val minValue: Double
    protected abstract val maxValue: Double

    protected abstract fun evaluate(aiContext: C): Double

    fun normalizedEvaluation(aiContext: C): Double {
        return (evaluate(aiContext) - minValue) / (maxValue - minValue)
    }
}