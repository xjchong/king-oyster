package com.helloworldramen.kingoyster.ai.architecture

abstract class AiOption<O : AiOptionContext> {

    open val tag: String? = this::class.simpleName?.take(4)

    abstract val parentStrategy: AiStrategy<out AiStrategyContext, O>
    abstract val optionContext: O

    abstract fun execute(): Boolean
}