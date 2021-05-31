package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.actions.MoveType
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.MoveEvent

class MovementPart : Part {

    override fun copy(): Part {
        return MovementPart()
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Move -> partOwner.respondToMove(action)
            else -> false
        }
    }

    private fun Entity.respondToMove(action: Move): Boolean {
        val (context, _, position) = action
        val currentPosition = context.positionOf(this) ?: return false

        if (context.entitiesAt(position)?.any { !canPass(it) } == true) {
            return false
        }

        if (!context.world.move(this, position)) {
            return false
        }

        EventBus.post(MoveEvent(this, currentPosition, position, action.type))

        if (action.type == MoveType.Charge) {
            // We successfully moved, so apply impact effect.

            // Apply damage to the destination position.
            context.world[position]?.forEach {
                if (it != this) it.respondToAction(Damage(context, this, 1))
            }

            // And apply damage to neighbors of the destination.
            position.neighbors().forEach {
                context.world.respondToActions(it, Damage(context, this, 1))
            }
        }

        return true
    }
}