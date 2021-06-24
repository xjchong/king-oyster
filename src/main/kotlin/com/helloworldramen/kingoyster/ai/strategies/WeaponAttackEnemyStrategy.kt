package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.*
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.WeaponAttackOption
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.parts.weapon
import com.helloworldramen.kingoyster.parts.weaponAttackPattern

class WeaponAttackEnemyStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "WAK"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, attacker) = strategyContext
        val weaponAttackPattern = attacker.weapon()?.weaponAttackPattern() ?: return listOf()

        return Direction.all().filter { direction ->
            weaponAttackPattern.isUsable(context, attacker, direction)
        }.map {
            WeaponAttackOption(this, strategyContext.withDirection(it))
        }
    }
}