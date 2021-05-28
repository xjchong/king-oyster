package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.AttackOption
import com.helloworldramen.kingoyster.parts.isEnemyOf

class AttackInRangeEnemiesStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "ATK"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, attacker) = strategyContext
        val attackerPosition = context.positionOf(attacker) ?: return listOf()
        val enemies = attackerPosition.neighbors().flatMap { neighbor ->
            context.entitiesAt(neighbor)?.filter { it.isEnemyOf(attacker) } ?: listOf()
        }

        return enemies.map {
            AttackOption(this, strategyContext.withTarget(it))
        }
    }
}