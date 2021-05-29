package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.actions.MoveWithImpact
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class MovementPart : Part {

    override fun copy(): Part {
        return MovementPart()
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Move -> partOwner.respondToMove(action)
            is MoveWithImpact -> partOwner.respondToMoveWithImpact(action)
            else -> false
        }
    }

    private fun Entity.respondToMove(action: Move): Boolean {
        val (context, _, position) = action

        if (context.entitiesAt(position)?.any { !canPass(it) } == true) {
            return false
        }

        return context.world.move(this, position)
    }

    private fun Entity.respondToMoveWithImpact(action: MoveWithImpact): Boolean {
        val (context, _, position) = action

        if (context.entitiesAt(position)?.any { !canPass(it) } == true) {
            return false
        }

        if (!context.world.move(this, position)) {
            return false
        }

        // We successfully moved, so apply impact effect.
        position.neighbors().forEach {
            context.world.respondToActions(it, Damage(context, this, 1))
        }

        return true
    }
}