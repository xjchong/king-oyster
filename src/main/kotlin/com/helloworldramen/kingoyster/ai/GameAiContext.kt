package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiContext
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.architecture.AiReasoner
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position

data class GameAiContext(
    val context: Context,
    val entity: Entity,
    val target: Entity? = null,
    val position: Position? = null) : AiContext

typealias GameAiConsideration = AiConsideration<GameAiContext>
typealias GameAiOption = AiOption<GameAiContext>
typealias GameAiReasoner = AiReasoner<GameAiContext>