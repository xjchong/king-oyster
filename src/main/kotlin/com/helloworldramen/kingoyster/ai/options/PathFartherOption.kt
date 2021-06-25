package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext
import com.helloworldramen.kingoyster.ai.tag
import com.helloworldramen.kingoyster.utilities.pathing.Pathing

class PathFartherOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "ptc.${optionContext.position.tag()}"

    override fun execute(): Boolean {
        val (context, entity, _, position) = optionContext

        if (position == null) return false

//        val currentPosition = context.positionOf(entity) ?: return false
        val nextPosition = Pathing.pathAway(context, entity, position) ?: return false

        return entity.respondToAction(Move(context, entity, nextPosition))
//
//        val passableNeighbors = currentPosition.neighborsShuffled().filter { neighbor ->
//            context.entitiesAt(neighbor)?.all { entity.canPass(it) } == true
//        }
//
//        return passableNeighbors.sortedByDescending { it.distanceFrom(position) }.any { greedyPosition ->
//            entity.respondToAction(Move(context, entity, greedyPosition))
//        }
    }
}