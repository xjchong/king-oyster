package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.DropWeapon
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DropWeaponEvent

class EquipmentPart(
    var weapon: Entity? = null
) : Part {

    override fun copy(): Part {
        return EquipmentPart(weapon = weapon?.copy())
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is DropWeapon -> partOwner.respondToDropWeapon(action)
            else -> false
        }
    }

    private fun Entity.respondToDropWeapon(action: DropWeapon): Boolean {
        val (context, _) = action
        val currentPosition = context.positionOf(this) ?: return false
        val weapon = weapon ?: return false

        if (!context.world.move(weapon, currentPosition)) return false

        this@EquipmentPart.weapon = null

        EventBus.post(DropWeaponEvent(this, weapon))

        return true
    }
}

fun Entity.equippedWeaponPart(): WeaponPart? {
    return find<EquipmentPart>()?.weapon?.find()
}