package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.PathOption
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.visiblePositions

class ChaseVisibleEnemiesStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy(considerations.toList()) {

    override val tag: String = "CHS"

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, chaser) = strategyContext
        val visiblePositions = chaser.visiblePositions()
        val enemyPositions = visiblePositions.filter { visiblePosition ->
            context.entitiesAt(visiblePosition)?.any { it.isEnemyOf(chaser) } == true
        }

        return enemyPositions.map {
            PathOption(this, strategyContext.withPosition(it))
        }
    }
}