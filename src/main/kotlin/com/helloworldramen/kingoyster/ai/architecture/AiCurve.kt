package com.helloworldramen.kingoyster.ai.architecture

abstract class AiCurve {

    protected abstract fun transform(rawNormalizedValue: Double): Double

    fun boundedTransform(rawNormalizedValue: Double) : Double {
        return transform(rawNormalizedValue).coerceIn(0.0, 1.0)
    }
}