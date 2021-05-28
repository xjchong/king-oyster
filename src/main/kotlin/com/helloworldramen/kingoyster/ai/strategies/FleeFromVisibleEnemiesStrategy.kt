package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.*
import com.helloworldramen.kingoyster.ai.options.PathFartherOption
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.visiblePositions

class FleeFromVisibleEnemiesStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "FLE"
    override val considerations: List<GameAiConsideration> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<GameAiOption> {
        val (context, chaser) = strategyContext
        val visiblePositions = chaser.visiblePositions()
        val enemyAndPosition = visiblePositions.flatMap { visiblePosition ->
            (context.entitiesAt(visiblePosition)?.filter { it.isEnemyOf(chaser) } ?: listOf()).map { enemy ->
                Pair(enemy, visiblePosition)
            }
        }

        return enemyAndPosition.map { (enemy, position) ->
            PathFartherOption(this,
                strategyContext.withTarget(enemy).withPosition(position))
        }
    }
}