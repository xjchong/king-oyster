package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.architecture.*
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position

data class GameAiStrategyContext(
    val context: Context,
    val entity: Entity,
) : AiStrategyContext {

    fun asOptionContext(): GameAiOptionContext {
        return GameAiOptionContext(context, entity)
    }

    fun withTarget(target: Entity?): GameAiOptionContext {
        return GameAiOptionContext(context, entity, target)
    }

    fun withPosition(position: Position?): GameAiOptionContext {
        return GameAiOptionContext(context, entity, position = position)
    }
}

data class GameAiOptionContext(
    val context: Context,
    val entity: Entity,
    val target: Entity? = null,
    val position: Position? = null
) : AiOptionContext {

    fun withTarget(target: Entity?): GameAiOptionContext {
        return copy(target = target)
    }

    fun withPosition(position: Position?): GameAiOptionContext {
        return copy(position = position)
    }
}

typealias GameAiStrategy = AiStrategy<GameAiStrategyContext, GameAiOptionContext>
typealias GameAiConsideration = AiConsideration<GameAiOptionContext>
typealias GameAiOption = AiOption<GameAiOptionContext>
typealias GameAiReasoner = AiReasoner<GameAiStrategyContext, GameAiOptionContext>