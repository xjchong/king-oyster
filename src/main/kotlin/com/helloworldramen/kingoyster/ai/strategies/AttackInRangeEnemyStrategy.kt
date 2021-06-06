package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.AttackOption
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.parts.combat.attackPattern
import com.helloworldramen.kingoyster.parts.isEnemyOf

class AttackInRangeEnemyStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "ATK"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, attacker) = strategyContext
        val attackPattern = attacker.attackPattern()
        val attackedPositionsForDirection = Direction.all().associateWith { direction ->
            attackPattern.calculateDamageForPosition(context, attacker, direction).keys
        }
        val attackableDirections = attackedPositionsForDirection.filter { (_, positions) ->
            positions.any { position ->
                context.entitiesAt(position)?.any { it.isEnemyOf(attacker) } != null
            }
        }.keys

        return attackableDirections.map {
            AttackOption(this, strategyContext.withDirection(it))
        }
    }
}