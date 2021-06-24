package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.DefaultAttackOption
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.parts.combat.defaultAttackPattern

class DefaultAttackEnemyStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "ATK"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, attacker) = strategyContext
        val attackPattern = attacker.defaultAttackPattern()

        return Direction.all().filter { direction ->
            attackPattern.isUsable(context, attacker, direction)
        }.map {
            DefaultAttackOption(this, strategyContext.withDirection(it))
        }
    }
}