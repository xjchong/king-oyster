package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.*
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.WeaponAttackOption
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.AttackPattern
import com.helloworldramen.kingoyster.parts.combat.defaultAttackPattern
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.weapon
import com.helloworldramen.kingoyster.parts.weaponAttackPattern

class WeaponAttackEnemyStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "ATK"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val attacker = strategyContext.entity
        val weaponAttackPattern = attacker.weapon()?.weaponAttackPattern()

        // If there is no weapon pattern, use the attacker's default attack pattern.
        return if (weaponAttackPattern == null) {
            calculateAttackOptions(strategyContext, attacker.defaultAttackPattern())
        } else {
            // If there is a weapon pattern, see if it applies here. If not, then fallback to the default attack pattern.
            calculateAttackOptions(strategyContext, weaponAttackPattern).ifEmpty {
                calculateAttackOptions(strategyContext, attacker.defaultAttackPattern())
            }
        }
    }

    private fun calculateAttackOptions(strategyContext: GameAiStrategyContext, attackPattern: AttackPattern): List<GameAiOption> {
        val (context, attacker) = strategyContext

        return Direction.all().filter{ direction ->
            attackPattern.isUsable(context, attacker, direction)
        }.associateWith { direction ->
            attackPattern.calculateDamageForPosition(context, attacker, direction).keys
        }.filter { (_, positions) ->
            positions.any { position ->
                context.entitiesAt(position)?.any { it.isEnemyOf(attacker) } == true
            }
        }.keys.map {
            WeaponAttackOption(this, strategyContext.withDirection(it))
        }
    }
}