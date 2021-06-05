package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.DamageWeapon
import com.helloworldramen.kingoyster.actions.DropWeapon
import com.helloworldramen.kingoyster.actions.ThrowWeapon
import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DropWeaponEvent
import com.helloworldramen.kingoyster.eventbus.events.ThrowWeaponEvent
import kotlin.math.roundToInt

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

        if (!context.world.move(weapon, currentPosition.findUnoccupiedPosition(context))) return false

        this@EquipmentPart.weapon = null

        EventBus.post(DropWeaponEvent(this, weapon))

        return true
    }

    private fun Entity.respondToThrowWeapon(action: ThrowWeapon): Boolean {
        val (context, thrower, direction) = action
        val weapon = weapon ?: return false
        val currentPosition = context.positionOf(this) ?: return false
        val nearestImpassablePosition = context.straightPathUntil(currentPosition, direction) { position ->
            val entities = context.entitiesAt(position)

            entities == null || (entities.any { !it.isPassable() } && !entities.contains(this))
        }.lastOrNull() ?: return false

        // Remove the weapon from inventory.
        this@EquipmentPart.weapon = null

        if (context.entitiesAt(nearestImpassablePosition)?.any { it.has<CombatPart>() } == true) {
            EventBus.post(ThrowWeaponEvent(this, weapon, currentPosition, nearestImpassablePosition))

            // Drop the weapon at the destination.
            context.world.move(weapon, nearestImpassablePosition.findUnoccupiedPosition(context))
            weapon.respondToAction(DamageWeapon(context, thrower, null, THROW_DURABILITY_LOSS))

            val damageInfo = weapon.find<WeaponPart>()?.damageInfo ?: defaultDamageInfo()
            val rawAmount = (power() * damageInfo.powerFactor * THROW_POWER_FACTOR).roundToInt() // Throwing gets a power multiplier.

            // Damage the entity at the destination.
            context.world.respondToActions(nearestImpassablePosition,
                Damage(context, this, rawAmount, damageInfo.damageType, damageInfo.elementType)
            )
        } else {
            val furthestPassablePosition = nearestImpassablePosition - direction.vector

            EventBus.post(ThrowWeaponEvent(this, weapon, currentPosition, furthestPassablePosition))

            // Drop the weapon at the destination.
            context.world.move(weapon, furthestPassablePosition.findUnoccupiedPosition(context))
        }

        return true
    }

    private fun Position.findUnoccupiedPosition(context: Context): Position {
        if (!isOccupied(context)) {
            return this
        }

        val unoccupiedAdjacentNeighbor = neighborsShuffled().firstOrNull {
            !it.isOccupied(context)
        }

        if (unoccupiedAdjacentNeighbor != null) return unoccupiedAdjacentNeighbor

        val unoccupiedDiagonalNeighbor = listOf(
            this.withRelative(1, -1),
            this.withRelative(1, 1),
            this.withRelative(-1, 1),
            this.withRelative(-1, -1)
        ).shuffled().firstOrNull {
            !it.isOccupied(context)
        }

        return unoccupiedDiagonalNeighbor ?: this
    }

    private fun Position.isOccupied(context: Context): Boolean {
        return context.entitiesAt(this)?.any {
            it.has<WeaponPart>() || it.has<ItemPart>() || (!it.isPassable() && !it.has<MovementPart>())
        } != false
    }

    companion object {
        private const val THROW_POWER_FACTOR = 1.8
        private const val THROW_DURABILITY_LOSS = 3
    }
}

fun Entity.weapon(): Entity? {
    return find<EquipmentPart>()?.weapon
}

fun Entity.equippedWeaponPart(): WeaponPart? {
    return weapon()?.find()
}