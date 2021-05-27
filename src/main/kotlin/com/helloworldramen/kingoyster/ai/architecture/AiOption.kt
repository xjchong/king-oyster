package com.helloworldramen.kingoyster.ai.architecture

abstract class AiOption<O : AiOptionContext>(val optionContext: O) {

    abstract fun execute(): Boolean
}