package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.considerations.ConstantConsideration
import com.helloworldramen.kingoyster.ai.considerations.HasWeaponConsideration
import com.helloworldramen.kingoyster.ai.considerations.VisibleAlliesConsideration
import com.helloworldramen.kingoyster.ai.considerations.OwnHealthConsideration
import com.helloworldramen.kingoyster.ai.curves.LinearCurve
import com.helloworldramen.kingoyster.ai.reasoners.HighestValueReasoner
import com.helloworldramen.kingoyster.ai.reasoners.PurelyRandomReasoner
import com.helloworldramen.kingoyster.ai.strategies.*
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.telegraphedPositions

object Ai {

    fun actForEntity(context: Context, entity: Entity) {
        if (entity.telegraphedPositions().isNotEmpty()) {
            entity.idle(context.world)
            return
        }

        val startTime = System.nanoTime()
        val aiContext = GameAiStrategyContext(context, entity)

        val entityOptionsWithScore = when (entity.name) {
            "ghost" -> HighestValueReasoner.prioritize(aiContext, listOf(
                FleeFromEnemyStrategy(
                    ConstantConsideration(0.6)
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                )
            ))
            "goblin" -> HighestValueReasoner.prioritize(aiContext, listOf(
                WeaponAttackEnemyStrategy(
                    ConstantConsideration(0.9),
                ),
                ChaseEnemyStrategy(
                    ConstantConsideration(0.8),
                ),
                TakeWeaponStrategy(
                    HasWeaponConsideration(0.0, 1.0),
                    ConstantConsideration(0.9)
                ),
                FleeFromEnemyStrategy(
                    OwnHealthConsideration(LinearCurve(2.0, 0.5)),
                    VisibleAlliesConsideration(LinearCurve(1.0, 0.3)),
                    HasWeaponConsideration(0.8, 1.0)
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                ),
            ))
            "hobgoblin" -> HighestValueReasoner.prioritize(aiContext, listOf(
                WeaponAttackEnemyStrategy(
                    ConstantConsideration(0.9),
                ),
                ChaseEnemyStrategy(
                    ConstantConsideration(0.8),
                ),
                TakeWeaponStrategy(
                    HasWeaponConsideration(0.0, 1.0),
                    ConstantConsideration(0.9)
                ),
                FleeFromEnemyStrategy(
                    OwnHealthConsideration(LinearCurve(1.0, 0.5)),
                    VisibleAlliesConsideration(LinearCurve(1.0, 0.6)),
                    HasWeaponConsideration(0.5, 0.8)
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                ),
            ))
            "blue slime", "red slime" -> PurelyRandomReasoner.prioritize(aiContext, listOf(
                WeaponAttackEnemyStrategy(
                    ConstantConsideration(0.5)
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
        println("AI(${totalTimeString}ms) ${entity.name}: ${optionsWithScore.map { (option, score) ->
            Pair("${option.parentStrategy.tag}-${option.tag}", String.format("%.2f", score))
        }}")
    }
}