package com.helloworldramen.kingoyster.ai.architecture

abstract class AiOption<C : AiContext>(private val considerations: List<AiConsideration<C>>) {

    abstract fun execute(aiContext: C): Boolean

    fun evaluate(aiContext: C): Double {
        // If we have nothing to consider, just do it!
        if (considerations.isEmpty()) return 1.0

        var aggregateValue = 1.0
        val modificationFactor = 1.0 - (1.0 / considerations.size)

        for (consideration in considerations) {
            val value = consideration.normalizedEvaluation(aiContext)

            // Early return if a consideration yields 0.0 value (aggregate will be 0)
            if (value == 0.0) return 0.0

            // Add compensation factor based on total number of considerations.
            // This is to compensate against many considerations rapidly
            // bringing down the final consideration score.
            val catchUpFactor = (1.0 - value) * modificationFactor
            val finalValue = value + (catchUpFactor * value)

            aggregateValue *= finalValue
        }

        return aggregateValue
    }
}