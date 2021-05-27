package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.isPassable

class PathOption(optionContext: GameAiOptionContext) : GameAiOption(optionContext) {

    override fun execute(): Boolean {
        val (context, entity, _, position) = optionContext

        if (position == null) return false

        val currentPosition = context.positionOf(entity) ?: return false
        val passableNeighbors = currentPosition.neighborsShuffled().filter { neighbor ->
            context.entitiesAt(neighbor)?.all { it.isPassable() } == true
        }

        return passableNeighbors.sortedBy { it.distanceFrom(position) }.any { greedyPosition ->
            entity.respondToAction(Move(context, entity, greedyPosition))
        }
    }
}