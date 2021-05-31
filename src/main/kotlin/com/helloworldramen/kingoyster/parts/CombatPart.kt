package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.entities.isPlayer
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.DeathEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent

class CombatPart(
    var maxHealth: Int,
    var maxMana: Int,
    var power: Int,
    var health: Int = maxHealth,
    var mana: Int = maxMana

) : Part {

    override fun copy(): Part {
        return CombatPart(maxHealth, maxMana, power, health, mana)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is Damage -> partOwner.respondToDamage(action)
            else -> false
        }
    }

    private fun Entity.respondToDamage(action: Damage): Boolean {
        val (context, source, amount) = action
        val currentPosition = context.positionOf(this) ?: return false

        health -= amount
        EventBus.post(DamageEvent(currentPosition, source, this, amount))

        if (health <= 0) {
            EventBus.post(DeathEvent(currentPosition, this))
            context.world.remove(this)

            if (isPlayer) {
                EventBus.post(GameOverEvent(false))
            }
        }

        return true
    }
}

fun Entity.maxHealth(): Int = find<CombatPart>()?.maxHealth ?: 0
fun Entity.health(): Int = find<CombatPart>()?.health ?: 0
fun Entity.maxMana(): Int = find<CombatPart>()?.maxMana ?: 0
fun Entity.mana(): Int = find<CombatPart>()?.mana ?: 0
fun Entity.power(): Int = find<CombatPart>()?.power ?: 0
