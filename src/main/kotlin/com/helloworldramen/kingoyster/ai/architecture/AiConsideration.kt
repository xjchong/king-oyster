package com.helloworldramen.kingoyster.ai.architecture


abstract class AiConsideration<O : AiOptionContext>(private val curve: AiCurve) {

    protected abstract val minValue: Double
    protected abstract val maxValue: Double

    protected abstract fun evaluate(optionContext: O): Double

    fun normalizedEvaluation(optionContext: O): Double {
        return curve.transform((evaluate(optionContext) - minValue) / (maxValue - minValue))
    }
}