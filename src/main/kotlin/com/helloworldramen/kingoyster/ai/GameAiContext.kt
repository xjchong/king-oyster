package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiContext
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity

data class GameAiContext(val context: Context, val entity: Entity) : AiContext

typealias GameAiConsideration = AiConsideration<GameAiContext>
typealias GameAiOption = AiOption<GameAiContext>