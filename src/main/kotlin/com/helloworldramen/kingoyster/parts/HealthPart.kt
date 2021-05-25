package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.DeathEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class HealthPart(var maxHealth: Int, var health: Int = maxHealth) : Part {

    override fun copy(): Part {
        return HealthPart(maxHealth, health)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Damage -> partOwner.respondToDamage(action)
            else -> false
        }
    }

    private fun Entity.respondToDamage(action: Damage): Boolean {
        val (context, _, amount) = action
        val currentPosition = context.world[this] ?: return false

        EventBus.post(DamageEvent(currentPosition, this, amount))
        health -= amount

        if (health <= 0) {
            EventBus.post(DeathEvent(currentPosition, this))
            context.world.remove(this)

            if (this.name == "player") {
                EventBus.post(GameOverEvent(false))
            }
        }

        return true
    }
}