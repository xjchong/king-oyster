package com.helloworldramen.kingoyster.ai.curves

import com.helloworldramen.kingoyster.ai.architecture.AiCurve

class LinearCurve(private val startValue: Double = 0.0, private val endValue: Double = 1.0) : AiCurve() {

    override fun transform(rawNormalizedValue: Double): Double {
        return ((endValue - startValue) * rawNormalizedValue) + startValue
    }
}