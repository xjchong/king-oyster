package com.helloworldramen.kingoyster.parts.combat

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.BasicAttackPattern
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.NoAttackPattern
import kotlin.math.roundToInt

class CombatPart(
    var maxHealth: Int,
    var maxMana: Int = 0,
    var power: Int = 0,
    var defaultAttackPattern: AttackPattern = NoAttackPattern(),
    health: Int = maxHealth,
    var mana: Int = maxMana,
) : Part {

    var health: Int = health
        private set

    override fun copy(): Part {
        return CombatPart(maxHealth, maxMana, power, defaultAttackPattern, health, mana)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is Damage -> partOwner.respondToDamage(action)
            is Heal ->partOwner.respondToHeal(action)
            is WeaponAttack -> partOwner.respondToWeaponAttack(action)
            else -> false
        }
    }

    private fun modifyHealth(context: Context, partOwner: Entity, amount: Int) {
        if (partOwner.find<CombatPart>() != this) return

        health = (health + amount).coerceAtMost(maxHealth)

        if (health <= 0) {
            val currentPosition = context.positionOf(partOwner) ?: return

            partOwner.respondToAction(DropWeapon(context, partOwner))
            partOwner.respondToAction(DropItem(context, partOwner))

            EventBus.post(DeathEvent(currentPosition, partOwner))
            context.world.remove(partOwner)

            if (partOwner.isPlayer) {
                EventBus.post(GameOverEvent(false))
            }
        }
    }

    private fun Entity.respondToWeaponAttack(action: WeaponAttack): Boolean {
        val (context, actor, direction) = action
        if (this != actor) return false

        val weapon = weapon()
        val attackPattern = weapon?.weaponAttackPattern() ?: defaultAttackPattern()

        if (!attackPattern.isUsable(context, this, direction)) return false

        attackPattern.beforeEffect(context, this, direction)

        val damageForPosition = attackPattern.calculateDamageForPosition(context, this, direction)

        weapon()?.respondToAction(DamageWeapon(context, this, this, 1))

        val breakFactor = if (weapon != null && weapon.durability() <= 0) 2.0 else 1.0

        EventBus.post(WeaponAttackEvent(this, direction, damageForPosition.keys))

        damageForPosition.forEach { (position, damageInfo) ->
            val amount = (power() * damageInfo.powerFactor * breakFactor).roundToInt()

            context.world.respondToActions(position,
                Damage(context, this, amount, damageInfo.damageType, damageInfo.elementType)
            )
        }

        attackPattern.afterEffect(context, this, direction)

        return true
    }

    private fun Entity.respondToDamage(action: Damage): Boolean {
        val (context, source, amount, damageType, elementType) = action
        val finalAmount = (resFactor(damageType, elementType) * amount).roundToInt()

        EventBus.post(DamageEvent(source, this, finalAmount))
        modifyHealth(context, this, -finalAmount)

        return true
    }

    private fun Entity.respondToHeal(action: Heal): Boolean {
        val (context, source, amount) = action

        EventBus.post(HealEvent(source, this, amount))
        modifyHealth(context, this, amount)

        return true
    }
}

fun Entity.maxHealth(): Int = find<CombatPart>()?.maxHealth ?: 0
fun Entity.health(): Int = find<CombatPart>()?.health ?: 0
fun Entity.maxMana(): Int = find<CombatPart>()?.maxMana ?: 0
fun Entity.mana(): Int = find<CombatPart>()?.mana ?: 0
fun Entity.power(): Int = find<CombatPart>()?.power ?: 0
fun Entity.defaultAttackPattern(): AttackPattern {
    return find<CombatPart>()?.defaultAttackPattern ?: BasicAttackPattern()
}
