package com.helloworldramen.kingoyster.ai.strategies

import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.ai.GameAiStrategyContext
import com.helloworldramen.kingoyster.ai.architecture.AiConsideration
import com.helloworldramen.kingoyster.ai.architecture.AiOption
import com.helloworldramen.kingoyster.ai.options.TakeWeaponOption
import com.helloworldramen.kingoyster.ai.options.PathCloserOption
import com.helloworldramen.kingoyster.parts.WeaponPart
import com.helloworldramen.kingoyster.parts.visiblePositions

class TakeWeaponStrategy(vararg considerations: GameAiConsideration) : GameAiStrategy() {

    override val tag: String = "EQW"
    override val considerations: List<AiConsideration<GameAiOptionContext>> = considerations.toList()

    override fun listOptions(strategyContext: GameAiStrategyContext): List<AiOption<GameAiOptionContext>> {
        val (context, actor) = strategyContext
        val actorPosition = context.positionOf(actor)
        val visiblePositions = actor.visiblePositions()
        val weaponAndPosition = visiblePositions.flatMap { visiblePosition ->
            context.entitiesAt(visiblePosition).filter { it.has<WeaponPart>() }.map { weapon ->
                Pair(weapon, visiblePosition)
            }
        }

        return weaponAndPosition.map { (weapon, position) ->
            if (position == actorPosition) {
                TakeWeaponOption(this, strategyContext.withTarget(weapon))
            } else {
                PathCloserOption(this, strategyContext.withTarget(weapon).withPosition(position))
            }
        }
    }
}