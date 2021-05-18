package com.helloworldramen.kingoyster.entities.facets

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.actions.Ascend
import com.helloworldramen.kingoyster.game.EntityFacet
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

        WorldGenerator.repopulate(context.world, user, context.world[user])

        return true
    }
}