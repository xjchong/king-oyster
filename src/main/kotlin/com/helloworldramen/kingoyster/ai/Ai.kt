package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.considerations.ConstantConsideration
import com.helloworldramen.kingoyster.ai.considerations.IsEnemyInRangeConsideration
import com.helloworldramen.kingoyster.ai.considerations.IsEnemyInSightConsideration
import com.helloworldramen.kingoyster.ai.strategies.AttackInRangeEnemiesStrategy
import com.helloworldramen.kingoyster.ai.strategies.ChaseVisibleEnemiesStrategy
import com.helloworldramen.kingoyster.ai.strategies.IdleStrategy
import com.helloworldramen.kingoyster.ai.strategies.WanderStrategy
import com.helloworldramen.kingoyster.ai.reasoners.HighestValueReasoner
import com.helloworldramen.kingoyster.ai.reasoners.PurelyRandomReasoner
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import godot.global.GD

object Ai {

    fun actForEntity(context: Context, entity: Entity) {
        val startTime = System.nanoTime()
        val aiContext = GameAiStrategyContext(context, entity)

        val entityOptionsWithScore = when (entity.name) {
            "ghost" -> HighestValueReasoner.prioritize(aiContext, listOf(
                WanderStrategy(
                    ConstantConsideration(0.5)
                )
            ))
            "goblin" -> HighestValueReasoner.prioritize(aiContext, listOf(
                AttackInRangeEnemiesStrategy(
                    IsEnemyInRangeConsideration
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                ),
                ChaseVisibleEnemiesStrategy(
                    IsEnemyInSightConsideration
                )
            ))
            "slime" -> PurelyRandomReasoner.prioritize(aiContext, listOf(
                AttackInRangeEnemiesStrategy(
                    IsEnemyInRangeConsideration
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                ),
                IdleStrategy(
                    ConstantConsideration(0.5)
                )
            ))
            else -> listOf()
        }

        if (!entityOptionsWithScore.any { it.first.execute() }) {
            entity.idle(context.world)
        }

        log(entity, entityOptionsWithScore, startTime)
    }

    private fun log(entity: Entity, optionsWithScore: List<Pair<GameAiOption, Double>>, startTime: Long) {
        val totalTimeString = String.format("%.2f", (System.nanoTime() - startTime) / 1000000.0)
        GD.print("AI(${totalTimeString}ms) ${entity.name}: ${optionsWithScore.map { (option, score) ->
            Pair("${option.parentStrategy.tag}-${option.tag}", String.format("%.2f", score))
        }}")
    }
}