package com.helloworldramen.kingoyster.parts.combat.statuseffects

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.resFactor

class ColdStatusEffect(val baseChance: Double, override var turnsRemaining: Int) : StatusEffect() {

    override fun applyChance(context: Context, target: Entity): Double {
        return baseChance * target.resFactor(DamageType.Special, ElementType.Ice)
    }

    override fun onApply(context: Context, owner: Entity) {
        owner.timeFactor *= 2
    }

    override fun onExpire(context: Context, owner: Entity) {
        owner.timeFactor *= 0.5
    }
}