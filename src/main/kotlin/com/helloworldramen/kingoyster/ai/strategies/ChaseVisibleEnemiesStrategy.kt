package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.PathCloserOption
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.visiblePositions

class ChaseVisibleEnemiesStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "CHS"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, chaser) = strategyContext
        val visiblePositions = chaser.visiblePositions()
        val enemyAndPosition = visiblePositions.flatMap { visiblePosition ->
            (context.entitiesAt(visiblePosition)?.filter { it.isEnemyOf(chaser) } ?: listOf()).map { enemy ->
                Pair(enemy, visiblePosition)
            }
        }

        return enemyAndPosition.map { (enemy, position) ->
            PathCloserOption(this,
                strategyContext.withTarget(enemy).withPosition(position))
        }
    }
}