package com.helloworldramen.kingoyster.ai.curves

import com.helloworldramen.kingoyster.ai.architecture.AiCurve

class LinearCurve(startValue: Double = 0.0, endValue: Double = 1.0) : AiCurve {

    private val y1: Double = startValue.coerceIn(0.0, 1.0)
    private val y2: Double = endValue.coerceIn(0.0, 1.0)

    override fun transform(rawNormalizedValue: Double): Double {
        return ((y2 - y1) * rawNormalizedValue) + y1
    }
}