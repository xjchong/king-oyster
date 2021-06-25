package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.actions.MoveAttack
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.MoveAttackEvent
import com.helloworldramen.kingoyster.eventbus.events.MoveEvent
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.AttackPattern
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.NoAttackPattern
import com.helloworldramen.kingoyster.parts.combat.power

class MovementPart(
    val attackPattern: AttackPattern = NoAttackPattern()
) : Part {

    override fun copy(): Part {
        return MovementPart(attackPattern)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Move -> partOwner.respondToMove(action)
            is MoveAttack -> partOwner.respondToMoveAttack(action)
            else -> false
        }
    }

    private fun Entity.respondToMove(action: Move): Boolean {
        val (context, actor, position) = action
        if (actor != this) return false
        val currentPosition = context.positionOf(actor) ?: return false
        val entitiesAtPosition = context.entitiesAt(position)

        if (entitiesAtPosition.any { !canPass(it) }) return false
        if (!context.world.move(actor, position)) return false

        EventBus.post(MoveEvent(actor, currentPosition, position))

        entitiesAtPosition.forEach { otherEntity ->
            otherEntity.trigger(context, actor)
        }

        return true
    }

    private fun Entity.respondToMoveAttack(action: MoveAttack): Boolean {
        val (context, actor, direction) = action
        val startPosition = context.positionOf(actor) ?: return false

        if (this != actor) return false

        return if (attackPattern.execute(context, actor, direction, power())) {
            val endPosition = context.positionOf(actor)

            EventBus.post(MoveAttackEvent(actor, direction, startPosition, endPosition))

            true
        } else false
    }
}