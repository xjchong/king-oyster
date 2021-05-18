package com.helloworldramen.kingoyster.entities.facets

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.actions.Move
import com.helloworldramen.kingoyster.game.GameContext
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Facet

object Movable : Facet<GameContext> {

    private const val DEFAULT_MOVE_TIME = 100

    override fun respondToAction(facetOwner: GameEntity, action: Action): Boolean {
        return when (action) {
            is Move -> respondToMove(facetOwner, action)
            else -> false
        }
    }

    private fun respondToMove(facetOwner: GameEntity, action: Move): Boolean {
        val (context, position) = action

        context.world.move(facetOwner, position)
        facetOwner.nextUpdateTime += DEFAULT_MOVE_TIME

        return true
    }
}