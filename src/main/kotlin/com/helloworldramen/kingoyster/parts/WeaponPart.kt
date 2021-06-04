package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.DamageWeapon
import com.helloworldramen.kingoyster.actions.DropWeapon
import com.helloworldramen.kingoyster.actions.Take
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DamageWeaponEvent
import com.helloworldramen.kingoyster.eventbus.events.EquipWeaponEvent

class WeaponPart(
    val attackInfo: AttackInfo,
    var maxDurability: Int,
    var durability: Int = maxDurability
) : Part {

    override fun copy(): Part {
        return WeaponPart(attackInfo, maxDurability, durability)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is DamageWeapon -> partOwner.respondToDamageWeapon(action)
            is Take -> partOwner.respondToTake(action)
            else -> false
        }
    }

    private fun Entity.respondToTake(action: Take): Boolean {
        val (context, actor) = action
        val equipment = actor.find<EquipmentPart>() ?: return false

        // Remove the weapon from the floor.
        if (!context.world.move(this, null)) return false

        // Drop the current weapon if any.
        actor.respondToAction(DropWeapon(context, actor))

        // Equip this weapon.
        equipment.weapon = this

        EventBus.post(EquipWeaponEvent(actor, this))

        return true
    }

    private fun Entity.respondToDamageWeapon(action: DamageWeapon): Boolean {
        val (context, _, owner, amount) = action

        durability -= amount

        EventBus.post(DamageWeaponEvent(this, owner, amount, durability <= 0))

        if (durability <= 0) {
            context.world.remove(this)
            owner?.find<EquipmentPart>()?.weapon = null
        }

        return true
    }

    companion object {
        const val BREAK_POWER_FACTOR = 3.0
    }
}

fun Entity.durability(): Int {
    return find<WeaponPart>()?.durability ?: 0
}