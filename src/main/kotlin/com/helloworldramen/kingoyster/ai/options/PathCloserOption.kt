package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext
import com.helloworldramen.kingoyster.ai.tag
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.extensions.asPosition
import com.helloworldramen.kingoyster.parts.combat.isKillable
import com.helloworldramen.kingoyster.parts.isCorporeal
import com.helloworldramen.kingoyster.parts.isPassable
import com.helloworldramen.kingoyster.utilities.AStar

class PathCloserOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "ptc.${optionContext.position.tag()}"

    override fun execute(): Boolean {
        val (context, entity, _, position) = optionContext

        if (position == null) return false

        val currentPosition = context.positionOf(entity) ?: return false
        val nextPosition = AStar.getPath(
            start = currentPosition.asPair(),
            goal = position.asPair(),
            cost = { _, to: Pair<Int, Int> ->
                val entities = context.entitiesAt(Position(to))

                when {
                    entities == null -> 999.0
                    !entity.isCorporeal() -> 1.0
                    entities.any { !it.isPassable() && !it.isKillable()} -> 888.0
                    entities.any { !it.isPassable() } -> 3.0
                    else -> 1.0
                }
            },
            heuristic = AStar.MANHATTAN_HEURISTIC
        ).firstOrNull()?.asPosition() ?: return false

        return entity.respondToAction(Move(context, entity, nextPosition))
    }
}