package com.helloworldramen.kingoyster.parts.combat

import com.helloworldramen.kingoyster.actions.*
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.AttackPattern
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.BasicAttackPattern
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.NoAttackPattern
import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect
import kotlin.math.roundToInt
import kotlin.random.Random

class CombatPart(
    var maxHealth: Int,
    var maxMana: Int = 0,
    var power: Int = 0,
    var defaultAttackPattern: AttackPattern = NoAttackPattern(),
    health: Int = maxHealth,
    mana: Int = maxMana,
    statusEffects: List<StatusEffect> = listOf()
) : Part {

    var health: Int = health
        private set

    var mana: Int = mana
        private set

    var statusEffects: List<StatusEffect> = statusEffects
        private set

    override fun copy(): Part {
        return CombatPart(maxHealth, maxMana, power, defaultAttackPattern, health, mana, statusEffects.toList())
    }

    override fun update(context: Context, partOwner: Entity) {
        statusEffects.forEach {
            it.onTick(context, partOwner)
            it.turnsRemaining--
        }

        val (expiredEffects, runningEffects) = statusEffects.partition {
            it.turnsRemaining <= 0
        }

        statusEffects = runningEffects

        expiredEffects.forEach {
            it.onExpire(context, partOwner)
        }

        if (expiredEffects.isNotEmpty()) {
            EventBus.post(StatusExpiredEvent(partOwner))
        }
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is Damage -> partOwner.respondToDamage(action)
            is Heal ->partOwner.respondToHeal(action)
            is ReceiveStatusEffect -> partOwner.respondToReceiveStatusEffect(action)
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

    private fun Entity.respondToDamage(action: Damage): Boolean {
        val (context, source, amount, damageType, elementType, statusEffect) = action
        val finalAmount = (resFactor(damageType, elementType) * amount).roundToInt()

        EventBus.post(DamageEntityEvent(source, this, finalAmount, damageType, elementType))
        modifyHealth(context, this, -finalAmount)

        if (statusEffect != null && health() > 0) {
            if (Random.nextDouble(1.0) < statusEffect.applyChance(context, this)) {
                respondToReceiveStatusEffect(
                    ReceiveStatusEffect(context, this, source, statusEffect)
                )
            }
        }

        return true
    }

    private fun Entity.respondToHeal(action: Heal): Boolean {
        val (context, source, amount) = action

        EventBus.post(HealEvent(source, this, amount))
        modifyHealth(context, this, amount)

        return true
    }

    private fun Entity.respondToReceiveStatusEffect(action: ReceiveStatusEffect): Boolean {
        val (context, actor, _, effect) = action

        if (actor != this) return false

        statusEffects = listOf(effect)

        effect.onApply(context, actor)
        EventBus.post(StatusAppliedEvent(this))

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
fun Entity.isKillable(): Boolean = has<CombatPart>()
fun Entity.statusEffects(): List<StatusEffect> = find<CombatPart>()?.statusEffects ?: listOf()
