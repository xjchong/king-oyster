package com.helloworldramen.kingoyster.parts.combat.statuseffects

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.resFactor

class BurnStatusEffect(val potency: Int, val baseChance: Double, override var turnsRemaining: Int) : StatusEffect() {

    override fun applyChance(context: Context, target: Entity): Double {
        return baseChance * target.resFactor(DamageType.Special, ElementType.Fire)
    }

    override fun onTick(context: Context, owner: Entity) {
        owner.respondToAction(Damage(context, owner, potency, DamageType.Special, ElementType.Fire))
    }
}