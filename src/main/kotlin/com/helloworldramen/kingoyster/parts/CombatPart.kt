package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.WeaponAttack
import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.entities.isPlayer
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.DeathEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import kotlin.math.roundToInt

class CombatPart(
    var maxHealth: Int,
    var maxMana: Int,
    var power: Int,
    var defaultAttackInfo: AttackInfo,
    var health: Int = maxHealth,
    var mana: Int = maxMana,
) : Part {

    override fun copy(): Part {
        return CombatPart(maxHealth, maxMana, power, defaultAttackInfo, health, mana)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is WeaponAttack -> partOwner.respondToWeaponAttack(action)
            is Damage -> partOwner.respondToDamage(action)
            else -> false
        }
    }

    private fun Entity.respondToWeaponAttack(action: WeaponAttack): Boolean {
        val (context, attacker) = action
        val attackInfo = attacker.equippedWeaponPart()?.attackInfo ?: attacker.defaultAttackInfo()
        val rawAmount = attacker.power() * attackInfo.powerFactor
        val resFactor = resFactor(attackInfo)
        val finalAmount = (rawAmount * resFactor).roundToInt()
        val damage = Damage(context, attacker, finalAmount)

        return respondToDamage(damage)
    }

    private fun Entity.respondToDamage(action: Damage): Boolean {
        val (context, source, amount) = action

        if (this == source) return false

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

sealed class DamageType {
    object Cut : DamageType()
    object Bash : DamageType()
    object Stab : DamageType()
    object Special : DamageType()
}

sealed class ElementType {
    object Fire : ElementType()
    object Ice : ElementType()
    object Volt : ElementType()
    object None : ElementType()
}

data class AttackInfo(
    val damageInfo: DamageInfo = DamageInfo(),
    val timeFactor: Double = 1.0
) {

    val powerFactor = damageInfo.powerFactor
    val damageType = damageInfo.damageType
    val elementType = damageInfo.elementType
}

data class DamageInfo(
    val powerFactor: Double = 1.0,
    val damageType: DamageType = DamageType.Special,
    val elementType: ElementType = ElementType.None
)

fun Entity.maxHealth(): Int = find<CombatPart>()?.maxHealth ?: 0
fun Entity.health(): Int = find<CombatPart>()?.health ?: 0
fun Entity.maxMana(): Int = find<CombatPart>()?.maxMana ?: 0
fun Entity.mana(): Int = find<CombatPart>()?.mana ?: 0
fun Entity.power(): Int = find<CombatPart>()?.power ?: 0
fun Entity.defaultAttackInfo(): AttackInfo = find<CombatPart>()?.defaultAttackInfo ?: AttackInfo(DamageInfo())
