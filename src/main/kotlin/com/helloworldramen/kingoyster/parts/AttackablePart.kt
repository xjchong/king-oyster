package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Attack
import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

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
        val amount = attacker.find(CombatPart::class)?.attackPotency ?: return false

        // Don't allow entities with the same faction to hurt each other... for now.
        if (find(FactionPart::class)?.faction == attacker.find(FactionPart::class)?.faction) {
            return false
        }

        respondToAction(Damage(
            context, attacker, amount
        ))

        return true
    }
}