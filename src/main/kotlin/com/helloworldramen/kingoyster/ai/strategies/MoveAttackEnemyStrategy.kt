package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.MoveAttackOption
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.isEnemyOf

class MoveAttackEnemyStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "MVK"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, attacker) = strategyContext
        val moveAttackPattern = attacker.find<MovementPart>()?.attackPattern ?: return listOf()

        return Direction.all().filter { direction ->
            moveAttackPattern.telegraphPositions(context ,attacker, direction).any { position ->
                context.entitiesAt(position)?.any { it.isEnemyOf(attacker) } == true
            }
        }.map {
            MoveAttackOption(this, strategyContext.withDirection(it))
        }
    }
}