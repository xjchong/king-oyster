package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.WeaponAttack
import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.DamageWeapon
import com.helloworldramen.kingoyster.actions.DropWeapon
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.DeathEvent
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.eventbus.events.WeaponAttackEvent
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.resFactor
import kotlin.math.roundToInt

class CombatPart(
    var maxHealth: Int,
    var maxMana: Int,
    var power: Int,
    var defaultDamageInfo: DamageInfo,
    var health: Int = maxHealth,
    var mana: Int = maxMana,
) : Part {

    override fun copy(): Part {
        return CombatPart(maxHealth, maxMana, power, defaultDamageInfo, health, mana)
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

        if (!isEnemyOf(attacker)) return false

        val weapon = attacker.weapon()
        val damageInfo = attacker.equippedWeaponPart()?.damageInfo ?: attacker.defaultDamageInfo()
        val rawAmount = attacker.power() * damageInfo.powerFactor
        val damage = Damage(context, attacker, rawAmount.roundToInt(), damageInfo.damageType, damageInfo.elementType)

        return if (respondToDamage(damage)) {
            EventBus.post(WeaponAttackEvent(attacker, this))

            weapon?.respondToAction(DamageWeapon(context, attacker, attacker, 1))

            true
        } else {
            false
        }
    }

    private fun Entity.respondToDamage(action: Damage): Boolean {
        val (context, source, amount, damageType, elementType) = action
        val currentPosition = context.positionOf(this) ?: return false
        val finalAmount = (resFactor(damageType, elementType) * amount).roundToInt()

        health -= finalAmount
        EventBus.post(DamageEvent(currentPosition, source, this, finalAmount))

        if (health <= 0) {
            respondToAction(DropWeapon(context, this))

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
fun Entity.defaultDamageInfo(): DamageInfo = find<CombatPart>()?.defaultDamageInfo ?: DamageInfo()
