package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.actions.MoveType
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.MoveEvent
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.power
import kotlin.math.roundToInt

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
        val (context, actor, position) = action
        if (actor != this) return false
        val currentPosition = context.positionOf(actor) ?: return false
        val entitiesAtPosition = context.entitiesAt(position) ?: return false

        if (entitiesAtPosition.any { !canPass(it) }) return false
        if (!context.world.move(actor, position)) return false

        EventBus.post(MoveEvent(actor, currentPosition, position, action.type))

        if (action.type == MoveType.Charge) {
            // We successfully moved, so apply impact effect.
            val power = (power() * 1.5).roundToInt()

            // Apply damage to the destination position.
            context.world[position]?.forEach {
                if (it != actor) it.respondToAction(Damage(context, actor, power, DamageType.Bash))
            }

            // And apply damage to neighbors of the destination.
            position.neighbors().forEach {
                context.applyAction(it, Damage(context, actor, power, DamageType.Bash))
            }

            this.respondToAction(Damage(context, actor, 1, DamageType.Special))
        }

        entitiesAtPosition.forEach { otherEntity ->
            otherEntity.trigger(context, actor)
        }

        return true
    }
}