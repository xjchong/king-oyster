package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Attack
import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.GameAiOption

class AttackRandomEnemyOption(vararg considerations: GameAiConsideration) : GameAiOption(considerations.toList()) {

    override fun execute(aiContext: GameAiContext): Boolean {
        val (context, attacker) = aiContext
        val attackerPosition = context.world[attacker] ?: return false

        return attackerPosition.neighborsShuffled().any { neighbor ->
            context.world.respondToActions(neighbor, Attack(context, attacker)) != null
        }
    }
}