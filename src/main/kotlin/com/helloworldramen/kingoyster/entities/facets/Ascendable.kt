package com.helloworldramen.kingoyster.entities.facets

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.actions.Ascend
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.GameOver
import com.helloworldramen.kingoyster.game.EntityFacet
import com.helloworldramen.kingoyster.game.GameContext
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.utilities.WorldGenerator

object Ascendable : EntityFacet {

    override fun respondToAction(facetOwner: GameEntity, action: Action): Boolean {
        return when(action) {
            is Ascend -> respondToAscend(action)
            else -> false
        }
    }

    private fun respondToAscend(action: Ascend): Boolean {
        val (context, user) = action

        context.worldLevel++

        if (context.worldLevel > GameContext.MAX_WORLD_LEVEL) {
            EventBus.post(GameOver(context))
        }  else {
            WorldGenerator.repopulate(context.world, user, context.world[user])
        }

        return true
    }
}