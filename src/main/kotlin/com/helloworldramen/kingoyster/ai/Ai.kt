package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.considerations.ConstantConsideration
import com.helloworldramen.kingoyster.ai.considerations.IsEnemyInRangeConsideration
import com.helloworldramen.kingoyster.ai.options.AttackRandomEnemyOption
import com.helloworldramen.kingoyster.ai.options.WanderRandomlyOption
import com.helloworldramen.kingoyster.ai.reasoners.HighestValueReasoner
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import godot.global.GD

object Ai {

    fun actForEntity(context: Context, entity: Entity) {
        val startTime = System.nanoTime()
        val aiContext = GameAiContext(context, entity)

        val entityOptions = when (entity.name) {
            "slime" -> HighestValueReasoner.prioritize(aiContext, listOf(
                AttackRandomEnemyOption(
                    IsEnemyInRangeConsideration
                ),
                WanderRandomlyOption(
                    ConstantConsideration(0.5)
                )
            ))
            else -> listOf()
        }

        if (!entityOptions.any { it.execute(aiContext) }) {
            entity.idle(context.world)
        }
        GD.print("ai time for ${entity.name}: ${(System.nanoTime() - startTime) / 1000000.0}")
    }
}