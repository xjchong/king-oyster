package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.DropWeapon
import com.helloworldramen.kingoyster.actions.EquipAsWeapon
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.EquipWeaponEvent

class WeaponPart(
    val attackInfo: AttackInfo
) : Part {

    override fun copy(): Part {
        return WeaponPart(attackInfo)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is EquipAsWeapon -> partOwner.respondToEquipAsWeapon(action)
            else -> false
        }
    }

    private fun Entity.respondToEquipAsWeapon(action: EquipAsWeapon): Boolean {
        val (context, actor) = action
        val equipment = actor.find<EquipmentPart>() ?: return false

        println("attempt to remove weapon")
        // Remove the weapon from the floor.
        if (!context.world.move(this, null)) return false
        println("removing weapon was a success")

        // Drop the current weapon if any.
        actor.respondToAction(DropWeapon(context, actor))

        // Equip this weapon.
        equipment.weapon = this

        EventBus.post(EquipWeaponEvent(actor, this))

        return true
    }
}