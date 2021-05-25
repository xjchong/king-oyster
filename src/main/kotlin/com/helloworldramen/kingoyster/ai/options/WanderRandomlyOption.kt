package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.parts.PhysicalPart

class WanderRandomlyOption(vararg considerations: GameAiConsideration) : GameAiOption(considerations.toList()) {

    override fun execute(aiContext: GameAiContext): Boolean {
        val (context, entity) = aiContext
        val currentPosition = context.world[entity] ?: return false
        val passableNeighbors = currentPosition.neighbors().filter { neighbor ->
            context.world[neighbor]?.none { it.find(PhysicalPart::class)?.isPassable == false } == true
        }

        return if (passableNeighbors.isEmpty()) {
            false
        } else {
            entity.respondToAction(Move(context, entity, passableNeighbors.random()))
        }
    }
}