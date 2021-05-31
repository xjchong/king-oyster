package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Attack
import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.AttackEvent
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class AttackablePart : Part {

    override fun copy(): Part {
        return AttackablePart()
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Attack -> partOwner.respondToAttack(action)
            else -> false
        }
    }

    private fun Entity.respondToAttack(action: Attack): Boolean {
        val (context, attacker) = action
        val amount = attacker.power()

        // Don't allow entities with the same faction to hurt each other... for now.
        if (faction() == attacker.faction()) {
            return false
        }

        EventBus.post(AttackEvent(attacker, this))

        respondToAction(Damage(
            context, attacker, amount
        ))

        return true
    }
}