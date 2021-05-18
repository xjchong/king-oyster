package com.helloworldramen.kingoyster.entities.facets

import com.helloworldramen.kingoyster.entities.actions.Move
import com.helloworldramen.kingoyster.game.GameContext
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Facet

object Movable : Facet<GameContext> {

    private const val DEFAULT_MOVE_TIME = 100

    override fun respondToAction(context: GameContext, action: Action): Boolean {
        return if (action is Move) {
            val (entity, position) = action

            context.world.move(entity, position)
            entity.nextUpdateTime += DEFAULT_MOVE_TIME

            true
        } else false
    }
}