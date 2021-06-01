package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.DropWeapon
import com.helloworldramen.kingoyster.actions.ThrowWeapon
import com.helloworldramen.kingoyster.actions.WeaponAttack
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DropWeaponEvent
import com.helloworldramen.kingoyster.eventbus.events.ThrowWeaponEvent

class EquipmentPart(
    var weapon: Entity? = null
) : Part {

    override fun copy(): Part {
        return EquipmentPart(weapon = weapon?.copy())
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is DropWeapon -> partOwner.respondToDropWeapon(action)
            is ThrowWeapon -> partOwner.respondToThrowWeapon(action)
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

    private fun Entity.respondToThrowWeapon(action: ThrowWeapon): Boolean {
        val (context, _, direction) = action
        val weapon = weapon ?: return false
        val currentPosition = context.positionOf(this) ?: return false
        val nearestImpassablePosition = context.nearestWhere(currentPosition, direction) { entities ->
            entities?.any { !it.isPassable() } == true
        }

        // Remove the weapon from inventory.
        this@EquipmentPart.weapon = null

        if (context.entitiesAt(nearestImpassablePosition)?.any { it.has<CombatPart>() } == true) {
            EventBus.post(ThrowWeaponEvent(this, weapon, currentPosition, nearestImpassablePosition, true))

            // Damage the entity at the destination.
            context.world.respondToActions(nearestImpassablePosition, WeaponAttack(context, this))

            // Remove the weapon from the world.
            context.world.remove(weapon)
        } else {
            val furthestPassablePosition = nearestImpassablePosition - direction.vector

            EventBus.post(ThrowWeaponEvent(this, weapon, currentPosition, furthestPassablePosition, false))

            // Drop the weapon at the destination.
            context.world.move(weapon, furthestPassablePosition)
        }

        return true
    }
}

fun Entity.equippedWeaponPart(): WeaponPart? {
    return find<EquipmentPart>()?.weapon?.find()
}