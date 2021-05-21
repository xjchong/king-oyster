package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class AIPart : Part {

    override fun copy(): Part {
        return AIPart()
    }

    override fun update(context: Context, partOwner: Entity) {
        // Wander around randomly.
        val currentPosition = context.world[partOwner] ?: return

        partOwner.respondToAction(Move(context, partOwner, currentPosition.neighborsShuffled().first()))
    }
}