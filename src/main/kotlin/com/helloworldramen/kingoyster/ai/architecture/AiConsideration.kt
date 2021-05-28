package com.helloworldramen.kingoyster.ai.architecture


abstract class AiConsideration<O : AiOptionContext> {

    protected abstract val curve: AiCurve
    protected abstract val minValue: Double
    protected abstract val maxValue: Double

    protected abstract fun evaluate(optionContext: O): Double

    fun normalizedEvaluation(optionContext: O): Double {
        return curve.boundedTransform((evaluate(optionContext) - minValue) / (maxValue - minValue))
    }
}