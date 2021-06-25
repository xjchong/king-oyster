package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DropWeaponEvent
import com.helloworldramen.kingoyster.eventbus.events.ThrowWeaponEvent
import com.helloworldramen.kingoyster.eventbus.events.WeaponAttackEvent
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.defaultAttackPattern
import com.helloworldramen.kingoyster.parts.combat.power
import kotlin.math.roundToInt

class WeaponSlotPart(
    var weapon: Entity? = null
) : Part {

    override fun copy(): Part {
        return WeaponSlotPart(weapon = weapon?.copy())
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is DropWeapon -> partOwner.respondToDropWeapon(action)
            is ThrowWeapon -> partOwner.respondToThrowWeapon(action)
            is WeaponAttack -> partOwner.respondToWeaponAttack(action)
            else -> false
        }
    }

    private fun Entity.respondToDropWeapon(action: DropWeapon): Boolean {
        val (context, actor) = action

        if (this != actor) return false

        val currentPosition = context.positionOf(actor) ?: return false
        val weapon = weapon ?: return false

        if (!context.world.move(weapon, context.findDropPosition(currentPosition))) return false

        this@WeaponSlotPart.weapon = null

        EventBus.post(DropWeaponEvent(actor, weapon))

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
        this@WeaponSlotPart.weapon = null

        if (context.entitiesAt(nearestImpassablePosition).any { it.has<CombatPart>() }) {
            EventBus.post(ThrowWeaponEvent(this, weapon, currentPosition, nearestImpassablePosition))

            // Drop the weapon at the destination.
            context.world.move(weapon, context.findDropPosition(nearestImpassablePosition))
            weapon.respondToAction(DamageWeapon(context, thrower, null, THROW_DURABILITY_LOSS))

            val throwInfo = weapon.throwInfo()
            val breakFactor = if (weapon.durability() <= 0) WeaponPart.BREAK_FACTOR else 1.0
            val rawAmount = (power() * throwInfo.powerFactor * breakFactor).roundToInt()

            // Damage the entity at the destination.
            context.tryActions(nearestImpassablePosition,
                Damage(context, this, rawAmount, throwInfo.damageType, throwInfo.elementType)
            )
        } else {
            val furthestPassablePosition = nearestImpassablePosition - direction.vector

            EventBus.post(ThrowWeaponEvent(this, weapon, currentPosition, furthestPassablePosition))

            // Drop the weapon at the destination.
            context.world.move(weapon, context.findDropPosition(furthestPassablePosition))
        }

        return true
    }

    private fun Entity.respondToWeaponAttack(action: WeaponAttack): Boolean {
        val (context, actor, direction) = action
        if (this != actor) return false

        val weapon = weapon()
        val attackPattern = weapon?.weaponAttackPattern() ?: defaultAttackPattern()

        if (!attackPattern.isUsable(context, this, direction)) return false

        weapon()?.respondToAction(DamageWeapon(context, this, this, 1))

        val breakFactor = if (weapon != null && weapon.durability() <= 0) 2.0 else 1.0
        val power = (power() * breakFactor).roundToInt()

        return if (attackPattern.execute(context, actor, direction, power)) {
            EventBus.post(WeaponAttackEvent(this, direction))

            true
        } else false
    }

    companion object {
        private const val THROW_DURABILITY_LOSS = 2
    }
}

fun Entity.weapon(): Entity? {
    return find<WeaponSlotPart>()?.weapon
}