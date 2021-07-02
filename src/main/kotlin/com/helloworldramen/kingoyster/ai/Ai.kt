package com.helloworldramen.kingoyster.ai

import com.helloworldramen.kingoyster.ai.considerations.*
import com.helloworldramen.kingoyster.ai.curves.LinearCurve
import com.helloworldramen.kingoyster.ai.reasoners.DiminishingReasoner
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
            "boar" -> DiminishingReasoner(0.7).prioritize(aiContext, listOf(
                MoveAttackEnemyStrategy(
                    IsEnemyInSightConsideration(1.0, 0.0),
                    OwnHealthConsideration(LinearCurve(0.7, 1.0))
                ),
                FleeFromEnemyStrategy(
                    OwnHealthConsideration(LinearCurve(1.0, 0.0))
                ),
                WanderStrategy(
                    IsEnemyInSightConsideration(0.5, 1.0)
                ),
                DefaultAttackEnemyStrategy(
                    IsEnemyInSightConsideration(1.0, 0.0),
                    OwnHealthConsideration(LinearCurve(0.7, 1.0))
                ),
            ))
            "bomb" -> DiminishingReasoner(0.7).prioritize(aiContext, listOf(
                ChaseEnemyStrategy(
                    ConstantConsideration(0.9)
                ),
                DefaultAttackEnemyStrategy(
                    IsEnemyInSightConsideration(1.0, 0.0),
                    OwnHealthConsideration(LinearCurve(0.0, 2.0))
                ),
                WanderStrategy(
                    IsEnemyInSightConsideration(0.2, 0.8)
                ),
                UseAbilityStrategy(
                    OwnHealthConsideration(LinearCurve(2.0, 0.0))
                ),
            ))
            "ghost" -> HighestValueReasoner.prioritize(aiContext, listOf(
                FleeFromEnemyStrategy(
                    ConstantConsideration(0.6)
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                )
            ))
            "goblin" -> DiminishingReasoner(0.9).prioritize(aiContext, listOf(
                WeaponAttackEnemyStrategy(
                    DoesSelfHaveWeaponConsideration(1.0, 0.0),
                    ConstantConsideration(0.9),
                ),
                DefaultAttackEnemyStrategy(
                    DoesSelfHaveWeaponConsideration(0.0, 1.0),
                    ConstantConsideration(0.9),
                ),
                ChaseEnemyStrategy(
                    ConstantConsideration(0.8),
                ),
                TakeWeaponStrategy(
                    DoesSelfHaveWeaponConsideration(0.0, 1.0),
                    ConstantConsideration(0.9)
                ),
                FleeFromEnemyStrategy(
                    OwnHealthConsideration(LinearCurve(2.0, 0.5)),
                    VisibleAlliesConsideration(LinearCurve(1.0, 0.3)),
                    DoesSelfHaveWeaponConsideration(0.8, 1.0),
                    DoesTargetHaveWeaponConsideration(1.0, 0.5)
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                ),
            ))
            "hobgoblin" -> HighestValueReasoner.prioritize(aiContext, listOf(
                WeaponAttackEnemyStrategy(
                    DoesSelfHaveWeaponConsideration(1.0, 0.0),
                    ConstantConsideration(0.9),
                ),
                DefaultAttackEnemyStrategy(
                    DoesSelfHaveWeaponConsideration(0.0, 1.0),
                    ConstantConsideration(0.9),
                ),
                ChaseEnemyStrategy(
                    ConstantConsideration(0.8),
                ),
                TakeWeaponStrategy(
                    DoesSelfHaveWeaponConsideration(0.0, 1.0),
                    ConstantConsideration(0.9)
                ),
                FleeFromEnemyStrategy(
                    OwnHealthConsideration(LinearCurve(1.0, 0.5)),
                    VisibleAlliesConsideration(LinearCurve(1.0, 0.6)),
                    DoesSelfHaveWeaponConsideration(0.5, 0.8),
                    DoesTargetHaveWeaponConsideration(1.0, 0.5)
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                ),
            ))
            "blue slime", "red slime" -> PurelyRandomReasoner.prioritize(aiContext, listOf(
                DefaultAttackEnemyStrategy(
                    ConstantConsideration(0.5)
                ),
                WanderStrategy(
                    ConstantConsideration(0.5)
                ),
                IdleStrategy(
                    ConstantConsideration(0.5)
                )
            ))
            "giant rat" -> DiminishingReasoner(0.7).prioritize(aiContext, listOf(
                BreedStrategy(
                    IsEnemyInSightConsideration(0.0, 1.0),
                    KnowsPlayerPositionConsideration(1.0, 0.0),
                    OwnHealthConsideration(LinearCurve(1.0, 0.5)),
                ),
                ChaseEnemyStrategy(
                    ConstantConsideration(0.66),
                ),
                FleeFromEnemyStrategy(
                    IsTargetInSightConsideration(1.0, 0.0),
                    OwnHealthConsideration(LinearCurve(1.0, 0.5)),
                    VisibleAlliesConsideration(LinearCurve(1.0, 0.2)),
                ),
                WanderStrategy(
                    ConstantConsideration(0.65)
                ),
                DefaultAttackEnemyStrategy(
                    ConstantConsideration(0.75)
                ),
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