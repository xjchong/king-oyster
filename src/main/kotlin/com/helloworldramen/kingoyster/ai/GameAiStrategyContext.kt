package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.architecture.*
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position

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

    fun withDirection(direction: Direction?): GameAiOptionContext {
        return GameAiOptionContext(context, entity, direction = direction)
    }
}

data class GameAiOptionContext(
    val context: Context,
    val entity: Entity,
    val target: Entity? = null,
    val position: Position? = null,
    val direction: Direction? = null
) : AiOptionContext {

    fun withTarget(target: Entity?): GameAiOptionContext {
        return copy(target = target)
    }

    fun withPosition(position: Position?): GameAiOptionContext {
        return copy(position = position)
    }

    fun withDirection(direction: Direction?): GameAiOptionContext {
        return copy(direction = direction)
    }
}

typealias GameAiStrategy = AiStrategy<GameAiStrategyContext, GameAiOptionContext>
typealias GameAiConsideration = AiConsideration<GameAiOptionContext>
typealias GameAiOption = AiOption<GameAiOptionContext>
typealias GameAiReasoner = AiReasoner<GameAiStrategyContext, GameAiOptionContext>

fun Position?.tag(): String {
    return if (this == null) "?,?"
    else "$x,$y"
}