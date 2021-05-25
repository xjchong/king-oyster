package com.helloworldramen.kingoyster.ai.architecture

interface AiReasoner<C : AiContext> {

    fun prioritize(aiContext: C, options: List<AiOption<C>>): List<AiOption<C>>
}